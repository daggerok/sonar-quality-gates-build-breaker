package com.github.daggerok.sonarbreaker;

import com.github.daggerok.sonarbreaker.infrastructure.Config;
import com.github.daggerok.sonarbreaker.infrastructure.Result;
import com.github.daggerok.sonarbreaker.sonar.fs.ReportTask;
import com.github.daggerok.sonarbreaker.sonar.rest.SonarClient;
import com.github.daggerok.sonarbreaker.sonar.rest.api.ce.TaskResponse;
import com.github.daggerok.sonarbreaker.sonar.rest.api.qualitygates.ProjectStatus;
import com.github.daggerok.sonarbreaker.sonar.rest.api.qualitygates.ProjectStatusResponse;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * java -jar sonar-breaker.jar ./target/sonar/report-task.txt
 * java -Dsonar.breaker.retry=25 -jar sonar-breaker.jar ./target/sonar/report-task.txt
 * java -Dsonar.breaker.delay=5 -Dsonar.breaker.retry=5 -jar sonar-breaker.jar ./target/sonar/report-task.txt
 */
@Log4j2
public class Main {

    @SneakyThrows
    public static void main(final String[] args) { // NOSONAR
        requireNonNull(args, "args may not be null."); // internal use

        if (args.length != 1) {
            final String actual = String.join(", ", args);
            final String error = format("arguments amount: %d (%s)", args.length, actual);
            Result.USAGE.withMessage(error).fail();
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
                Result.QUALITY_GATES_RESPONSE_FAILED.withMessage(error).fail();
                return;
            }

            final TaskResponse body = response.body();
            final String status = requireNonNull(body, "oops 3.5").getTask().getStatus();
            log.info("{} retries left for analysis ({})", retry, status.toLowerCase().replaceAll("_", " "));

            if (!asList("SUCCESS", "FAILED").contains(status)) {
                TimeUnit.SECONDS.sleep(delay);
                if (retry > 0) continue;

                final String error = format("stop waiting after %s tries", maxRetries);
                Result.TIMEOUT_EXCEEDED.withMessage(error).fail();
                return;
            }

            analysisId = body.getTask().getAnalysisId();
        }

        final val call = sonar.getProjectStatus(analysisId);
        final Response<ProjectStatusResponse> response = call.execute();

        if (!response.isSuccessful()) {
            final String error = format("%n%s", requireNonNull(response.errorBody(), "response failed.").string());
            Result.QUALITY_GATES_RESPONSE_FAILED.withMessage(error).fail();
            return;
        }

        ProjectStatusResponse body = response.body();
        final val maybeBody = Optional.ofNullable(body);
        if (!maybeBody.isPresent()) {
            Result.QUALITY_GATES_EMPTY_RESPONSE.withMessage().fail();
            return;
        }

        final val maybeProjectStatus = maybeBody.map(ProjectStatusResponse::getProjectStatus);
        final val maybeStatus = maybeProjectStatus.map(ProjectStatus::getStatus);
        if (!maybeStatus.isPresent()) {
            Result.QUALITY_GATES_STATUS_FAILED.withMessage().fail();
            return;
        }

        final String status = maybeStatus.get();
        if (!"OK".equalsIgnoreCase(status)) {
            final ProjectStatus ps = maybeProjectStatus.get();
            Result.QUALITY_GATES_FAILED.withMessage(ps.failedConditions()).fail();
            return;
        }

        Result.BUILD_SUCCESS.withMessage().done();
    }
}
