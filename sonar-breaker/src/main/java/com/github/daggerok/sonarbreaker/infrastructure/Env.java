package com.github.daggerok.sonarbreaker.infrastructure;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Env {
    SONAR_REPORT_TASK("sonar.report.task", "target/sonar/report-task.txt"),
    SONAR_BREAKER_RETRY("sonar.breaker.retry", "100"),
    SONAR_BREAKER_DELAY("sonar.breaker.delay", "1"),
    SONAR_BREAKER_METRICS_INCLUDES("sonar.breaker.metrics.includes", ""),
    SONAR_BREAKER_METRICS_EXCLUDES("sonar.breaker.metrics.excludes", "");
    public final String systemProperty;
    public final String value;
}
