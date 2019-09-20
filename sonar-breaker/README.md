# Sonar Breaker [![Build Status](https://travis-ci.org/daggerok/sonar-quality-gates-build-breaker.svg?branch=master)](https://travis-ci.org/daggerok/sonar-quality-gates-build-breaker)
Main commandline tool

## Table of content

* [Quick start](#Quick-start)
  * [Bootstrap SonarQube](#Bootstrap-SonarQube)
  * [Fail analysis in shell](#Fail-analysis-in-shell)
  * [Better](#Better)
  * [Teardown](#Teardown)
* [Links](#links)

## Quick start

### Bootstrap SonarQube

```shell script
./mvnw -pl docker docker-compose:up
# or: docker-compose -f docker/docker-compose.yaml up
# ...
# sonar_1  | 2019.09.19 17:56:03 INFO  app[][o.s.a.SchedulerImpl] Process[ce] is up
# sonar_1  | 2019.09.19 17:56:03 INFO  app[][o.s.a.SchedulerImpl] SonarQube is up
```

### Fail analysis in shell

```bash
source ./shitty-demo-project/target/sonar/report-task.txt
result=$(http http://127.0.0.1/api/qualitygates/project_status\?projectKey\=com.example:shitty-demo-project | jq -r '.projectStatus.status')
test $result != "OK" && echo faild
test $result != "OK" || echo success
```

### Better

_do analysis and fail build if it's sonarQube quality grates did passed with single line command_

```bash
./mvnw -P sonar ; java -jar ./sonar-breaker/target/*-all.jar ./target/sonar/report-task.txt
```

### Teardown

```shell script
./mvnw -pl docker docker-compose:down
# or: docker-compose -f docker/docker-compose.yaml down -v --rmi local
```

## Links

* [SonarQube docker image](https://hub.docker.com/_/sonarqube/)
* [SonarScanner for Maven](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-maven/)
* [SonarQube maven examples](https://github.com/SonarSource/sonar-scanning-examples/tree/master/sonarqube-scanner-maven)
* https://mitrai.com/tech-guide/using-sonarqube-to-automate-quality-checks-for-your-maven-project/
* [How to publish maven projects to JCenter](https://github.com/daggerok/publish-maven-project-to-jcenter)
