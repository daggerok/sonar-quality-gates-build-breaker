package com.github.daggerok.sonarbreaker.maven.plugin;

import com.github.daggerok.sonarbreaker.Main;
import io.vavr.control.Try;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

@Mojo(
        name = "analyze",
        threadSafe = true,
        aggregator = false,
        requiresDirectInvocation = true,
        // defaultPhase = LifecyclePhase.VERIFY,
        requiresDependencyResolution = ResolutionScope.TEST
)
public class SonarBreakerMojo extends AbstractMojo {

    @Parameter(defaultValue = "${maven.multiModuleProjectDirectory}")
    private String multiModuleBaseDirname;

    @Parameter(defaultValue = "${maven.multiModuleProjectDirectory}")
    private File multiModuleBaseDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Path reportTaskTxt = Paths.get(multiModuleBaseDirname, "target", "sonar", "report-task.txt");
        log("{} analysis...", reportTaskTxt);
        final String[] args = Stream.of(reportTaskTxt.toFile().getAbsolutePath()).toArray(String[]::new);
        Try.run(() -> Main.main(args))
           .onFailure(throwable -> getLog().error(throwable.getLocalizedMessage(), throwable))
           .andFinally(() -> log("Analysis done!"));
    }

    private void log(String message, Object... variables) {
        Objects.requireNonNull(variables, "log variables may not be null or empty.");
        final Log log = getLog();
        if (!log.isInfoEnabled()) return;
        if (variables.length < 1) log.info(message);
        else {
            final String replaced = message.replaceAll("\\{}", "%s");
            log.info(String.format(replaced, variables));
        }
    }
}
