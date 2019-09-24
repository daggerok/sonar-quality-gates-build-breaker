package com.github.daggerok.sonarbreaker.sonar.rest.api.qualitygates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.daggerok.sonarbreaker.infrastructure.Config;
import com.github.daggerok.sonarbreaker.infrastructure.Env;
import com.github.daggerok.sonarbreaker.sonar.rest.api.qualitygates.projectstatus.Condition;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Data
@Log4j2
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectStatus {

    private String status;
    private Collection<Condition> conditions = new CopyOnWriteArrayList<>();

    public Collection<Condition> failedConditions() {

        final Predicate<Condition> basic = Condition::isBroken;

        final String includes = Config.get(Env.SONAR_BREAKER_METRICS_INCLUDES);
        final val withIncludes = includes.length() < 1 ? basic
                : basic.and(c -> asList(includes.split(",")).contains(c.getMetricKey()));

        final String excludes = Config.get(Env.SONAR_BREAKER_METRICS_EXCLUDES);
        final val withIncludesAndExcludes = excludes.length() < 1 ? withIncludes
                : withIncludes.and(c -> !asList(excludes.split(",")).contains(c.getMetricKey()));

        return conditions.stream()
                         .filter(withIncludesAndExcludes)
                         .peek(c -> log.error("{}: {} -> {}", c.getStatus(), c.getMetricKey(), c.getActualValue()))
                         .collect(Collectors.toList());
    }
}
