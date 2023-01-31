package com.rocket.quizzy.model;

public class Option {
    int id;
    String optionText;
    String optionType;
    String optionUrl;

    public static String OPTION_TEXT = "TEXT";
    public static String OPTION_IMAGE = "IMAGE";
    public static String OPTION_AUDIO = "AUDIO";

    public Option(int id, String optionText, String optionType, String optionUrl) {
        this.id = id;
        this.optionText = optionText;
        this.optionType = optionType;
        this.optionUrl = optionUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public String getOptionUrl() {
        return optionUrl;
    }

    public void setOptionUrl(String optionUrl) {
        this.optionUrl = optionUrl;
    }
}
