package com.github.daggerok.sonarbreaker.maven.plugin;

import com.github.daggerok.sonarbreaker.SonarBreaker;
import io.vavr.collection.HashMap;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.daggerok.sonarbreaker.infrastructure.Env.*;
import static io.vavr.Predicates.not;

/**
 * Waiting for sonar:sonar analysis completion and failing execution in case if
 * any related to current project Quality Gates metrics wont accepts defined thresholds
 *
 * goal sonar-breaker:analyze
 */
@Mojo(
        name = "analyze",
        threadSafe = true,
        aggregator = false,
        requiresDirectInvocation = true,
        // defaultPhase = LifecyclePhase.VERIFY,
        requiresDependencyResolution = ResolutionScope.TEST
)
@Log4j2
public class SonarBreakerAnalyzer extends AbstractMojo {

    /**
     * Configuring sonar-breaker-maven-plugin execute sonar-breaker in a standalone mode.
     * If present, maven build will be terminated from sonar-breaker process, not maven
     */
    @Parameter(property = "sonar.breaker.standalone", alias = "standalone", defaultValue = "false")
    private String sonarBreakerStandalone;

    /**
     * Max number of retries to be attempt for SonarQube REST API
     * calls during waiting for analysis completion
     */
    @Parameter(property = "sonar.breaker.retry", alias = "retry")
    private String sonarBreakerRetry;

    /**
     * Time interval in seconds between SonarQube REST API calls
     * to be sent to SonarQube REST API during waiting for analysis
     * completion
     */
    @Parameter(property = "sonar.breaker.delay", alias = "delay")
    private String sonarBreakerDelay;

    /**
     * List of metrics divided by coma without spaces will be included
     * for Quality Gates check results if status is ERROR.
     * If not present all issues are going to be analyzed and causing
     * build failure if actual value will be equals to ERROR
     */
    @Parameter(property = "sonar.breaker.metrics.includes", alias = "metricsIncludes")
    private String sonarBreakerMetricsIncludes;

    /**
     * List of metrics divided by coma without spaces in between to
     * be excluded from Quality Gates check results even if their
     * actual status is ERROR
     */
    @Parameter(property = "sonar.breaker.metrics.excludes", alias = "metricsExcludes")
    private String sonarBreakerMetricsExcludes;

    /**
     * Base multi-module (root) project directory. It can be used to calculate in runtime where
     * target/sonar/report-task.txt file should be located after sonar:sonar goal execution.
     * <hr/>
     * Default value: ${mavep.multiModuleProjectDirectory}
     * Requires: maven >= 3.3.9
     */
    @Parameter(
            alias = "projectBaseDir",
            property = "sonar.projectBaseDir",
            defaultValue = "${maven.multiModuleProjectDirectory}"
    )
    private String sonarProjectBaseDir;

    /**
     * SonarQube analysis result (goal sonar:sonar).
     * As result file target/sonar/report-task.txt should be generated.
     * <hr/>
     * Default value: null
     * Fallback value: ${sonar.projectBaseDir}/target/sonar/report-task.txt
     * Requires: maven >= 3.3.9
     */
    @Parameter(property = "sonar.scanner.metadataFilePath", alias = "metadataFilePath")
    private File sonarScannerMetadataFilePath;

    /**
     * Specify whenever plugin execution can fail.
     * <hr/>
     * Default value: false
     */
    @Parameter(property = "sonar.breaker.allowFailure", defaultValue = "false")
    private boolean allowFailure;

    /**
     * Main plugin execution entry point
     *
     * @throws MojoExecutionException in case of sonar-breaker:analyze goal execution failure
     * @throws MojoFailureException   in case of maven compilation execution failure
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        mapPluginProps();

        final File sonarReportFile = findSonarQubeReportTaskTxtFile();
        if (!sonarReportFile.exists()) {
            final val notFoundError = error(sonarReportFile, "found");
            if (!allowFailure) throw notFoundError;
            log.warn("Skipping build which allowed to fail...");
            return;
        }
        if (!sonarReportFile.canRead()) {
            final val cannotReadError = error(sonarReportFile, "read");
            if (!allowFailure) throw cannotReadError;
            log.warn("Skipping build allowed to fail: {}", cannotReadError.getLocalizedMessage());
            return;
        }

        final String path = sonarReportFile.getAbsolutePath();
        log.debug("Metadata file resolved: {}", () -> path);

        final Try<Void> aTry = Try.run(() -> SonarBreaker.main(Stream.of(path).toArray(String[]::new)))
                                  .onFailure(e -> log.error("{}", e.getLocalizedMessage()));
        if (aTry.isSuccess()) return;

        final Throwable cause = aTry.getCause();
        final String message = cause.getLocalizedMessage();
        final String error = String.join(" ", "sonar-breaker:analyze goal failed", message);
        if (!allowFailure) throw new MojoExecutionException(error);
        log.warn("Build is allowed to fail: {}", error);
    }

    /**
     * Set all from plugin props in Java VM System as properties
     */
    private void mapPluginProps() {
        HashMap.of(SONAR_BREAKER_STANDALONE, sonarBreakerStandalone,
                   SONAR_BREAKER_DELAY, sonarBreakerDelay,
                   SONAR_BREAKER_RETRY, sonarBreakerRetry,
                   SONAR_BREAKER_METRICS_INCLUDES, sonarBreakerMetricsIncludes,
                   SONAR_BREAKER_METRICS_EXCLUDES, sonarBreakerMetricsExcludes)
               .forEach((env, v) -> Optional.ofNullable(v)
                                            .map(String::trim)
                                            .filter(not(String::isEmpty))
                                            .map(value -> System.setProperty(env.systemProperty, value)));
    }

    private File findSonarQubeReportTaskTxtFile() {
        return Optional.ofNullable(sonarScannerMetadataFilePath)
                       .orElse(Paths.get(sonarProjectBaseDir, "target", "sonar", "report-task.txt")
                                    .toFile());
    }

    private static MojoExecutionException error(File metadataFilePath, String what) {
        if (log.isWarnEnabled()) log.warn("File {} cannot be {}.", metadataFilePath, what);
        final String fullErrorMessage = String.join(
                "", "File ", metadataFilePath.getPath(), " cannot be ", what, ". ",
                "If your SonarQube setup uses different file, then you can use sonar.scanner.metadataFilePath ",
                "system property or <sonar.scanner.metadataFilePath/> plugin configuration.");
        return new MojoExecutionException(fullErrorMessage);
    }
}
