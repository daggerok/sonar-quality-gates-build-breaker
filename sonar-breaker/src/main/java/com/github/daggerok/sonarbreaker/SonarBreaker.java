package com.github.daggerok.sonarbreaker;

import com.github.daggerok.sonarbreaker.infrastructure.Config;
import com.github.daggerok.sonarbreaker.infrastructure.Env;
import com.github.daggerok.sonarbreaker.infrastructure.Result;
import com.github.daggerok.sonarbreaker.sonar.fs.SonarReportTask;
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
 * java -Dsonar.breaker.ignores=new_coverage -jar sonar-breaker.jar ./target/sonar/report-task.txt
 * SONAR_BREAKER_METRICS_EXCLUDES=new_coverage java -jar sonar-breaker.jar ./target/sonar/report-task.txt
 */
@Log4j2
public class SonarBreaker {

    @SneakyThrows
    public static void main(final String[] args) { // NOSONAR
        requireNonNull(args, "args may not be null."); // internal use

        if (args.length != 1) {
            final String actual = String.join(", ", args);
            final String error = format("arguments amount: %d (%s)", args.length, actual);
            Result.USAGE.fail(error);
            return;
        }

        final SonarReportTask sonarReportTask = SonarReportTask.of(args[0]);
        final SonarClient sonar = new Retrofit.Builder()
                .baseUrl(sonarReportTask.getServerUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(SonarClient.class);

        final int maxRetries = Integer.parseInt(Config.get(Env.SONAR_BREAKER_RETRY));
        final int delay = Integer.parseInt(Config.get(Env.SONAR_BREAKER_DELAY));

        String analysisId = null;
        int retry = maxRetries;

        while (analysisId == null && retry-- > 0) {

            final val analysis = sonar.getTask(sonarReportTask.getCeTaskId());
            final Response<TaskResponse> response = analysis.execute();

            if (!response.isSuccessful()) {
                String error = new String(requireNonNull(response.errorBody(), "response failed.").bytes());
                Result.QUALITY_GATES_RESPONSE_FAILED.fail(error);
                return;
            }

            final TaskResponse body = response.body();
            final String status = requireNonNull(body, "oops 3.5").getTask().getStatus();
            log.debug("{} retries left for analysis ({})", retry, status.toLowerCase().replaceAll("_", " "));

            if (!asList("SUCCESS", "FAILED").contains(status)) {
                TimeUnit.SECONDS.sleep(delay);
                if (retry > 0) continue;

                final String error = format("stop waiting after %s tries", maxRetries);
                Result.TIMEOUT_EXCEEDED.fail(error);
                return;
            }

            analysisId = body.getTask().getAnalysisId();
        }

        final val call = sonar.getProjectStatus(analysisId);
        final Response<ProjectStatusResponse> response = call.execute();
        log.debug("Quality gates status available here: {}", call.request().url());

        if (!response.isSuccessful()) {
            final String error = format("%n%s", requireNonNull(response.errorBody(), "response failed.").string());
            Result.QUALITY_GATES_RESPONSE_FAILED.fail(error);
            return;
        }

        final val body = response.body();
        final val maybeBody = Optional.ofNullable(body);

        if (!maybeBody.isPresent()) {
            Result.QUALITY_GATES_EMPTY_RESPONSE.fail();
            return;
        }

        final val maybeProjectStatus = maybeBody.map(ProjectStatusResponse::getProjectStatus);
        final val maybeStatus = maybeProjectStatus.map(ProjectStatus::getStatus);

        if (!maybeStatus.isPresent()) {
            Result.QUALITY_GATES_STATUS_FAILED.fail();
            return;
        }

        final String status = maybeStatus.get();
        final val projectStatus = maybeProjectStatus.get();

        if (!"OK".equalsIgnoreCase(status)) {
            final val hasErrors = projectStatus.failedConditions().size() > 0;
            if (hasErrors) {
                Result.QUALITY_GATES_FAILED.fail();
                return;
            }
        }

        Result.BUILD_SUCCESS.complete();
    }
}
