package daggerok;

import daggerok.infrastructure.Config;
import daggerok.sonar.fs.ReportTask;
import daggerok.sonar.rest.SonarClient;
import daggerok.sonar.rest.api.ce.TaskResponse;
import daggerok.sonar.rest.api.qualitygates.ProjectStatus;
import daggerok.sonar.rest.api.qualitygates.ProjectStatusResponse;
import lombok.SneakyThrows;
import lombok.val;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static daggerok.infrastructure.Result.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * java -jar sonar-breaker.jar ./target/sonar/report-task.txt
 * java -Dsonar.retry=25 -jar sonar-breaker.jar ./target/sonar/report-task.txt
 * java -Dsonar.delay=5 -Dsonar.retry=5 -jar sonar-breaker.jar ./target/sonar/report-task.txt
 */
public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        requireNonNull(args, "args may not be null."); // internal check

        if (args.length != 1) {
            String error = format("arguments amount: %d.", args.length);
            USAGE.withMessage(error).fail();
            return;
        }

        ReportTask reportTask = ReportTask.of(args[0]);
        reportTask.printBuildInfo();

        SonarClient sonar = new Retrofit.Builder()
                .baseUrl(reportTask.getServerUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(SonarClient.class);

        String analysisId = null;
        int retry = Integer.parseInt(Config.get("sonar.retry", "100"));
        int delay = Integer.parseInt(Config.get("sonar.delay", "1"));

        while (analysisId == null && retry-- > 0) {

            val analysis = sonar.getTask(reportTask.getCeTaskId());
            Response<TaskResponse> response = analysis.execute();

            if (!response.isSuccessful()) {
                String error = new String(requireNonNull(response.errorBody(), "response failed.").bytes());
                QUALITY_GATES_RESPONSE_FAILED.withMessage(error).fail();
                return;
            }

            TaskResponse body = response.body();
            String status = requireNonNull(body, "oops 3.5").getTask().getStatus();
            System.out.printf("%d attempts left for analysis (%s)%n", retry, status.toLowerCase().replaceAll("_", " "));

            if (!asList("SUCCESS", "FAILED").contains(status)) {
                TimeUnit.SECONDS.sleep(delay);
                if (retry > 0) continue;

                String error = format("stop waiting after %s retries", retry);
                TIMEOUT_EXCEEDED.withMessage(error).fail();
                return;
            }

            analysisId = body.getTask().getAnalysisId();
        }

        val call = sonar.getProjectStatus(analysisId);
        Response<ProjectStatusResponse> response = call.execute();

        if (!response.isSuccessful()) {
            String error = format("%n%s", requireNonNull(response.errorBody(), "response failed.").string());
            QUALITY_GATES_RESPONSE_FAILED.withMessage(error).fail();
            return;
        }

        ProjectStatusResponse body = response.body();
        val maybeBody = Optional.ofNullable(body);
        if (!maybeBody.isPresent()) {
            QUALITY_GATES_EMPTY_RESPONSE.withMessage().fail();
            return;
        }

        val maybeProjectStatus = maybeBody.map(ProjectStatusResponse::getProjectStatus);
        val maybeStatus = maybeProjectStatus.map(ProjectStatus::getStatus);
        if (!maybeStatus.isPresent()) {
            QUALITY_GATES_STATUS_FAILED.withMessage().fail();
            return;
        }

        String status = maybeStatus.get();
        if (!"OK".equalsIgnoreCase(status)) {
            ProjectStatus ps = maybeProjectStatus.get();
            QUALITY_GATES_FAILED.withMessage(ps.failedConditions()).fail();
            return;
        }

        BUILD_SUCCESS.withMessage().done();
    }
}
