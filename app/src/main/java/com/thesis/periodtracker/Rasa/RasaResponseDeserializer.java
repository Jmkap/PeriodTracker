package com.thesis.periodtracker.Rasa;

import com.google.gson.*;
import com.thesis.periodtracker.Rasa.responses.ConditionResponse;
import com.thesis.periodtracker.Rasa.responses.RasaResponse;
import com.thesis.periodtracker.Rasa.responses.TextResponse;

import java.lang.reflect.Type;

public class RasaResponseDeserializer implements JsonDeserializer<RasaResponse> {
    @Override
    public RasaResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("text")) {
            return new Gson().fromJson(json, TextResponse.class);  // Text Response
        } else if (jsonObject.has("custom")) {
            return new Gson().fromJson(json, ConditionResponse.class);  // Custom JSON
        } else {
            throw new JsonParseException("Unknown response type");
        }
    }
}
