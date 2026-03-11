/**
 * Exception thrown when access is denied in GeoQuest.
 * <p>
 * Used for error handling in controllers and services, typically for authorization failures.
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
