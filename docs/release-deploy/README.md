# Release / Deploy
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
    release:prepare release:perform
```

_or more verbosely_

```bash
./mvnw -DskipTests -Dmaven.deploy.skip=true -B -s .mvn/settings.xml \
  -Dresume=false -DgenerateReleasePoms=false -DtagNameFormat="@{project.version}" \
  -Darguments="-DskipTests -Dmaven.deploy.skip=true" \
  release:prepare release:perform
```

Release should be found on github project repo release page
