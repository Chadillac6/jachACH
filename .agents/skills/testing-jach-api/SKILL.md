# Testing jachACH REST API

## Build & Run

```bash
# Build from repo root (both jach-core and jach-api)
cd /home/ubuntu/repos/jachACH
mvn clean package -DskipTests

# Run the Spring Boot API
java -jar jach-api/target/jach-api-0.3.5-SNAPSHOT.jar &
# Runs on port 8080
```

## Endpoints

| Endpoint | Method | Content-Type | Description |
|----------|--------|-------------|-------------|
| `/ach/parse` | POST | `multipart/form-data` | Upload ACH file, get JSON |
| `/ach/generate` | POST | `application/json` | Send raw ACH text, get NACHA flat file |
| `/ach/validate` | POST | `multipart/form-data` | Upload ACH file, get validation result |
| `/swagger-ui/index.html` | GET | — | Swagger UI |
| `/api-docs` | GET | — | OpenAPI JSON spec |

## Test Data

- Valid ACH file: `jach-api/src/test/resources/ach-payrol.txt` (9 lines, 1 batch, 5 PPD entries)
- This file is also available in `jach-core/src/test/resources/`

## Testing via curl

### Parse (multipart upload)
```bash
curl -X POST http://localhost:8080/ach/parse \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@jach-api/src/test/resources/ach-payrol.txt;type=text/plain'
```

### Generate (raw ACH text as JSON body)
**Important:** The generate endpoint expects `application/json` with the raw ACH text as the body. Do NOT use multipart form data. Use `--data-binary` to preserve newlines:
```bash
curl -X POST http://localhost:8080/ach/generate \
  -H 'Content-Type: application/json' \
  --data-binary @jach-api/src/test/resources/ach-payrol.txt
```

### Validate (multipart upload)
```bash
curl -X POST http://localhost:8080/ach/validate \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@jach-api/src/test/resources/ach-payrol.txt;type=text/plain'
```

### Error handling test
```bash
echo 'not an ACH file' > /tmp/invalid.txt
curl -X POST http://localhost:8080/ach/parse \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@/tmp/invalid.txt;type=text/plain'
# Expects HTTP 400 with {"error": "ACH Processing Error", ...}
```

## Common Pitfalls

- The `/ach/generate` endpoint does NOT accept multipart form data — it requires `application/json`. Sending multipart will result in HTTP 500 "Content-Type 'multipart/form-data' is not supported".
- ACH files must have exactly 94 characters per line. The validation endpoint checks this.
- The ACH class has mutable state (ACHWriter.lines). Each request must create a new ACH instance — do not reuse a singleton across requests.
- BufferedReaders wrapping InputStreams should use try-with-resources to avoid file descriptor leaks.

## Swagger UI Testing

1. Navigate to `http://localhost:8080/swagger-ui/index.html`
2. All 3 endpoints are listed under the "ACH" tag
3. Use "Try it out" → upload file → "Execute" for parse and validate
4. For generate, Swagger UI provides a text input for the JSON body

## Unit/Integration Tests

```bash
# Run all tests
mvn clean verify

# Run only API tests
mvn -pl jach-api test
```

## Devin Secrets Needed

No secrets required — the API runs locally without authentication.
