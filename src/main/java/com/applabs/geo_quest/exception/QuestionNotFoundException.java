/**
 * Exception thrown when a question is not found in GeoQuest.
 * <p>
 * Used for error handling in controllers and services.
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.exception;

public class QuestionNotFoundException extends RuntimeException {
    public QuestionNotFoundException(String questionId) {
        super("Question not found: " + questionId);
    }
}
