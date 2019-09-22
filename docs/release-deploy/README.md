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

```bash
./mvnw -DskipTests -B -s .mvn/settings.xml \
  -Darguments="-DskipTests -Dmaven.deploy.skip=true" \
  -Dresume=false -DgenerateReleasePoms=false \
  release:prepare release:perform \
  -Plocal-release
```

Release should be found on github project repo release page
