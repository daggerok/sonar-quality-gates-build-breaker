#!/usr/bin/env bash
###
# bash ./bin/compose-kill.sh
###
ARGS=${1:-}
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && ./mvnw -pl :docker docker-compose:down ${ARGS}
