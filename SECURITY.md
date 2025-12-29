## Security Policy for EWallet Clone User Service

This project is a security-focused authentication/user-service. The rules below are mandatory for contributions and deployments.

### 1) Authentication
- Passwords: Hash with BCrypt (cost ≥ 10) or Argon2id; never log or return hashes.
- Tokens: Access tokens short-lived (5–15m). Refresh tokens 7–30d, stored hashed, rotated on use, old revoked.
- OTP: Max 1 send/minute, 5 attempts, TTL 2–5m. Store hashed if persisted; generic errors to avoid enumeration.
- Account discovery: Always respond generically (“If the email exists, instructions were sent.”).
- JWT keys: Strong 256-bit secrets or RS256 with protected private key; support rotation.
- Input validation: `@Valid`, length and regex constraints, strict parsing.

### 2) Authorization
- RBAC: Server-owned roles; never accept roles from clients. Enforce with method-level security.
- Resource ownership: Validate `userId == authenticatedUserId` for user-owned resources.
- Admin boundaries: Admin-only endpoints explicitly guarded; support/read-only roles separated.
- IDs & inputs: Prefer UUIDs; validate pagination/filter params; restrict file types and sizes.

### 3) Data & Infrastructure
- Secrets: Never in code/Git. Use env/secret manager (Vault/SM/SSM).
- Transport: Assume HTTPS-only clients; reject insecure origins.
- Rate limiting: Protect `/auth/*` (login, refresh, OTP, reset).
- Logging: No secrets/OTP/password/tokens. Log event + user id + status only.
- File uploads: Presigned URLs, MIME/type/size checks, random names; no local FS; optional AV scan hook.
- Least privilege: Scoped DB user, private S3 bucket, scoped API keys.
- Error handling: Generic client errors; detailed logs internally.
- CORS: Restrict origins/methods/headers; never `*` with credentials.
- Dependency hygiene: Keep dependencies updated; run vulnerability scans (OWASP DC/Snyk/Dependabot).

### 4) Incident & Revocation
- Support token revocation/blacklist on logout, block, or suspected compromise.
- Audit/auth events logging for login/refresh/OTP/reset/admin actions.
- Ability to disable accounts (block/suspend) and invalidate active tokens.

### 5) Testing Expectations
- Unit: password policy, OTP validation, refresh rotation, token parsing.
- Integration: end-to-end auth flows (register/login/refresh/OTP/reset), RBAC, blocked-user behavior.
- Negative paths: expired/invalid tokens, lockouts, rate-limit responses.

### 6) Contribution Rules
- No sensitive data in commits, logs, or test fixtures.
- New endpoints must specify authZ requirements and rate-limit posture.
- Default to immutable identifiers; updates must validate ownership and roles.

