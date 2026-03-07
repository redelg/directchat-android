#!/usr/bin/env node

import fs from 'node:fs/promises';
import path from 'node:path';
import process from 'node:process';
import { GoogleAuth } from 'google-auth-library';

const DEFAULTS = {
  packageName: 'com.codergang.chatdirecto',
  track: 'production',
  status: 'completed',
  aab: 'app/build/outputs/bundle/release/app-release.aab',
  changesNotSentForReview: true,
};

function printUsage() {
  console.log(`
Upload AAB to Google Play

Usage:
  node docs/upload-aab.mjs \\
    --service-account /path/to/service-account.json \\
    [--aab app/build/outputs/bundle/release/app-release.aab] \\
    [--package-name com.codergang.chatdirecto] \\
    [--track production] \\
    [--status completed] \\
    [--release-notes "Bug fixes and improvements"] \\
    [--dry-run]

Tracks: production, beta, alpha, internal
Status: completed, draft, halted
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

    const key = arg.slice(2);
    const value = argv[i + 1];
    if (!value || value.startsWith('--')) {
      throw new Error(`Missing value for ${arg}`);
    }

    if (key === 'service-account') args.serviceAccount = value;
    else if (key === 'aab') args.aab = value;
    else if (key === 'package-name') args.packageName = value;
    else if (key === 'track') args.track = value;
    else if (key === 'status') args.status = value;
    else if (key === 'release-notes') args.releaseNotes = value;
    else throw new Error(`Unknown parameter: ${arg}`);
    i += 1;
  }
  return args;
}

async function main() {
  let args;
  try {
    args = parseArgs(process.argv.slice(2));
  } catch (error) {
    console.error(`Error: ${error.message}`);
    printUsage();
    process.exit(1);
  }

  if (args.help) {
    printUsage();
    return;
  }

  if (!args.serviceAccount) {
    console.error('Error: --service-account is required');
    printUsage();
    process.exit(1);
  }

  const serviceAccountPath = path.resolve(args.serviceAccount);
  const aabPath = path.resolve(args.aab);

  // Validate service account
  let serviceAccount;
  try {
    serviceAccount = JSON.parse(await fs.readFile(serviceAccountPath, 'utf8'));
  } catch (error) {
    console.error(`Error reading service account: ${error.message}`);
    process.exit(1);
  }

  // Validate AAB exists
  try {
    const stat = await fs.stat(aabPath);
    console.log(`AAB: ${aabPath} (${(stat.size / 1024 / 1024).toFixed(1)} MB)`);
  } catch {
    console.error(`AAB not found: ${aabPath}`);
    process.exit(1);
  }

  // Authenticate
  const auth = new GoogleAuth({
    credentials: serviceAccount,
    scopes: ['https://www.googleapis.com/auth/androidpublisher'],
  });
  const client = await auth.getClient();
  const accessToken = await client.getAccessToken();
  const token = accessToken?.token;
  if (!token) {
    throw new Error('Failed to obtain access token');
  }
  console.log('Authenticated successfully');

  const baseUrl = `https://androidpublisher.googleapis.com/androidpublisher/v3/applications/${args.packageName}`;

  // 1. Create edit
  console.log('Creating edit...');
  const editRes = await fetch(`${baseUrl}/edits`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({}),
  });
  if (!editRes.ok) {
    const err = await editRes.text();
    throw new Error(`Failed to create edit: ${editRes.status} ${err}`);
  }
  const edit = await editRes.json();
  const editId = edit.id;
  console.log(`Edit created: ${editId}`);

  if (args.dryRun) {
    console.log('[DRY RUN] Would upload AAB and assign to track. Deleting edit.');
    await fetch(`${baseUrl}/edits/${editId}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log('Dry run complete.');
    return;
  }

  // 2. Upload AAB
  console.log('Uploading AAB...');
  const aabBuffer = await fs.readFile(aabPath);
  const uploadRes = await fetch(
    `https://androidpublisher.googleapis.com/upload/androidpublisher/v3/applications/${args.packageName}/edits/${editId}/bundles?uploadType=media`,
    {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/octet-stream',
      },
      body: aabBuffer,
    }
  );
  if (!uploadRes.ok) {
    const err = await uploadRes.text();
    throw new Error(`Failed to upload AAB: ${uploadRes.status} ${err}`);
  }
  const bundle = await uploadRes.json();
  const versionCode = bundle.versionCode;
  console.log(`AAB uploaded — versionCode: ${versionCode}`);

  // 3. Assign to track
  console.log(`Assigning to track: ${args.track} (status: ${args.status})...`);
  const release = {
    versionCodes: [String(versionCode)],
    status: args.status,
  };
  if (args.releaseNotes) {
    release.releaseNotes = [
      { language: 'en-US', text: args.releaseNotes },
      { language: 'es-419', text: args.releaseNotes },
      { language: 'pt-BR', text: args.releaseNotes },
    ];
  }

  const trackRes = await fetch(`${baseUrl}/edits/${editId}/tracks/${args.track}`, {
    method: 'PUT',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      track: args.track,
      releases: [release],
    }),
  });
  if (!trackRes.ok) {
    const err = await trackRes.text();
    throw new Error(`Failed to assign track: ${trackRes.status} ${err}`);
  }
  console.log('Track assigned');

  // 4. Commit edit
  console.log('Committing edit...');
  const commitUrl = `${baseUrl}/edits/${editId}:commit?changesNotSentForReview=${args.changesNotSentForReview}`;
  let commitRes = await fetch(commitUrl, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({}),
  });

  // Retry without changesNotSentForReview if needed
  if (!commitRes.ok) {
    const errText = await commitRes.text();
    if (errText.toLowerCase().includes('changesnotsentforreview')) {
      console.log('Retrying commit without changesNotSentForReview flag...');
      commitRes = await fetch(`${baseUrl}/edits/${editId}:commit`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({}),
      });
      if (!commitRes.ok) {
        const err2 = await commitRes.text();
        throw new Error(`Failed to commit: ${commitRes.status} ${err2}`);
      }
    } else {
      throw new Error(`Failed to commit: ${commitRes.status} ${errText}`);
    }
  }

  console.log('\nDone!');
  console.log(`  Package: ${args.packageName}`);
  console.log(`  Version code: ${versionCode}`);
  console.log(`  Track: ${args.track}`);
  console.log(`  Status: ${args.status}`);
}

main().catch((error) => {
  console.error(`Fatal error: ${error.stack || error.message}`);
  process.exit(1);
});
