#!/usr/bin/env bash
###
# bash ./bin/compose-create.sh
###
ARGS=${1:-}
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && ./mvnw -pl :docker docker-compose:up ${ARGS}
while [[ $(docker ps -n 1 -q -f health=healthy -f status=running | wc -l) -lt 1 ]] ; do
  sleep 1s ;
  echo -ne '.' ;
done
echo ''
