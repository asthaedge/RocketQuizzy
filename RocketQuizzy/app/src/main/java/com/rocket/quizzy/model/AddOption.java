package com.rocket.quizzy.model;

public class AddOption {

    int optionNo;
    String optionName;

    public AddOption(int optionNo, String optionName) {
        this.optionNo = optionNo;
        this.optionName = optionName;
    }

    public int getOptionNo() {
        return optionNo;
    }

    public void setOptionNo(int optionNo) {
        this.optionNo = optionNo;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
}
