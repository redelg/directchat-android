#!/usr/bin/env node

import fs from 'node:fs/promises';
import path from 'node:path';
import process from 'node:process';
import { GoogleAuth } from 'google-auth-library';

const DEFAULTS = {
  packageName: 'com.devmo.freeinternet',
  report: 'tools/firestore-audit/output/play_listings_upsert_report.json',
  dryRun: false,
  changesNotSentForReview: true,
};

const TITLE_MAX = 30;
const SHORT_MAX = 80;
const FULL_MAX = 4000;

const BLOCKED_TERMS = [
  /\broot\b/i,
  /\bjailbreak\b/i,
  /\bhack(e[ao]r|ing)?\b/i,
  /\bcrack(ead[oa]?|ing)?\b/i,
  /\bbypass\b/i,
  /\bcircumvent\b/i,
  /\bgratis ilimitad[oa]?\b/i,
  /\binternet gratis ilimitad[oa]?\b/i,
  /\bfree unlimited internet\b/i,
  /\bwifi (password|senha|hack)\b/i,
];

function printUsage() {
  console.log(`
Upsert Play Store Listings

Uso:
  node tools/firestore-audit/upsert-play-listings.mjs \\
    --service-account /ruta/play-service-account.json \\
    --file tools/firestore-audit/play-listings-proposed-v1.json \\
    [--package-name com.devmo.freeinternet] \\
    [--report tools/firestore-audit/output/play_listings_upsert_report.json] \\
    [--dry-run] \\
    [--send-for-review]
`);
}

function parseArgs(argv) {
  const args = { ...DEFAULTS };
  for (let i = 0; i < argv.length; i += 1) {
    const arg = argv[i];
    if (!arg.startsWith('--')) continue;

    if (arg === '--help' || arg === '-h') {
      args.help = true;
      continue;
    }
    if (arg === '--dry-run') {
      args.dryRun = true;
      continue;
    }
    if (arg === '--send-for-review') {
      args.changesNotSentForReview = false;
      continue;
    }

    const key = arg.slice(2);
    const value = argv[i + 1];
    if (!value || value.startsWith('--')) {
      throw new Error(`Falta valor para ${arg}`);
    }

    if (key === 'service-account') args.serviceAccount = value;
    else if (key === 'file') args.file = value;
    else if (key === 'package-name') args.packageName = value;
    else if (key === 'report') args.report = value;
    else throw new Error(`Parametro no reconocido: ${arg}`);
    i += 1;
  }
  return args;
}

function clean(input) {
  return typeof input === 'string' ? input.trim() : '';
}

function detectBlockedTerms(text) {
  const hits = [];
  for (const rx of BLOCKED_TERMS) {
    if (rx.test(text)) hits.push(rx.source);
  }
  return hits;
}

function normalizePayload(raw) {
  if (!raw || !Array.isArray(raw.listings)) {
    throw new Error('El archivo debe incluir { "listings": [] }');
  }

  const errors = [];
  const listings = raw.listings.map((entry, index) => {
    const language = clean(entry.language);
    const title = clean(entry.title);
    const shortDescription = clean(entry.shortDescription);
    const fullDescription = clean(entry.fullDescription);

    if (!language || !title || !shortDescription || !fullDescription) {
      errors.push({
        type: 'missing_fields',
        index,
        language,
        message: 'language, title, shortDescription y fullDescription son obligatorios',
      });
    }
    if (title.length > TITLE_MAX) {
      errors.push({
        type: 'title_too_long',
        index,
        language,
        max: TITLE_MAX,
        actual: title.length,
      });
    }
    if (shortDescription.length > SHORT_MAX) {
      errors.push({
        type: 'short_description_too_long',
        index,
        language,
        max: SHORT_MAX,
        actual: shortDescription.length,
      });
    }
    if (fullDescription.length > FULL_MAX) {
      errors.push({
        type: 'full_description_too_long',
        index,
        language,
        max: FULL_MAX,
        actual: fullDescription.length,
      });
    }

    const policyHits = detectBlockedTerms(`${title}\n${shortDescription}\n${fullDescription}`);
    if (policyHits.length > 0) {
      errors.push({
        type: 'policy_blocked_terms',
        index,
        language,
        hits: policyHits,
      });
    }

    return {
      language,
      title,
      shortDescription,
      fullDescription,
    };
  });

  return { listings, errors };
}

async function loadJson(filePath) {
  const raw = await fs.readFile(path.resolve(filePath), 'utf8');
  return JSON.parse(raw);
}

async function fetchJson(url, token, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
  });
  const text = await response.text();
  let data;
  try {
    data = text ? JSON.parse(text) : {};
  } catch {
    data = { raw: text };
  }
  if (!response.ok) {
    throw new Error(`HTTP ${response.status} ${response.statusText}: ${JSON.stringify(data)}`);
  }
  return data;
}

