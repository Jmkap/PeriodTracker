package com.thesis.periodtracker.Rasa;

import com.google.gson.*;
import com.thesis.periodtracker.Rasa.responses.ImageResponse;
import com.thesis.periodtracker.Rasa.responses.ImpressionResponse;
import com.thesis.periodtracker.Rasa.responses.RasaResponse;
import com.thesis.periodtracker.Rasa.responses.SymptomResponse;
import com.thesis.periodtracker.Rasa.responses.TextResponse;
import com.thesis.periodtracker.Rasa.responses.UserInfoResponse;

import java.lang.reflect.Type;

public class RasaResponseDeserializer implements JsonDeserializer<RasaResponse> {
    @Override
    public RasaResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("text")) {
            return new Gson().fromJson(json, TextResponse.class);  // Text Response
        } else if (jsonObject.has("image")) {
            return new Gson().fromJson(json, ImageResponse.class);  // Image response
        } else if (jsonObject.has("custom")) {
            JsonObject customObject = jsonObject.getAsJsonObject("custom");
            String control = customObject.get("control").getAsString();

            // Check the control type to determine the response type
            if ("record_condition".equals(control)) {
                // Deserialize as ConditionResponse
                return new Gson().fromJson(json, ImpressionResponse.class);
            } else if ("record_symptom".equals(control)) {
                // Deserialize as SymptomResponse
                return new Gson().fromJson(json, SymptomResponse.class);
            } else if ("record_user_info".equals(control)) {
                // Deserialize as UserInfoResponse
                return new Gson().fromJson(json, UserInfoResponse.class);
            } else {
                throw new JsonParseException("Unknown custom response type");
            }
        } else {
            throw new JsonParseException("Unknown response type");
        }
    }
}
