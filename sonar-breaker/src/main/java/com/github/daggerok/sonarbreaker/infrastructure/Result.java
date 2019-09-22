package com.github.daggerok.sonarbreaker.infrastructure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
public enum Result {

    BUILD_SUCCESS("BUILD SUCCESS!", 0),
    USAGE("BUILD FAILED! Usage: java -jar verify-sonar.jar path/to/sonar/report-task.txt", 1),
    TIMEOUT_EXCEEDED("BUILD FAILED! Timeout exceeded", 2),
    QUALITY_GATES_RESPONSE_FAILED("BUILD FAILED! Quality gates response failed", 3),
    QUALITY_GATES_EMPTY_RESPONSE("BUILD FAILED! Quality gates returns empty response", 4),
    QUALITY_GATES_STATUS_FAILED("BUILD FAILED! No quality gates status found", 5),
    QUALITY_GATES_FAILED("BUILD FAILED! Verify quality gates status", 6),
    ;

    private final String message;

    @Getter
    private final int exitCode;

    public void fail(final String... messages) {
        log.error(message);
        Arrays.stream(messages)
              .filter(Objects::nonNull)
              .map(Objects::toString)
              .forEach(log::error);
        System.exit(exitCode);
    }

    public void complete() {
        log.info(BUILD_SUCCESS.message);
        System.exit(BUILD_SUCCESS.exitCode);
    }
}
