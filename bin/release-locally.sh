#!/usr/bin/env bash
###
# bash ./bin/publish-locally.sh
###
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && ./mvnw -DskipTests -B -s .mvn/settings.xml \
  -Darguments="-Dmaven.deploy.skip=true -Prelease-locally,publish-locally" \
  -Dresume=false release:prepare release:perform \
  -Prelease-locally,publish-locally
#cd "${ROOT_PROJECT_DIR}" && ./mvnw release:rollback -s .mvn/settings.xml
