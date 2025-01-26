package com.thesis.periodtracker.Rasa.responses;

public class RestartResponse extends RasaResponse{
    private CustomData custom;

    public CustomData getCustom() {
        return custom;
    }

    public static class CustomData {
        private String control;
    }
}
