#!/usr/bin/env node

import fs from 'node:fs/promises';
import path from 'node:path';
import process from 'node:process';
import { GoogleAuth } from 'google-auth-library';

const DEFAULTS = {
  packageName: 'com.codergang.chatdirecto',
  report: 'tools/firestore-audit/output/play_graphics_upsert_report.json',
  changesNotSentForReview: true,
};

function printUsage() {
  console.log(`
Upsert Play Graphics (screenshots/icon/feature)

Uso:
  node tools/firestore-audit/upsert-play-graphics.mjs \\
    --service-account /ruta/play-service-account.json \\
    --file tools/firestore-audit/play-graphics-updates.sample.json \\
    [--package-name com.codergang.chatdirecto] \\
    [--report tools/firestore-audit/output/play_graphics_upsert_report.json] \\
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

function mimeTypeFromFile(filePath) {
  const lower = filePath.toLowerCase();
  if (lower.endsWith('.png')) return 'image/png';
  if (lower.endsWith('.jpg') || lower.endsWith('.jpeg')) return 'image/jpeg';
  if (lower.endsWith('.webp')) return 'image/webp';
  return 'application/octet-stream';
}

function shouldRetryCommitWithoutReviewFlag(error) {
  const message = String(error?.message || '').toLowerCase();
  return (
    message.includes('changes are sent for review automatically') ||
    message.includes('changesnotsentforreview must not be set')
  );
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

async function uploadBinary(url, token, filePath) {
  const body = await fs.readFile(filePath);
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': mimeTypeFromFile(filePath),
    },
    body,
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

async function loadConfig(filePath) {
  const raw = await fs.readFile(path.resolve(filePath), 'utf8');
  const json = JSON.parse(raw);
  if (!Array.isArray(json.updates)) {
    throw new Error('El archivo debe contener { \"updates\": [] }');
  }
  for (const [index, item] of json.updates.entries()) {
    if (!item || typeof item !== 'object') {
      throw new Error(`update invalido en index ${index}`);
    }
    if (!item.language || !item.imageType || !Array.isArray(item.files) || item.files.length === 0) {
      throw new Error(
        `update invalido en index ${index}: requiere language, imageType y files[] no vacio`
      );
    }
  }
  return json.updates;
}

async function writeReport(reportPath, payload) {
  await fs.mkdir(path.dirname(reportPath), { recursive: true });
  await fs.writeFile(reportPath, `${JSON.stringify(payload, null, 2)}\n`, 'utf8');
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
  const serviceAccount = JSON.parse(await fs.readFile(serviceAccountPath, 'utf8'));
  const updates = await loadConfig(args.file);

  const auth = new GoogleAuth({
    credentials: serviceAccount,
    scopes: ['https://www.googleapis.com/auth/androidpublisher'],
  });
  const client = await auth.getClient();
  const accessToken = await client.getAccessToken();
  const token = accessToken?.token;
  if (!token) throw new Error('No se pudo obtener access token');

  const baseUrl = `https://androidpublisher.googleapis.com/androidpublisher/v3/applications/${args.packageName}`;
  const uploadBaseUrl = `https://androidpublisher.googleapis.com/upload/androidpublisher/v3/applications/${args.packageName}`;

  const edit = await fetchJson(`${baseUrl}/edits`, token, {
    method: 'POST',
    body: JSON.stringify({}),
  });
  const editId = edit.id;

  const changed = [];

  try {
    for (const update of updates) {
      const language = String(update.language).trim();
      const imageType = String(update.imageType).trim();
      const replace = update.replace !== false;
      const files = update.files.map((f) => path.resolve(f));

      if (replace) {
        await fetchJson(`${baseUrl}/edits/${editId}/listings/${encodeURIComponent(language)}/${imageType}`, token, {
          method: 'DELETE',
        });
      }

      for (const filePath of files) {
        await uploadBinary(
          `${uploadBaseUrl}/edits/${editId}/listings/${encodeURIComponent(language)}/${imageType}?uploadType=media`,
          token,
          filePath
        );
      }

      changed.push({ language, imageType, files, replace });
    }

    const commitWithFlagUrl = `${baseUrl}/edits/${editId}:commit?changesNotSentForReview=${args.changesNotSentForReview ? 'true' : 'false'}`;
    try {
      await fetchJson(commitWithFlagUrl, token, {
        method: 'POST',
        body: JSON.stringify({}),
      });
    } catch (error) {
      if (!shouldRetryCommitWithoutReviewFlag(error)) throw error;
      await fetchJson(`${baseUrl}/edits/${editId}:commit`, token, {
        method: 'POST',
        body: JSON.stringify({}),
      });
    }

    await writeReport(reportPath, {
      generatedAt: new Date().toISOString(),
      packageName: args.packageName,
      editId,
      updatesCount: changed.length,
      changed,
      changesNotSentForReviewRequested: args.changesNotSentForReview,
    });

    console.log('✅ Recursos graficos actualizados en Play');
    console.log(`- package: ${args.packageName}`);
    console.log(`- editId: ${editId}`);
    console.log(`- updates: ${changed.length}`);
    console.log(`- report: ${reportPath}`);
  } catch (error) {
    await writeReport(reportPath, {
      generatedAt: new Date().toISOString(),
      packageName: args.packageName,
      editId,
      error: error.message,
      changed,
    });
    throw error;
  }
}

main().catch((error) => {
  console.error(`❌ Error fatal: ${error.stack || error.message}`);
  process.exit(1);
});
