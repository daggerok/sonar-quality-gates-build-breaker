#!/usr/bin/env bash
###
# bash ./bin/sonar-breaker-maven-plugin-demo.sh
###
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && ./mvnw -P sonar -DskipTests
cd "${ROOT_PROJECT_DIR}" && ./mvnw -pl :sonar-breaker-maven-plugin-demo sonar-breaker:analyze
