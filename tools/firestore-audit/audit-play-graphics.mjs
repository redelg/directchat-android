#!/usr/bin/env node

import fs from 'node:fs/promises';
import path from 'node:path';
import process from 'node:process';
import { GoogleAuth } from 'google-auth-library';

const DEFAULTS = {
  packageName: 'com.codergang.chatdirecto',
  outDir: 'tools/firestore-audit/output/play-graphics',
  report: 'tools/firestore-audit/output/play_graphics_audit_report.json',
  download: true,
};

const IMAGE_TYPES = [
  'icon',
  'featureGraphic',
  'phoneScreenshots',
  'sevenInchScreenshots',
  'tenInchScreenshots',
  'tvScreenshots',
  'wearScreenshots',
  'tvBanner',
];

function printUsage() {
  console.log(`
Audit Play Store Graphics

Uso:
  node tools/firestore-audit/audit-play-graphics.mjs \\
    --service-account /ruta/play-service-account.json \\
    [--package-name com.codergang.chatdirecto] \\
    [--out-dir tools/firestore-audit/output/play-graphics] \\
    [--report tools/firestore-audit/output/play_graphics_audit_report.json] \\
    [--no-download]
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
    if (arg === '--no-download') {
      args.download = false;
      continue;
    }

    const key = arg.slice(2);
    const value = argv[i + 1];
    if (!value || value.startsWith('--')) {
      throw new Error(`Falta valor para ${arg}`);
    }

    if (key === 'service-account') args.serviceAccount = value;
    else if (key === 'package-name') args.packageName = value;
    else if (key === 'out-dir') args.outDir = value;
    else if (key === 'report') args.report = value;
    else throw new Error(`Parametro no reconocido: ${arg}`);
    i += 1;
  }
  return args;
}

function inferExt(buffer, contentType) {
  const header = buffer.subarray(0, 12);
  const isPng =
    header[0] === 0x89 &&
    header[1] === 0x50 &&
    header[2] === 0x4e &&
    header[3] === 0x47 &&
    header[4] === 0x0d &&
    header[5] === 0x0a &&
    header[6] === 0x1a &&
    header[7] === 0x0a;
  if (isPng || String(contentType || '').includes('png')) return 'png';
  const isJpeg = header[0] === 0xff && header[1] === 0xd8;
  if (isJpeg || String(contentType || '').includes('jpeg') || String(contentType || '').includes('jpg')) {
    return 'jpg';
  }
  const isWebp =
    header[0] === 0x52 &&
    header[1] === 0x49 &&
    header[2] === 0x46 &&
    header[3] === 0x46 &&
    header[8] === 0x57 &&
    header[9] === 0x45 &&
    header[10] === 0x42 &&
    header[11] === 0x50;
  if (isWebp || String(contentType || '').includes('webp')) return 'webp';
  return 'bin';
}

function parsePngDimensions(buffer) {
  if (buffer.length < 24) return null;
  const width = buffer.readUInt32BE(16);
  const height = buffer.readUInt32BE(20);
  return { width, height };
}

function parseJpegDimensions(buffer) {
  let offset = 2;
  while (offset + 9 < buffer.length) {
    if (buffer[offset] !== 0xff) {
      offset += 1;
      continue;
    }
    const marker = buffer[offset + 1];
    if (marker === 0xd8 || marker === 0xd9) {
      offset += 2;
      continue;
    }
    const length = buffer.readUInt16BE(offset + 2);
    if (length < 2) return null;
    const isSof =
      marker >= 0xc0 &&
      marker <= 0xcf &&
      marker !== 0xc4 &&
      marker !== 0xc8 &&
      marker !== 0xcc;
    if (isSof) {
      const height = buffer.readUInt16BE(offset + 5);
      const width = buffer.readUInt16BE(offset + 7);
      return { width, height };
    }
    offset += 2 + length;
  }
  return null;
}

function parseWebpDimensions(buffer) {
  if (buffer.length < 30) return null;
  const riff = buffer.toString('ascii', 0, 4) === 'RIFF';
  const webp = buffer.toString('ascii', 8, 12) === 'WEBP';
  if (!riff || !webp) return null;

  const chunkType = buffer.toString('ascii', 12, 16);
  if (chunkType === 'VP8X' && buffer.length >= 30) {
    const widthMinusOne = buffer.readUIntLE(24, 3);
    const heightMinusOne = buffer.readUIntLE(27, 3);
    return { width: widthMinusOne + 1, height: heightMinusOne + 1 };
  }
  return null;
}

function getDimensions(ext, buffer) {
  if (ext === 'png') return parsePngDimensions(buffer);
  if (ext === 'jpg') return parseJpegDimensions(buffer);
  if (ext === 'webp') return parseWebpDimensions(buffer);
  return null;
}

function screenshotRequirementIssues(width, height) {
  const issues = [];
  if (!width || !height) {
    issues.push('missing_dimensions');
    return issues;
  }
  const minDim = Math.min(width, height);
  const maxDim = Math.max(width, height);
  if (minDim < 320) issues.push('min_dimension_lt_320');
  if (maxDim > 3840) issues.push('max_dimension_gt_3840');
  if (maxDim > minDim * 2) issues.push('aspect_ratio_over_2_to_1');
  return issues;
}

function validateGraphic(type, ext, bytes, dimensions) {
  const issues = [];
  if (type === 'icon') {
    if (ext !== 'png') issues.push('icon_not_png');
    if (!dimensions || dimensions.width !== 512 || dimensions.height !== 512) {
      issues.push('icon_not_512x512');
    }
    if (bytes > 1024 * 1024) issues.push('icon_over_1024kb');
  } else if (type === 'featureGraphic') {
    if (ext !== 'png' && ext !== 'jpg') issues.push('feature_invalid_format');
    if (!dimensions || dimensions.width !== 1024 || dimensions.height !== 500) {
      issues.push('feature_not_1024x500');
    }
  } else if (type.endsWith('Screenshots')) {
    issues.push(...screenshotRequirementIssues(dimensions?.width, dimensions?.height));
  }
  return issues;
}

function sanitizeName(value) {
  return String(value || '')
    .replace(/[^\w.-]+/g, '_')
    .replace(/^_+|_+$/g, '');
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

async function fetchImageBuffer(url) {
  let resolvedUrl = url;
  if (typeof url === 'string' && url.includes('googleusercontent.com') && !url.includes('=')) {
    // Google image endpoints default to a downscaled preview unless size is specified.
    resolvedUrl = `${url}=s0`;
  }

  const response = await fetch(resolvedUrl);
  if (!response.ok) {
    throw new Error(`HTTP ${response.status} ${response.statusText}`);
  }
  const contentType = response.headers.get('content-type') || '';
  const buffer = Buffer.from(await response.arrayBuffer());
  return { buffer, contentType, resolvedUrl };
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
  if (!args.serviceAccount) {
    console.error('❌ Debes indicar --service-account');
    printUsage();
    process.exit(1);
  }

  const serviceAccountPath = path.resolve(args.serviceAccount);
  const outDir = path.resolve(args.outDir);
  const reportPath = path.resolve(args.report);

  const serviceAccount = JSON.parse(await fs.readFile(serviceAccountPath, 'utf8'));
  const auth = new GoogleAuth({
    credentials: serviceAccount,
    scopes: ['https://www.googleapis.com/auth/androidpublisher'],
  });
  const client = await auth.getClient();
  const accessToken = await client.getAccessToken();
  const token = accessToken?.token;
  if (!token) throw new Error('No se pudo obtener access token');

  const baseUrl = `https://androidpublisher.googleapis.com/androidpublisher/v3/applications/${args.packageName}`;
  const edit = await fetchJson(`${baseUrl}/edits`, token, {
    method: 'POST',
    body: JSON.stringify({}),
  });
  const editId = edit.id;

  const errors = [];
  const findings = [];

  try {
    const listings = await fetchJson(`${baseUrl}/edits/${editId}/listings`, token);
    const locales = (listings.listings || []).map((item) => item.language).filter(Boolean);

    if (args.download) {
      await fs.mkdir(outDir, { recursive: true });
    }

    for (const locale of locales) {
      for (const imageType of IMAGE_TYPES) {
        let data;
        try {
          data = await fetchJson(
            `${baseUrl}/edits/${editId}/listings/${encodeURIComponent(locale)}/${imageType}`,
            token
          );
        } catch (error) {
          errors.push({
            locale,
            imageType,
            stage: 'list',
            error: error.message,
          });
          continue;
        }

        const images = Array.isArray(data.images) ? data.images : [];
        for (let index = 0; index < images.length; index += 1) {
          const item = images[index];
          let bytes = null;
          let ext = null;
          let dimensions = null;
          let savedPath = null;
          let contentType = null;
          let resolvedUrl = item.url || null;
          const issues = [];

          try {
            const fetched = await fetchImageBuffer(item.url);
            resolvedUrl = fetched.resolvedUrl || resolvedUrl;
            contentType = fetched.contentType;
            bytes = fetched.buffer.length;
            ext = inferExt(fetched.buffer, fetched.contentType);
            dimensions = getDimensions(ext, fetched.buffer);
            issues.push(...validateGraphic(imageType, ext, bytes, dimensions));

            if (args.download) {
              const localeDir = path.join(outDir, sanitizeName(locale), imageType);
              await fs.mkdir(localeDir, { recursive: true });
              const hash = sanitizeName(item.sha256 || item.sha1 || `${index + 1}`);
              const fileName = `${String(index + 1).padStart(2, '0')}_${hash}.${ext}`;
              const filePath = path.join(localeDir, fileName);
              await fs.writeFile(filePath, fetched.buffer);
              savedPath = filePath;
            }
          } catch (error) {
            errors.push({
              locale,
              imageType,
              index,
              imageId: item.id,
              stage: 'download',
              error: error.message,
            });
          }

          findings.push({
            locale,
            imageType,
            index,
            imageId: item.id || null,
            sha1: item.sha1 || null,
            sha256: item.sha256 || null,
            url: item.url || null,
            resolvedUrl,
            contentType,
            ext,
            bytes,
            width: dimensions?.width || null,
            height: dimensions?.height || null,
            issues,
            savedPath,
          });
        }
      }
    }

    const byType = {};
    const byLocale = {};
    let totalIssues = 0;
    for (const item of findings) {
      byType[item.imageType] = byType[item.imageType] || { count: 0, issues: 0 };
      byType[item.imageType].count += 1;
      byType[item.imageType].issues += item.issues.length;

      byLocale[item.locale] = byLocale[item.locale] || { count: 0, issues: 0 };
      byLocale[item.locale].count += 1;
      byLocale[item.locale].issues += item.issues.length;

      totalIssues += item.issues.length;
    }

    const report = {
      generatedAt: new Date().toISOString(),
      packageName: args.packageName,
      editId,
      locales: Object.keys(byLocale).sort(),
      imageTypes: IMAGE_TYPES,
      downloadEnabled: args.download,
      outDir: args.download ? outDir : null,
      totalAssets: findings.length,
      totalIssues,
      errorCount: errors.length,
      byType,
      byLocale,
      findings,
      errors,
    };

    await fs.mkdir(path.dirname(reportPath), { recursive: true });
    await fs.writeFile(reportPath, `${JSON.stringify(report, null, 2)}\n`, 'utf8');

    console.log('✅ Play graphics audit completado');
    console.log(`- package: ${args.packageName}`);
    console.log(`- locales: ${report.locales.join(', ')}`);
    console.log(`- assets: ${report.totalAssets}`);
    console.log(`- issues: ${report.totalIssues}`);
    console.log(`- errors: ${report.errorCount}`);
    if (args.download) {
      console.log(`- assets dir: ${outDir}`);
    }
    console.log(`- report: ${reportPath}`);
  } finally {
    await fetchJson(`${baseUrl}/edits/${editId}`, token, { method: 'DELETE' }).catch(() => {});
  }
}

main().catch((error) => {
  console.error(`❌ Error fatal: ${error.stack || error.message}`);
  process.exit(1);
});
