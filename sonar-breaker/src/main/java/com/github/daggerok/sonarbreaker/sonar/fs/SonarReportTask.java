package com.github.daggerok.sonarbreaker.sonar.fs;

import com.github.daggerok.sonarbreaker.infrastructure.Config;
import com.github.daggerok.sonarbreaker.infrastructure.Env;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SonarReportTask {

    private final Map<String, String> dataHolder = new ConcurrentHashMap<>();

    public static SonarReportTask of(final String filePath) {
        final String path = Optional.ofNullable(filePath)
                                    .orElse(Config.get(Env.SONAR_REPORT_TASK));
        return new SonarReportTask().withProperties(path);
    }

    public String getServerUrl() {
        return get("serverUrl");
    }

    public String getCeTaskId() {
        return get("ceTaskId");
    }

    private String get(final String key) {
        return dataHolder.getOrDefault(key, "");
    }

    @SneakyThrows
    private SonarReportTask withProperties(final String filePath) {
        log.debug("Reading {}", filePath);
        // final @Cleanup FileInputStream fileInputStream = new FileInputStream(filePath);
        // final Properties properties = new Properties();
        // properties.load(fileInputStream);
        // dataHolder.putAll(properties.entrySet()
        //                             .stream()
        //                             .collect(Collectors.toMap(e -> e.getKey().toString(),
        //                                                       e -> e.getValue().toString())));
        try (final Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.map(String::trim)
                  .peek(log::debug)
                  .filter(s -> s.contains("="))
                  .map(s -> s.split("="))
                  .collect(Collectors.toMap(e -> e[0], e -> e[1]))
                  .forEach(dataHolder::put);
            return this;
        }
    }
}