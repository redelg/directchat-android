# Google Play Suspension - `com.codergang.directchat`

## Enforcement snapshot
- Status: `Suspended`
- Effective date: `2025-11-18`
- Policy: `Impersonation`
- Scope from Play notice: store listing text, featured graphic, and app screenshots.

## Root cause
The listing and creatives referenced third-party messaging brands and used visual language that could imply affiliation/authorization.

## Remediation implemented in code
1. Rebranded in-app identity to neutral icon/splash resources.
2. Removed third-party brand mentions from in-app copy.
3. Added neutral non-affiliation statement in settings.
4. Replaced external legal links with in-app local legal pages to prevent broken links during review.
5. Bumped app version to `2.0.1` (`versionCode 8`).

## Store listing remediation required
1. Replace title/short/full descriptions with neutral copy (see `docs/PLAY_LISTING_SAFE_COPY.md`).
2. Replace screenshots and featured graphic to remove third-party marks/logos/UI references.
3. Keep wording factual and avoid suggesting partnership, endorsement, or authorization.

## Appeal package checklist
1. Include statement that all impersonation-risk assets and copy were removed.
2. Attach before/after screenshots of app icon, splash, settings disclaimer, and legal screens.
3. Confirm new listing text and creatives are trademark-neutral.
4. Request reinstatement review for updated compliance state.
