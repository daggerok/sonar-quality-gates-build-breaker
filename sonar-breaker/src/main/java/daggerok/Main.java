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

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static daggerok.infrastructure.Result.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.fill;
import static java.util.Objects.requireNonNull;

/**
 * java -jar sonar-breaker.jar ./target/sonar/report-task.txt
 * java -Dsonar.breaker.retry=25 -jar sonar-breaker.jar ./target/sonar/report-task.txt
 * java -Dsonar.breaker.delay=5 -Dsonar.breaker.retry=5 -jar sonar-breaker.jar ./target/sonar/report-task.txt
 */
public class Main {

    @SneakyThrows
    public static void main(final String[] args) {
        requireNonNull(args, "args may not be null."); // internal check

        if (args.length != 1) {
            final String actual = String.join(", ", args);
            final String error = format("arguments amount: %d (%s)", args.length, actual);
            USAGE.withMessage(error).fail();
            return;
        }

        final ReportTask reportTask = ReportTask.of(args[0]);
        reportTask.printBuildInfo();

        final SonarClient sonar = new Retrofit.Builder()
                .baseUrl(reportTask.getServerUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(SonarClient.class);

        final int maxRetries = Integer.parseInt(Config.get("sonar.breaker.retry", "100"));
        final int delay = Integer.parseInt(Config.get("sonar.breaker.delay", "1"));

        String analysisId = null;
        int retry = maxRetries;

        while (analysisId == null && retry-- > 0) {

            final val analysis = sonar.getTask(reportTask.getCeTaskId());
            final Response<TaskResponse> response = analysis.execute();

            if (!response.isSuccessful()) {
                String error = new String(requireNonNull(response.errorBody(), "response failed.").bytes());
                QUALITY_GATES_RESPONSE_FAILED.withMessage(error).fail();
                return;
            }

            final TaskResponse body = response.body();
            final String status = requireNonNull(body, "oops 3.5").getTask().getStatus();
            System.out.printf("%d attempts left for analysis (%s)%n", retry, status.toLowerCase().replaceAll("_", " "));

            if (!asList("SUCCESS", "FAILED").contains(status)) {
                TimeUnit.SECONDS.sleep(delay);
                if (retry > 0) continue;

                final String error = format("stop waiting after %s retries", maxRetries);
                TIMEOUT_EXCEEDED.withMessage(error).fail();
                return;
            }

            analysisId = body.getTask().getAnalysisId();
        }

        final val call = sonar.getProjectStatus(analysisId);
        final Response<ProjectStatusResponse> response = call.execute();

        if (!response.isSuccessful()) {
            final String error = format("%n%s", requireNonNull(response.errorBody(), "response failed.").string());
            QUALITY_GATES_RESPONSE_FAILED.withMessage(error).fail();
            return;
        }

        ProjectStatusResponse body = response.body();
        final val maybeBody = Optional.ofNullable(body);
        if (!maybeBody.isPresent()) {
            QUALITY_GATES_EMPTY_RESPONSE.withMessage().fail();
            return;
        }

        final val maybeProjectStatus = maybeBody.map(ProjectStatusResponse::getProjectStatus);
        final val maybeStatus = maybeProjectStatus.map(ProjectStatus::getStatus);
        if (!maybeStatus.isPresent()) {
            QUALITY_GATES_STATUS_FAILED.withMessage().fail();
            return;
        }

        final String status = maybeStatus.get();
        if (!"OK".equalsIgnoreCase(status)) {
            final ProjectStatus ps = maybeProjectStatus.get();
            QUALITY_GATES_FAILED.withMessage(ps.failedConditions()).fail();
            return;
        }

        BUILD_SUCCESS.withMessage().done();
    }
}
