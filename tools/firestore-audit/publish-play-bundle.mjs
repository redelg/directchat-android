#!/usr/bin/env node

import fs from 'node:fs/promises';
import path from 'node:path';
import process from 'node:process';
import { GoogleAuth } from 'google-auth-library';

const DEFAULTS = {
  packageName: 'com.codergang.chatdirecto',
  track: 'internal',
  status: 'completed',
  report: 'tools/firestore-audit/output/play_bundle_publish_report.json',
  changesNotSentForReview: true,
};

function printUsage() {
  console.log(`
Publish AAB to Play Track

Uso:
  node tools/firestore-audit/publish-play-bundle.mjs \\
    --service-account /ruta/play-service-account.json \\
    --aab /ruta/app-release.aab \\
    [--package-name com.codergang.chatdirecto] \\
    [--track internal|alpha|beta|production] \\
    [--status completed|draft|inProgress|halted] \\
    [--release-name "1.4.3"] \\
    [--release-notes-file tools/firestore-audit/release-notes-v143.json] \\
    [--report tools/firestore-audit/output/play_bundle_publish_report.json] \\
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
    else if (key === 'aab') args.aab = value;
    else if (key === 'package-name') args.packageName = value;
    else if (key === 'track') args.track = value;
    else if (key === 'status') args.status = value;
    else if (key === 'release-name') args.releaseName = value;
    else if (key === 'release-notes-file') args.releaseNotesFile = value;
    else if (key === 'report') args.report = value;
    else throw new Error(`Parametro no reconocido: ${arg}`);
    i += 1;
  }
  return args;
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

async function uploadBundle(url, token, aabBuffer) {
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/octet-stream',
    },
    body: aabBuffer,
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

async function loadReleaseNotes(filePath) {
  if (!filePath) return null;
  const raw = await fs.readFile(path.resolve(filePath), 'utf8');
  const json = JSON.parse(raw);
  if (!Array.isArray(json)) {
    throw new Error('release-notes-file debe ser un array: [{ language, text }]');
  }
  return json.map((it, index) => {
    if (!it || typeof it !== 'object') {
      throw new Error(`release note invalida en index ${index}`);
    }
    const language = String(it.language || '').trim();
    const text = String(it.text || '').trim();
    if (!language || !text) {
      throw new Error(`release note sin language/text en index ${index}`);
    }
    return { language, text };
  });
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
  if (!args.serviceAccount || !args.aab) {
    console.error('❌ Debes indicar --service-account y --aab');
    printUsage();
    process.exit(1);
  }

  const serviceAccountPath = path.resolve(args.serviceAccount);
  const aabPath = path.resolve(args.aab);
  const reportPath = path.resolve(args.report);
  const serviceAccount = JSON.parse(await fs.readFile(serviceAccountPath, 'utf8'));
  const aabBuffer = await fs.readFile(aabPath);
  const releaseNotes = await loadReleaseNotes(args.releaseNotesFile);

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

  try {
    const bundle = await uploadBundle(
      `${uploadBaseUrl}/edits/${editId}/bundles?uploadType=media`,
      token,
      aabBuffer
    );
    const versionCode = String(bundle.versionCode);

    const releasesPayload = {
      track: args.track,
      releases: [
        {
          name: args.releaseName || null,
          status: args.status,
          versionCodes: [versionCode],
          releaseNotes: releaseNotes || undefined,
        },
      ],
    };

    if (!releasesPayload.releases[0].name) {
      delete releasesPayload.releases[0].name;
    }
    if (!releaseNotes) {
      delete releasesPayload.releases[0].releaseNotes;
    }

    await fetchJson(`${baseUrl}/edits/${editId}/tracks/${args.track}`, token, {
      method: 'PUT',
      body: JSON.stringify(releasesPayload),
    });

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

    const report = {
      generatedAt: new Date().toISOString(),
      packageName: args.packageName,
      track: args.track,
      status: args.status,
      editId,
      versionCode,
      releaseName: args.releaseName || null,
      aab: aabPath,
      aabSizeBytes: aabBuffer.length,
      releaseNotesProvided: Boolean(releaseNotes?.length),
      changesNotSentForReviewRequested: args.changesNotSentForReview,
    };
    await writeReport(reportPath, report);

    console.log('✅ Bundle publicado en Play');
    console.log(`- package: ${args.packageName}`);
    console.log(`- track: ${args.track}`);
    console.log(`- versionCode: ${versionCode}`);
    console.log(`- editId: ${editId}`);
    console.log(`- report: ${reportPath}`);
  } catch (error) {
    await writeReport(reportPath, {
      generatedAt: new Date().toISOString(),
      packageName: args.packageName,
      track: args.track,
      status: args.status,
      editId,
      aab: aabPath,
      error: error.message,
    });
    throw error;
  }
}

main().catch((error) => {
  console.error(`❌ Error fatal: ${error.stack || error.message}`);
  process.exit(1);
});
