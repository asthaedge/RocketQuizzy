package com.rocket.quizzy.model;

public class Badge {
    int level;
    boolean achieved;

    public Badge(int level, boolean achieved) {
        this.level = level;
        this.achieved = achieved;
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
}
