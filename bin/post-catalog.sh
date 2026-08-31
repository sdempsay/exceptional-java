#!/usr/bin/env bash
# After `mvn deploy`, POST this GAV to a reachable axiom-mcp (laptop, not GitHub Actions).
set -euo pipefail

url="${AXIOM_URL:-http://127.0.0.1:8741/catalogs}"
group_id="${1:-org.dempsay.utils}"
artifact_id="${2:-exceptional}"
version="${3:-}"

if [[ -z "${version}" ]]; then
  version="$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version)"
fi

curl -sS -X POST "${url}" \
  -H "Content-Type: application/json" \
  -d "{\"groupId\":\"${group_id}\",\"artifactId\":\"${artifact_id}\",\"version\":\"${version}\"}"
echo
