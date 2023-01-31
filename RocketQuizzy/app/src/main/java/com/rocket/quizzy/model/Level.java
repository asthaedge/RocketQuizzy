package com.rocket.quizzy.model;

public class Level {
    int level;
    boolean achieved;
    String levelTitle;

    public Level(int level, boolean achieved, String levelTitle) {
        this.level = level;
        this.achieved = achieved;
        this.levelTitle = levelTitle;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }

    public String getLevelTitle() {
        return levelTitle;
    }

    public void setLevelTitle(String levelTitle) {
        this.levelTitle = levelTitle;
    }
}
