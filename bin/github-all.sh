#!/usr/bin/env bash
###
# bash ./bin/local-publish.sh
###
ARGS=${1:-}
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && bash ./bin/local-release.sh && bash ./bin/local-publish.sh && bash ./bin/github-publish.sh
