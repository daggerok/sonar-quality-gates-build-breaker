package com.github.daggerok.sonarbreaker.infrastructure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Objects;

@Log4j2
@Getter
@RequiredArgsConstructor
public enum Result {

    BUILD_SUCCESS("BUILD SUCCESS!", 0),
    USAGE("BUILD FAILED! Usage: java -jar verify-sonar.jar path/to/sonar/report-task.txt", 1),
    TIMEOUT_EXCEEDED("BUILD FAILED! Timeout exceeded", 2),
    SONAR_ANALYSIS_FAILED("BUILD FAILED! Sonar analysis task response failed", 3),
    QUALITY_GATES_RESPONSE_FAILED("BUILD FAILED! Quality gates response failed", 4),
    QUALITY_GATES_STATUS_FAILED("BUILD FAILED! No quality gates status found", 5),
    QUALITY_GATES_FAILED("BUILD FAILED! Verify quality gates status", 6),
    ;

    private final String message;
    private final int exitCode;

    private void withErrors(final String... withErrors) {
        Arrays.stream(withErrors)
              .filter(Objects::nonNull)
              .map(Objects::toString)
              .forEach(log::error);
    }

    public void fail(final String... withErrors) {
        withErrors(withErrors);
        if (withErrors.length == 0) log.error(message);
        else log.error(message);

        final boolean shouldExitOtherwiseThrowAnError = Boolean.parseBoolean(Config.get(Env.SONAR_BREAKER_STANDALONE));
        if (shouldExitOtherwiseThrowAnError) System.exit(exitCode);

        throw new SonarBreakerException(String.join(", ", withErrors));
    }
}
