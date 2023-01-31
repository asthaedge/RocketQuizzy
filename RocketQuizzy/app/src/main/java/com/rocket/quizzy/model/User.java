package com.rocket.quizzy.model;

public class User {
    String name;
    String phoneNo;
    String imageUri;
    String uid;

    public User() {
    }

    public User(String name, String phoneNo, String imageUri, String uid) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.imageUri = imageUri;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
