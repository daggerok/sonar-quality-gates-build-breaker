package com.github.daggerok.sonarbreaker.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Config {

    public static String get(final String prop, final String defaultValue, final String... args) {
        final String environmentVariableName = prop.replaceAll("\\.", "_").toUpperCase();
        final String environmentVariable = System.getenv().getOrDefault(environmentVariableName, defaultValue);
        return Arrays.stream(args)
                     .filter(Objects::nonNull)
                     .map(String::trim)
                     .filter(s -> !s.isEmpty())
                     .filter(s -> s.startsWith(prop))
                     .map(s -> s.split("="))
                     .map(pair -> pair[1])
                     .findFirst()
                     .orElse(System.getProperty(prop, environmentVariable));
    }
}
