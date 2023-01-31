package com.rocket.quizzy.service.interfaces;

public interface FireResult {
    void onUploadSuccess(String imageUrl);
    void onUploadFailure(String error);
}
