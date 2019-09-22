---
title: Release and Deploy
lang: en-US
---

# Release and Deploy
Here are some notes, about how to publish or deploy locally or remotely to github / bintray jcenter 

[[toc]]

## publish artifacts locally

::: tip NOTE
See `./bin/local-publish.sh` script.
:::

```bash
./mvnw -P local-publish
# ...
tree ./target/.m2
```

Artifacts should be found under `./target/m2/repository` directory

## release github tag

::: tip NOTE
See `./bin/local-release.sh` script.
In case of failure, see `./bin/local-rollback.sh` script. 
:::

_shorter_

```bash
./mvnw -Plocal-release -B -s .mvn/settings.xml \
    release:clean release:prepare release:perform
```

_or more verbosely_

```bash
./mvnw -DskipTests -Dmaven.deploy.skip=true -B -s .mvn/settings.xml \
  -Darguments="-DskipTests -Dmaven.deploy.skip=true" \
  -Dresume=false -DgenerateReleasePoms=false \
  -DtagNameFormat="@{project.version}" \
  release:prepare release:perform
```

Release should be found on github project repo release page

## publish artifacts to github

::: tip NOTE
See `./bin/github-publish.sh` script.
:::

* first of all create maven branch on github and remove everything from it. so it will be using as new fresh clean maven
repository
* secondly prepare your `.mvn/settings.xml` file (see details in it's comments, you need last one: github):
  ```bash
  cp -Rf .mvn/settings.template.xml .mvn/settings.xml
  ```
* finally publish artifacts locally, so next we can upload them to github project repo in maven branch:
  ```bash
  ./mvnw -P local-publish
  ./mvnw -P github-publish -pl :sonar-quality-gates-build-breaker -s .mvn/settings.xml 
  ```
* To use published artifacts dependencies in your other maven project, add next maven repository in your project pom.xml
  file:
  ```xml
  <repository>
      <id>github-maven-repo</id>
      <url>https://github.com/daggerok/sonar-quality-gates-build-breaker/tree/maven/</url>
  </repository>
  ```

::: tip NOTE
shorter way (one liner command) do everything we have dome before:

```bash
bash ./bin/local-release.sh ; bash ./bin/local-publish.sh ; bash ./bin/github-publish.sh
```
:::
