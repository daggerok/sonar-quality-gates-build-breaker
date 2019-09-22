#!/usr/bin/env bash
###
# bash ./bin/compose-recreate.sh
###
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && bash ./bin/compose-kill.sh
cd "${ROOT_PROJECT_DIR}" && bash ./bin/compose-create.sh
