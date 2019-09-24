package com.github.daggerok.sonarbreaker.maven.plugin;

import com.github.daggerok.sonarbreaker.SonarBreaker;
import io.vavr.collection.HashMap;
import io.vavr.control.Try;
import lombok.extern.log4j.Log4j2;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.daggerok.sonarbreaker.infrastructure.Env.*;
import static io.vavr.Predicates.not;

/**
 * Waiting for sonar:sonar analysis completion and failing execution in case if
 * any related to current project Quality Gates metrics wont accepts defined thresholds
 *
 * @goal sonar-breaker:analyze
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
     * Max number of retries to be attempt for SonarQube REST API
     * calls during waiting for analysis completion
     */
    @Parameter(property = "sonar.breaker.retry")
    private String sonarBreakerRetry;

    /**
     * Time interval in seconds between SonarQube REST API calls
     * to be sent to SonarQube REST API during waiting for analysis
     * completion
     */
    @Parameter(property = "sonar.breaker.delay")
    private String sonarBreakerDelay;

    /**
     * List of metrics divided by coma without spaces in between
     * from Quality Gates check to be included in result if
     * their status is ERROR
     */
    @Parameter(property = "sonar.breaker.metrics.includes")
    private String sonarBreakerMetricsIncludes;

    /**
     * List of metrics divided by coma without spaces in between to
     * be excluded from Quality Gates check results even if status
     * is ERROR
     */
    @Parameter(property = "sonar.breaker.metrics.excludes")
    private String sonarBreakerMetricsExcludes;

    /**
     * Base multi-module (root) project directory. It can be used to calculate in runtime where
     * target/sonar/report-task.txt file should be located after sonar:sonar goal execution.
     * <hr/>
     * Default value: ${mavep.multiModuleProjectDirectory}
     * Requires: maven >= 3.3.9
     */
    @Parameter(property = "sonar.projectBaseDir", defaultValue = "${maven.multiModuleProjectDirectory}")
    private String sonarProjectBaseDir;

    /**
     * SonarQube analysis result (goal sonar:sonar).
     * As result file target/sonar/report-task.txt should be generated.
     * <hr/>
     * Default value: null
     * Fallback value: ${sonar.projectBaseDir}/target/sonar/report-task.txt
     * Requires: maven >= 3.3.9
     */
    @Parameter(property = "sonar.scanner.metadataFilePath"/*,
               defaultValue = "${maven.multiModuleProjectDirectory}/target/sonar/report-task.txt"*/)
    private File sonarScannerMetadataFilePath;

    /**
     * Specify whenever plugin execution can fail.
     * <hr/>
     * Default value: false
     */
    @Parameter(property = "sonar.breaker.failOnError", defaultValue = "false")
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
        final Function<String, MojoExecutionException> error = what -> {
            if (log.isWarnEnabled()) log.warn("File {} cannot be {}.", sonarReportFile, what);
            return new MojoExecutionException(String.join(
                    "", "File ", sonarReportFile.getPath(), " cannot be ", what, ". ",
                    "If your SonarQube setup uses different file, then you can use sonar.scanner.metadataFilePath ",
                    "system property or <sonar.scanner.metadataFilePath/> plugin configuration."
            ));
        };

        if (!sonarReportFile.exists()) {
            if (!allowFailure) throw error.apply("found");
            error.apply("found").getLocalizedMessage();
            log.warn("Skipping build allowed to fail...");
            return;
        }

        if (!sonarReportFile.canRead()) {
            if (!allowFailure) throw error.apply("read");
            error.apply("read").getLocalizedMessage();
            log.warn("Skipping build allowed to fail...");
            return;
        }

        final String path = sonarReportFile.getAbsolutePath();
        log.debug("Resolved metadata file: {}", () -> path);

        final Try<Void> aTry = Try.run(() -> SonarBreaker.main(Stream.of(path).toArray(String[]::new)))
                                  .onFailure(e -> log.error("{}\n{}", e.getLocalizedMessage(), e))
                                  .andFinally(() -> log.info(() -> "Analysis done!"));
        if (aTry.isSuccess()) {
            aTry.get();
            return;
        }

        final Throwable cause = aTry.getCause();
        final String message = cause.getLocalizedMessage();

        if (allowFailure) log.warn("Ignored failed execution: {}", message, cause);
        else throw new MojoExecutionException(String.join(" ", "sonar-breaker:analyze goal failed", message));
    }

    private void mapPluginProps() {
        HashMap.of(SONAR_BREAKER_DELAY, sonarBreakerDelay,
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
}
