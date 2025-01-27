package com.thesis.periodtracker.Rasa;

import com.google.gson.*;
import com.thesis.periodtracker.Rasa.responses.ImageResponse;
import com.thesis.periodtracker.Rasa.responses.ImpressionResponse;
import com.thesis.periodtracker.Rasa.responses.RasaResponse;
import com.thesis.periodtracker.Rasa.responses.RestartResponse;
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
            switch (control) {
                case "record_condition":
                    // Deserialize as ConditionResponse
                    return new Gson().fromJson(json, ImpressionResponse.class);
                case "record_symptom":
                    // Deserialize as SymptomResponse
                    return new Gson().fromJson(json, SymptomResponse.class);
                case "record_user_info":
                    // Deserialize as UserInfoResponse
                    return new Gson().fromJson(json, UserInfoResponse.class);
                case "create_new_session":
                    // Deserialize as RestartResponse
                    return new Gson().fromJson(json, RestartResponse.class);
                default:
                    throw new JsonParseException("Unknown custom response  type");
            }
        } else {
            throw new JsonParseException("Unknown response type");
        }
    }
}
