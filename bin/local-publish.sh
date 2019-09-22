#!/usr/bin/env bash
###
# bash ./bin/local-publish.sh
###
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && ./mvnw -P local-publish
#./mvnw release:clean release:prepare release:perform \
