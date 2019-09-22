#!/usr/bin/env bash
###
# bash ./bin/local-publish.sh
###
ARGS=${1:-}
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && ./mvnw release:prepare release:perform -B -s .mvn/settings.xml \
  -DskipTests -Darguments="-DskipTests" -DtagNameFormat="@{project.version}" \
  -Dresume=false -DautoVersionSubmodules=true -DgenerateReleasePoms=false \
  -DpreparationGoals="clean" -DcompletionGoals="clean install"
