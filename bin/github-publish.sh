#!/usr/bin/env bash
###
# bash ./bin/local-publish.sh
###
ARGS=${1:-}
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && ./mvnw -P local-publish ${ARGS}
cd "${ROOT_PROJECT_DIR}" && ./mvnw -P github-publish -pl :sonar-quality-gates-build-breaker -s .mvn/settings.xml ${ARGS}
