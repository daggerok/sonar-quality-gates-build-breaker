package com.github.daggerok.sonarbreaker.sonar.rest.api.qualitygates.projectstatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Period {
    private Integer index;
    private String mode;
    private String date;
}
