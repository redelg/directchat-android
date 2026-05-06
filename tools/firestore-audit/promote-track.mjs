#!/usr/bin/env node
import fs from 'node:fs/promises';
import process from 'node:process';
import path from 'node:path';
import { GoogleAuth } from 'google-auth-library';

const args = {};
for (let i = 2; i < process.argv.length; i += 2) {
  args[process.argv[i].replace(/^--/, '')] = process.argv[i + 1];
}
const PKG = args['package-name'] || 'com.codergang.chatdirecto';
const SA = path.resolve(args['service-account']);
const FROM = args.from || 'internal';
const TO = args.to || 'production';
const VC = String(args['version-code']);
const NAME = args['release-name'];
const NOTES_FILE = args['release-notes-file'];

const sa = JSON.parse(await fs.readFile(SA, 'utf8'));
const notes = NOTES_FILE
  ? JSON.parse(await fs.readFile(path.resolve(NOTES_FILE), 'utf8')).map(n => ({ language: n.language, text: n.text }))
  : undefined;

const auth = new GoogleAuth({ credentials: sa, scopes: ['https://www.googleapis.com/auth/androidpublisher'] });
const client = await auth.getClient();
const token = (await client.getAccessToken()).token;

const base = `https://androidpublisher.googleapis.com/androidpublisher/v3/applications/${PKG}`;

async function api(url, opts = {}) {
  const r = await fetch(url, {
    ...opts,
    headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json', ...(opts.headers || {}) },
  });
  const t = await r.text();
  const j = t ? JSON.parse(t) : {};
  if (!r.ok) throw new Error(`HTTP ${r.status}: ${JSON.stringify(j)}`);
  return j;
}

const edit = await api(`${base}/edits`, { method: 'POST', body: '{}' });
const editId = edit.id;
console.log('editId:', editId);

// Optionally remove from source track to avoid duplicate
try {
  const srcTrack = await api(`${base}/edits/${editId}/tracks/${FROM}`);
  const srcReleases = (srcTrack.releases || []).filter(r => !(r.versionCodes || []).includes(VC));
  await api(`${base}/edits/${editId}/tracks/${FROM}`, {
    method: 'PUT',
    body: JSON.stringify({ track: FROM, releases: srcReleases }),
  });
  console.log(`removed v${VC} from ${FROM}`);
} catch (e) {
  console.log(`skip removing from ${FROM}: ${e.message}`);
}

// Add to destination track
const release = {
  name: NAME,
  status: 'completed',
  versionCodes: [VC],
};
if (notes) release.releaseNotes = notes;

await api(`${base}/edits/${editId}/tracks/${TO}`, {
  method: 'PUT',
  body: JSON.stringify({ track: TO, releases: [release] }),
});
console.log(`added v${VC} to ${TO}`);

// Commit
let commitUrl = `${base}/edits/${editId}:commit?changesNotSentForReview=false`;
let commit;
try {
  commit = await api(commitUrl, { method: 'POST' });
} catch (e) {
  if (/changesnotsentforreview/i.test(e.message) || /changes are sent for review/i.test(e.message)) {
    commit = await api(`${base}/edits/${editId}:commit`, { method: 'POST' });
  } else {
    throw e;
  }
}
console.log('committed:', commit.id || commit);
console.log(`✅ v${VC} promoted from ${FROM} to ${TO}`);
