#!/usr/bin/env bash
###
# bash ./bin/local-publish.sh
###
ARGS=${1:-}
if [ -z "${GPG_PASSPHRASE}" ]; then
  echo 'please provide GPG_PASSPHRASE env variable'
  exit -1
fi
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && ./mvnw release:prepare release:perform -B -s .mvn/settings.xml \
  -DskipTests -Darguments="-DskipTests -Dgpg.passphrase=${GPG_PASSPHRASE}" \
  -Dgpg.passphrase=${GPG_PASSPHRASE} -DautoVersionSubmodules=true \
  -Dresume=false  -DgenerateReleasePoms=false \
  -DtagNameFormat="@{project.version}" \
  -DcompletionGoals="clean install" \
  -DpreparationGoals="clean"
#cd "${ROOT_PROJECT_DIR}" && ./mvnw release:prepare release:perform -B -s .mvn/settings.xml \
#  -DskipTests -Darguments="-DskipTests" -DautoVersionSubmodules=true \
#  -Dresume=false  -DgenerateReleasePoms=false \
#  -DtagNameFormat="@{project.version}" \
#  -DcompletionGoals="clean install" \
#  -DpreparationGoals="clean"
