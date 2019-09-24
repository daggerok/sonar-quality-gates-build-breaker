package com.github.daggerok.sonarbreaker.infrastructure;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Env {
    SONAR_SCANNER_METADATA_FILE_PATH("sonar.scanner.metadataFilePath", "target/sonar/report-task.txt"),
    SONAR_BREAKER_STANDALONE("sonar.breaker.standalone", "true"),
    SONAR_BREAKER_RETRY("sonar.breaker.retry", "100"),
    SONAR_BREAKER_DELAY("sonar.breaker.delay", "1"),
    SONAR_BREAKER_METRICS_INCLUDES("sonar.breaker.metrics.includes", ""),
    SONAR_BREAKER_METRICS_EXCLUDES("sonar.breaker.metrics.excludes", "");
    public final String systemProperty;
    public final String value;
}
