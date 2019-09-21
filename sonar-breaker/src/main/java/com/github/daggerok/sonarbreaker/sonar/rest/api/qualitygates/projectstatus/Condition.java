package com.github.daggerok.sonarbreaker.sonar.rest.api.qualitygates.projectstatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Condition {

    private String status;
    private String metricKey;
    private String comparator;
    private Integer periodIndex;
    private String errorThreshold;
    private String actualValue;

    public boolean isBroken() {
        return Objects.isNull(metricKey) || !"OK".equalsIgnoreCase(metricKey);
    }
}
