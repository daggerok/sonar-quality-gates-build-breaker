# Docker compose [![Build Status](https://travis-ci.org/daggerok/sonar-quality-gates-build-breaker.svg?branch=master)](https://travis-ci.org/daggerok/sonar-quality-gates-build-breaker)
Main commandline tool

## Bootstrap SonarQube

```shell script
./mvnw -pl docker docker-compose:up
# or: docker-compose -f docker/docker-compose.yaml up
# ...
# sonar_1  | 2019.09.19 17:56:03 INFO  app[][o.s.a.SchedulerImpl] Process[ce] is up
# sonar_1  | 2019.09.19 17:56:03 INFO  app[][o.s.a.SchedulerImpl] SonarQube is up
```

## Teardown docker

```shell script
./mvnw -pl docker docker-compose:down
# or: docker-compose -f docker/docker-compose.yaml down -v --rmi local
```
