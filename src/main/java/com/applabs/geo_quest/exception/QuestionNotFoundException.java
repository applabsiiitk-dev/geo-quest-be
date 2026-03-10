package com.applabs.geo_quest.exception;

public class QuestionNotFoundException extends RuntimeException {
    public QuestionNotFoundException(String questionId) {
        super("Question not found: " + questionId);
    }
}
