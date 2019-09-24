package com.github.daggerok.sonarbreaker.sonar.rest;

import com.github.daggerok.sonarbreaker.sonar.rest.api.ce.TaskResponse;
import com.github.daggerok.sonarbreaker.sonar.rest.api.qualitygates.ProjectStatusResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SonarRestClient {
    @GET("/api/qualitygates/project_status")
    Call<ProjectStatusResponse> getProjectStatus(@Query("analysisId") String analysisId);

    @GET("/api/ce/task")
    Call<TaskResponse> getTask(@Query("id") String ceTaskId);
}
