package com.thesis.periodtracker.Rasa.responses;

import com.google.gson.annotations.SerializedName;


public class SymptomResponse extends RasaResponse {
    private CustomData custom;

    public CustomData getCustom() {
        return custom;
    }

    public static class CustomData {
        private String control;
        private SymptomData data;

        public String getControl() {
            return control;
        }

        public SymptomData getData() {
            return data;
        }
    }

    public static class SymptomData {
        private String symptomName;
        private int duration;
        private int intensity;
        public String getSymptomName() {
            return symptomName;
        }
        public int getIntensity() {
            return intensity;
        }
        public int getDuration() {
            return duration;
        }
    }
}
