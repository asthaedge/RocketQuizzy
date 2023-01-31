package com.rocket.quizzy.model;

import java.util.ArrayList;

public class Question {
    String questionID;
    int questionLevel;
    String questionType;
    String questionText;
    String questionUrl;
    String optionType;
    String correctAnswer;
    ArrayList<String> optionsUrl;
    ArrayList<String> optionsText;

    public static String QUESTION_TEXT = "TEXT";
    public static String QUESTION_IMAGE = "IMAGE";
    public static String QUESTION_AUDIO = "AUDIO";

    public Question() {
    }

    public Question(String questionID, int questionLevel, String questionType, String questionText, String questionUrl, String optionType,String correctAnswer, ArrayList<String> optionsUrl, ArrayList<String> optionsText) {
        this.questionID = questionID;
        this.questionLevel = questionLevel;
        this.questionType = questionType;
        this.questionText = questionText;
        this.questionUrl = questionUrl;
        this.optionType = optionType;
        this.correctAnswer = correctAnswer;
        this.optionsUrl = optionsUrl;
        this.optionsText = optionsText;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public int getQuestionLevel() {
        return questionLevel;
    }

    public void setQuestionLevel(int questionLevel) {
        this.questionLevel = questionLevel;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionUrl() {
        return questionUrl;
    }

    public void setQuestionUrl(String questionUrl) {
        this.questionUrl = questionUrl;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public ArrayList<String> getOptionsUrl() {
        return optionsUrl;
    }

    public void setOptionsUrl(ArrayList<String> optionsUrl) {
        this.optionsUrl = optionsUrl;
    }

    public ArrayList<String> getOptionsText() {
        return optionsText;
    }

    public void setOptionsText(ArrayList<String> optionsText) {
        this.optionsText = optionsText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
