---
title: Release and Deploy
lang: en-US
---

# Release and Deploy
Here are some notes, about how to publish or deploy locally or remotely to github / bintray jcenter [ ![Download](https://api.bintray.com/packages/daggerok/maven/sonar-quality-gates-build-breaker/images/download.svg) ](https://bintray.com/daggerok/maven/sonar-quality-gates-build-breaker/_latestVersion)

[[toc]]

## Versions

<!--

## Manual (by hands) job

-->

First of all, if you just wanna increment version, you can do it with `versions-maven-plugin` like so:

```bash
mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion} versions:commit
```

```cmd
mvn build-helper:parse-version versions:set -DnewVersion=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.nextIncrementalVersion} versions:commit
```

_example_

```bash
mvn build-helper:parse-version versions:set -DnewVersion=1.2.3-SNAPSHOT versions:commit
```

## Publish artifacts locally

::: tip NOTE
See `./bin/local-publish.sh` script.
:::

```bash
./mvnw -P local-publish
# ...
tree ./target/.m2
```

Artifacts should be found under `./target/m2/repository` directory

## Release github tag

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

## Publish artifacts to github

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
bash ./bin/github-all.sh
```
:::

<!--

## Automatic (CI) job

All job before was intended to be executed manually from developer laptop...
Let's on each created tag trigger bintray jcenter release publication automatically!

-->

## Upload Github release

```bash
./mvnw clean package
./mvnw -P github-release -pl :sonar-quality-gates-build-breaker
```

## Publish artifacts to bintray jcenter

::: tip NOTE
See `./bin/bintray-all.sh` script.
:::

_Workflow_

* on each tag trigger publish job
* job should publish artifacts to own bintray jcenter maven repository
* after it can be released manually in bintray web UI and synced with maven central

_gpg_

```bash
gpg --gen-key
gpg --list-secret-keys
# find: 7D2BAF1CF37B13E2069D6956105BD0E739499BDB in putput
gpg --keyserver hkp://pgp.mit.edu --send-keys 7D2BAF1CF37B13E2069D6956105BD0E739499BDB
gpg --keyserver hkp://pgp.mit.edu --recv-keys 7D2BAF1CF37B13E2069D6956105BD0E739499BDB
```

_avoid prompting gog passphrase_

add according profile

```xml
<profile>
    <id>read gpg.passphrase from environment variable</id>
    <activation>
        <property>
            <name>env.GPG_PASSPHRASE</name>
        </property>
    </activation>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-gpg-plugin</artifactId>
                <version>${maven-gpg-plugin.version}</version>
                <executions>
                    <execution>
                        <id>sign-artifacts</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>sign</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <passphrase>${env.GPG_PASSPHRASE}</passphrase>
                </configuration>
            </plugin>
        </plugins>
    </build>
</profile>
```

_first of all create own bintray jcenter `daggerok/maven` repository_

* Go to https://bintray.com/daggerok
* Sign in (with GitHub)
* Click on top right conor icon -> `Edit Profile`
* Click on repositories -> `New Repository`
* Enter name: `maven`
* Select type: `Maven`
* Select default license: `MIT`
* Click `Create`

_first time initially (before first publication) you have to create according package for your artifact in your maven
repository_

* Goto https://bintray.com/daggerok/maven
* Click `Add New Package`
* Enter Name: `sonar-quality-gates-build-breaker`
* Enter description...
* Select `Licences`, `Tags`, `Maturity`
* Enter Website: `https://daggerok.github.io/sonar-quality-gates-build-breaker/`
* Enter Issues tracker: `https://github.com/daggerok/sonar-quality-gates-build-breaker`
* Enter Version control: `https://github.com/daggerok/sonar-quality-gates-build-breaker.git`
* Check `Make download numbers in stats public`
* Click `Create Package`
* Enter GitHub repo (user/repo): `daggerok/sonar-quality-gates-build-breaker`
* Click `Update Package`
* Enter GitHub release notes file: `CHANGELOG.md`
* Click `Update Package`
* Verify popup notification: `Package sonar-quality-gates-build-breaker was updated`

_secondly update settings.xml file accordingly: `//servers/server/bintray-daggerok-daggerok` password => bintray API key_

```bash
vi .mvn/settings.xml
```

_add `distributionManagement` section in your pom.xml_

```xml
<distributionManagement>
    <repository>
        <id>bintray-daggerok-maven</id>
        <name>daggerok-maven</name>
        <url>https://api.bintray.com/maven/daggerok/maven/sonar-quality-gates-build-breaker/;publish=1</url>
    </repository>
</distributionManagement>
```

_finally, once repository was created and everything else needed has been done, publish artifacts to your personal
bintray jcenter maven repository_

<!--

```bash
./mvnw -Pbintray-publish -B -s .mvn/settings.xml
```

-->

_more verbosely_

```bash
./mvnw release:prepare release:perform -B -s .mvn/settings.xml \
  -DskipTests -Darguments="-DskipTests" \
  -DtagNameFormat="@{project.version}" \
  -DautoVersionSubmodules=true \
  -DgenerateReleasePoms=false \
  -DpreparationGoals="clean" \
  -DcompletionGoals="clean" \
  -Dresume=false
```

_easiest way to release locally_

```bash
GPG_PASSPHRASE=MyGpgPassPhraseForSonarBreaker bash ./bin/bintray-all.sh
```
