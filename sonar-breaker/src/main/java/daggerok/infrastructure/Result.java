package daggerok.infrastructure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public Result withMessage(final Object... messages) {
        System.out.println(message);
        System.out.println(Arrays.stream(messages)
                                 .filter(Objects::nonNull)
                                 .map(Objects::toString)
                                 .collect(Collectors.joining("\n")));
        return this;
    }

    public void fail() {
        System.exit(exitCode);
    }

    public void done() {
        System.exit(BUILD_SUCCESS.exitCode);
    }
}
