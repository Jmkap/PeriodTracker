package com.thesis.periodtracker;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RasaApiService {
    @Headers("Content-Type: application/json")
    @POST("/webhooks/rest/webhook")
    Call<List<RasaResponse>> sendMessage(@Body RasaRequest rasaRequest);
}