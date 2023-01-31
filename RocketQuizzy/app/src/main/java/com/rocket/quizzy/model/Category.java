package com.rocket.quizzy.model;

public class Category {
    String id;
    String name;
    String thumbnail;
    boolean isEnabled;

    public Category() {
    }

    public Category(String id, String name, String thumbnail, boolean isEnabled) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
        this.isEnabled = isEnabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
