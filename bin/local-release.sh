#!/usr/bin/env bash
###
# bash ./bin/local-release.sh
###
ARGS=${1:-}
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && ./mvnw -DskipTests -B -s .mvn/settings.xml \
  release:clean release:prepare release:perform \
  -Plocal-release ${ARGS}
#cd "${ROOT_PROJECT_DIR}" && ./mvnw release:rollback -s .mvn/settings.xml
cd "${ROOT_PROJECT_DIR}" && git fetch --all -p -a --tags
cd "${ROOT_PROJECT_DIR}" && ./mvnw -DskipTests install
