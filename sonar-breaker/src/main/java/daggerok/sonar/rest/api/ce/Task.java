package daggerok.sonar.rest.api.ce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {
    private String id;
    private String type;
    private String componentId;
    private String componentKey;
    private String componentName;
    private String componentQualifier;
    private String analysisId;
    private String status;
    private String submittedAt;
    private String submitterLogin;
    private String startedAt;
    private String executedAt;
    private Long executionTimeMs;
    private boolean logs;
    private boolean hasScannerContext;
    private String organization;
    private Long warningCount;
}
