package com.rocket.quizzy.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Quiz {

    String id;
    String quizTitle;
    String quizThumbnail;
    String categoryId;
    ArrayList<Question> questions;
    String quizDescription;
    String[] tags;
    boolean isTop;

    public Quiz() {
    }

    public Quiz(String id, String quizTitle, String quizThumbnail, String categoryId , ArrayList<Question> questions, String quizDescription, boolean isTop, String[] tags) {
        this.id = id;
        this.quizTitle = quizTitle;
        this.quizThumbnail = quizThumbnail;
        this.questions = questions;
        this.quizDescription = quizDescription;
        this.isTop = isTop;
        this.tags = tags;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public String getQuizThumbnail() {
        return quizThumbnail;
    }

    public void setQuizThumbnail(String quizThumbnail) {
        this.quizThumbnail = quizThumbnail;
    }

    public ArrayList<Question> getList() {
        return questions;
    }

    public void setList(ArrayList<Question> list) {
        this.questions = list;
    }

    public String getQuizDescription() {
        return quizDescription;
    }

    public void setQuizDescription(String quizDescription) {
        this.quizDescription = quizDescription;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
