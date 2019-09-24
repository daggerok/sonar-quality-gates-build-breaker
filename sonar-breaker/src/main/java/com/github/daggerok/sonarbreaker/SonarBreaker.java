package com.github.daggerok.sonarbreaker;

import com.github.daggerok.sonarbreaker.infrastructure.Config;
import com.github.daggerok.sonarbreaker.infrastructure.Env;
import com.github.daggerok.sonarbreaker.infrastructure.Result;
import com.github.daggerok.sonarbreaker.infrastructure.SonarBreakerException;
import com.github.daggerok.sonarbreaker.sonar.fs.SonarMetadata;
import com.github.daggerok.sonarbreaker.sonar.rest.SonarRestClient;
import com.github.daggerok.sonarbreaker.sonar.rest.api.ce.TaskResponse;
import com.github.daggerok.sonarbreaker.sonar.rest.api.qualitygates.ProjectStatus;
import com.github.daggerok.sonarbreaker.sonar.rest.api.qualitygates.ProjectStatusResponse;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
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
        requireNonNull(args, "Arguments (args) may not be null."); // internal use

        if (args.length != 1) {
            usageError(args);
            return;
        }

        final SonarMetadata sonarMetadata = SonarMetadata.of(args[0]);
        final SonarRestClient restClient = sonarRestClient(sonarMetadata);
        final int maxRetries = Integer.parseInt(Config.get(Env.SONAR_BREAKER_RETRY));
        final int delay = Integer.parseInt(Config.get(Env.SONAR_BREAKER_DELAY));

        String analysisId = null;
        int retry = maxRetries;

        while (analysisId == null && retry-- > 0) {

            final val analysis = restClient.getTask(sonarMetadata.getCeTaskId());
            final Response<TaskResponse> response = analysis.execute();

            if (!response.isSuccessful()) {
                sonarAnalysisError(response);
                return;
            }

            final TaskResponse body = response.body();
            final String status = requireNonNull(body, "oops 3.5").getTask().getStatus();
            log.debug("Analysis {}, {} retries left", status.toLowerCase().replaceAll("_", " "), retry);

            if (!asList("SUCCESS", "FAILED").contains(status)) {
                TimeUnit.SECONDS.sleep(delay);
                if (retry > 0) continue;
                sonarAnalysisMaxRetriesExceededError(maxRetries);
                return;
            }

            analysisId = body.getTask().getAnalysisId();
        }

        final val call = restClient.getProjectStatus(analysisId);
        final Response<ProjectStatusResponse> response = call.execute();

        if (!response.isSuccessful()) {
            qualityGatesResponseError(response);
            return;
        }

        final val details = String.format("Details: %s", call.request().url());
        final val maybePs = Optional.ofNullable(response.body()).map(ProjectStatusResponse::getProjectStatus);
        final val maybeStatus = maybePs.map(ProjectStatus::getStatus);

        if (!maybeStatus.isPresent()) {
            Result.QUALITY_GATES_STATUS_FAILED.fail(details);
            return;
        }

        final String status = maybeStatus.orElseThrow(() -> new SonarBreakerException("Cannot get status"));
        final val projectStatus = maybePs.orElseThrow(() -> new SonarBreakerException("Cannot get project status"));

        if (!"OK".equalsIgnoreCase(status)) {
            final int failures = projectStatus.failedConditions().size();
            if (failures > 0) {
                qualityGatesError(failures, details);
                return;
            }
        }

        log.info(Result.BUILD_SUCCESS.getMessage());
    }

    private static void usageError(final String[] args) {
        final String error = format("Unsupported arguments amount: %d", args.length);
        Result.USAGE.fail(error);
    }

    private static SonarRestClient sonarRestClient(final SonarMetadata sonarMetadata) {
        return new Retrofit.Builder()
                .baseUrl(sonarMetadata.getServerUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(SonarRestClient.class);
    }

    private static void sonarAnalysisMaxRetriesExceededError(final int maxRetries) {
        final String error = format("stop waiting after %s tries", maxRetries);
        Result.TIMEOUT_EXCEEDED.fail(error);
    }

    private static void sonarAnalysisError(final Response<TaskResponse> response) throws IOException {
        final ResponseBody responseErrorBody = requireNonNull(response.errorBody(), "response failed");
        String error = new String(responseErrorBody.bytes());
        Result.SONAR_ANALYSIS_FAILED.fail(error);
    }

    private static void qualityGatesResponseError(final Response<ProjectStatusResponse> response) throws IOException {
        final String responseBodyString = requireNonNull(response.errorBody(), "response failed").string();
        final String error = format("%n%s", responseBodyString);
        Result.QUALITY_GATES_RESPONSE_FAILED.fail(error);
    }

    private static void qualityGatesError(final int failures, final String details) {
        final String bugs = failures > 1 ? "issues" : "issue";
        final String error = format("%d %s needs to be fixed! %s", failures, bugs, details);
        Result.QUALITY_GATES_FAILED.fail(error);
    }
}