function shouldRetryCommitWithoutReviewFlag(error) {
  const message = String(error?.message || '').toLowerCase();
  return (
    message.includes('changes are sent for review automatically') ||
    message.includes('changesnotsentforreview must not be set')
  );
}

async function writeReport(reportPath, report) {
  await fs.mkdir(path.dirname(reportPath), { recursive: true });
  await fs.writeFile(reportPath, `${JSON.stringify(report, null, 2)}\n`, 'utf8');
}

async function main() {
  let args;
  try {
    args = parseArgs(process.argv.slice(2));
  } catch (error) {
    console.error(`❌ ${error.message}`);
    printUsage();
    process.exit(1);
  }

  if (args.help) {
    printUsage();
    return;
  }

  if (!args.serviceAccount || !args.file) {
    console.error('❌ Debes indicar --service-account y --file');
    printUsage();
    process.exit(1);
  }

  const serviceAccountPath = path.resolve(args.serviceAccount);
  const reportPath = path.resolve(args.report);

  let serviceAccount;
  try {
    serviceAccount = JSON.parse(await fs.readFile(serviceAccountPath, 'utf8'));
  } catch (error) {
    console.error(`❌ No se pudo leer service account: ${error.message}`);
    process.exit(1);
  }

  const payload = normalizePayload(await loadJson(args.file));
  if (payload.errors.length > 0) {
    await writeReport(reportPath, {
      generatedAt: new Date().toISOString(),
      dryRun: args.dryRun,
      packageName: args.packageName,
      inputFile: path.resolve(args.file),
      errorCount: payload.errors.length,
      errors: payload.errors,
    });
    console.error(`❌ El archivo no paso validacion (${payload.errors.length} errores)`);
    console.error(`Revisa: ${reportPath}`);
    process.exit(1);
  }

  const auth = new GoogleAuth({
    credentials: serviceAccount,
    scopes: ['https://www.googleapis.com/auth/androidpublisher'],
  });
  const client = await auth.getClient();
  const accessToken = await client.getAccessToken();
  const token = accessToken?.token;
  if (!token) {
    throw new Error('No se pudo obtener access token');
  }

  const baseUrl = `https://androidpublisher.googleapis.com/androidpublisher/v3/applications/${args.packageName}`;
  const edit = await fetchJson(`${baseUrl}/edits`, token, {
    method: 'POST',
    body: JSON.stringify({}),
  });
  const editId = edit.id;

  const changedLanguages = [];
  try {
    for (const listing of payload.listings) {
      await fetchJson(`${baseUrl}/edits/${editId}/listings/${listing.language}`, token, {
        method: 'PUT',
        body: JSON.stringify({
          language: listing.language,
          title: listing.title,
          shortDescription: listing.shortDescription,
          fullDescription: listing.fullDescription,
        }),
      });
      changedLanguages.push(listing.language);
    }

    if (args.dryRun) {
      await fetchJson(`${baseUrl}/edits/${editId}`, token, { method: 'DELETE' });
    } else {
      const commitWithFlagUrl = `${baseUrl}/edits/${editId}:commit?changesNotSentForReview=${args.changesNotSentForReview ? 'true' : 'false'}`;
      try {
        await fetchJson(commitWithFlagUrl, token, {
          method: 'POST',
          body: JSON.stringify({}),
        });
      } catch (error) {
        if (!shouldRetryCommitWithoutReviewFlag(error)) {
          throw error;
        }
        await fetchJson(`${baseUrl}/edits/${editId}:commit`, token, {
          method: 'POST',
          body: JSON.stringify({}),
        });
      }
    }
  } catch (error) {
    await writeReport(reportPath, {
      generatedAt: new Date().toISOString(),
      dryRun: args.dryRun,
      packageName: args.packageName,
      inputFile: path.resolve(args.file),
      editId,
      changedLanguages,
      error: error.message,
    });
    throw error;
  }

  const report = {
    generatedAt: new Date().toISOString(),
    dryRun: args.dryRun,
    packageName: args.packageName,
    inputFile: path.resolve(args.file),
    editId,
    listingsPlanned: payload.listings.length,
    listingsUpdated: changedLanguages.length,
    changedLanguages,
    changesNotSentForReview: args.dryRun ? null : args.changesNotSentForReview,
  };
  await writeReport(reportPath, report);

  console.log(args.dryRun ? '✅ Dry-run completado' : '✅ Fichas actualizadas');
  console.log(`- package: ${args.packageName}`);
  console.log(`- editId: ${editId}`);
  console.log(`- updated: ${changedLanguages.length}`);
  console.log(`- locales: ${changedLanguages.join(', ')}`);
  console.log(`- report: ${reportPath}`);
}

main().catch((error) => {
  console.error(`❌ Error fatal: ${error.stack || error.message}`);
  process.exit(1);
});
