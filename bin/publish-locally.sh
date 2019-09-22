#!/usr/bin/env bash
###
# bash ./bin/publish-locally.sh
###
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && ./mvnw -P publish-locally
#./mvnw release:clean release:prepare release:perform \
