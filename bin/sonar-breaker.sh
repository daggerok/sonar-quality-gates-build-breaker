#!/usr/bin/env bash
#RPPT_PROJECT_DIR=$(cd "$(dirname ${BASH_SOURCE[0]})/.." >/dev/null 2>&1 && pwd)
RPPT_PROJECT_DIR=$(cd "$(dirname ${BASH_SOURCE[0]})/.." && pwd)
#set -e
cd ${RPPT_PROJECT_DIR}
./mvnw -P sonar ; java -jar ./sonar-breaker/target/*-all.jar ./target/sonar/report-task.txt
