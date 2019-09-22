package com.github.daggerok.sonarbreaker.sonar.rest.api.qualitygates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.daggerok.sonarbreaker.sonar.rest.api.qualitygates.projectstatus.Condition;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectStatus {

    private String status;
    private Collection<Condition> conditions = new CopyOnWriteArrayList<>();

    public String[] failedConditions() {
        return conditions.stream()
                         .filter(Condition::isBroken)
                         .map(c -> String.format("%s: %s(%s)",
                                                 c.getMetricKey().replaceAll("_", " "),
                                                 c.getErrorThreshold(),
                                                 c.getActualValue()))
                         .toArray(String[]::new);
    }
}