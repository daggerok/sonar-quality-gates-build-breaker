#!/usr/bin/env bash
###
# bash deploy-locally.sh
###
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && ./mvnw -P deploy-locally
#./mvnw release:clean release:prepare release:perform \
