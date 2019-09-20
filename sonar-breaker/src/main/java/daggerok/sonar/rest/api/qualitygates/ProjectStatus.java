package daggerok.sonar.rest.api.qualitygates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import daggerok.sonar.rest.api.qualitygates.projectstatus.Condition;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.stream.Collectors.joining;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectStatus {

    private String status;
    private Collection<Condition> conditions = new CopyOnWriteArrayList<>();

    public String failedConditions() {
        return conditions.stream()
                         .filter(Condition::isBroken)
                         .map(c -> String.format("%s: %s(%s)",
                                                 c.getMetricKey().replaceAll("_", " "),
                                                 c.getErrorThreshold(),
                                                 c.getActualValue()))
                         .collect(joining("\n"));
    }
}
