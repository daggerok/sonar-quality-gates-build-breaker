#!/usr/bin/env bash
###
# bash ./bin/local-release.sh
###
ROOT_PROJECT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "${ROOT_PROJECT_DIR}" && ./mvnw -DskipTests -B -s .mvn/settings.xml \
  -Darguments="-Dmaven.deploy.skip=true -Plocal-release,local-publish" \
  -Dresume=false release:prepare release:perform \
  -Plocal-release,local-publish
#cd "${ROOT_PROJECT_DIR}" && ./mvnw release:rollback -s .mvn/settings.xml