package com.github.daggerok.sonarbreaker.sonar.rest.api.ce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskResponse {
    private Task task;
}
