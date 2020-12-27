package org.example.Bot.Translation;

import java.util.List;

public class TranslationAzureResponse {
    public DetectedLanguage detectedLanguage;
    public List<InnerTranslation> translations;

    public DetectedLanguage getDetectedLanguage() {
        return detectedLanguage;
    }

    public void setDetectedLanguage(DetectedLanguage detectedLanguage) {
        this.detectedLanguage = detectedLanguage;
    }

    public List<InnerTranslation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<InnerTranslation> translations) {
        this.translations = translations;
    }

    public class DetectedLanguage {
        public String language;
        public double score;

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }

    public class InnerTranslation {
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String text;
        public String to;
    }
}
