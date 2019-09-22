#!/usr/bin/env bash
###
# bash ./bin/compose-create.sh
###
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && ./mvnw -pl :docker docker-compose:up
n=${1:-1}
while [[ $(docker ps -n "${n}" -q -f health=healthy -f status=running | wc -l) -lt ${n} ]] ; do
  sleep 1s ;
  echo -ne '.' ;
done
echo ''
