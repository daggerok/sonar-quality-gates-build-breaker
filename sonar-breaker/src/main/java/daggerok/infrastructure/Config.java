package daggerok.infrastructure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Config {

    public static String get(String prop, String defaultValue, String... args) {
        String environmentVariableName = prop.replaceAll("\\.", "_").toUpperCase();
        String environmentVariable = System.getenv().getOrDefault(environmentVariableName, defaultValue);
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
