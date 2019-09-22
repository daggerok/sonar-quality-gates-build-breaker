#!/usr/bin/env bash
###
# bash ./bin/sonar-breaker-maven-plugin-demo.sh
###
ARGS=${1:-}
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && ./mvnw -P sonar -DskipTests ${ARGS}
cd "${ROOT_PROJECT_DIR}" && ./mvnw -pl :sonar-breaker-maven-plugin-demo sonar-breaker:analyze ${ARGS}
