package com.github.daggerok.sonarbreaker.maven.plugin;

import com.github.daggerok.sonarbreaker.Main;
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
     * Base multi-module (root) project directory. It can be used to calculate in runtime where
     * target/sonar/report-task.txt file should be located after sonar:sonar goal execution.
     * <hr/>
     * Default value: ${mavep.multiModuleProjectDirectory}
     * Requires: maven >= 3.3.9
     */
    @Parameter(property = "sonar.breaker.root", defaultValue = "${maven.multiModuleProjectDirectory}")
    private String multiModuleBaseDirname;

    /**
     * SonarQube analysis result (goal sonar:sonar).
     * As result file target/sonar/report-task.txt should be generated.
     * <hr/>
     * Default value: null
     * Fallback value: ${sonar.breaker.root}/target/sonar/report-task.txt
     * Requires: maven >= 3.3.9
     */
    @Parameter(property = "sonar.breaker.reportTaskTxt"/*,
               defaultValue = "${maven.multiModuleProjectDirectory}/target/sonar/report-task.txt"*/)
    private File reportTaskTxt;

    /**
     * Specify whenever plugin execution can fail.
     * <hr/>
     * Default value: false
     */
    @Parameter(property = "sonar.breaker.failOnError", defaultValue = "false")
    private boolean allowFailure;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        final File sonarReportFile = findSonarQubeReportTaskTxtFile();
        final Function<String, MojoExecutionException> error = what -> {
            if (log.isWarnEnabled()) log.warn("File {} cannot be {}.", sonarReportFile, what);
            return new MojoExecutionException(String.join(
                    "", "File ", sonarReportFile.getPath(), " cannot be ", what, ". ",
                    "If your SonarQube setup uses different file, then you can use sonar.breaker.reportTaskTxt ",
                    "system property or <reportTaskTxt/> plugin configuration."
            ));
        };

        if (!sonarReportFile.exists() && allowFailure) error.apply("found").getLocalizedMessage();
        else if (!sonarReportFile.exists() && !allowFailure) throw error.apply("found");

        if (!sonarReportFile.canRead() && allowFailure) error.apply("read").getLocalizedMessage();
        else if (!sonarReportFile.canRead() && !allowFailure) throw error.apply("read");

        final String path = sonarReportFile.getAbsolutePath();
        log.info("{} analysis...", path);

        final Try<Void> aTry = Try.run(() -> Main.main(Stream.of(path).toArray(String[]::new)))
                                  .onFailure(e -> log.warn("{}\n{}", e.getLocalizedMessage(), e))
                                  .andFinally(() -> log.info("Analysis done!"));

        if (aTry.isSuccess()) {
            aTry.get();
            return;
        }

        final Throwable cause = aTry.getCause();
        final String message = cause.getLocalizedMessage();

        if (allowFailure) log.warn("Ignored failed execution: {}", message, cause);
        else throw new MojoExecutionException(String.join(" ", "sonar-breaker:analysis goal failed", message));
    }

    private File findSonarQubeReportTaskTxtFile() {
        return Optional.ofNullable(reportTaskTxt)
                       .orElse(Paths.get(multiModuleBaseDirname, "target", "sonar", "report-task.txt")
                                    .toFile());
    }
}
