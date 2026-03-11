/**
 * Enum representing the status of a GeoQuest session.
 * <p>
 * Values:
 * <ul>
 *   <li><b>ACTIVE</b>: Session is ongoing and playable.</li>
 *   <li><b>COMPLETED</b>: Session has ended or expired.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used in Session model and services to track session lifecycle.</li>
 *   <li>Controls access, scoring, and question flow.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.enums;

public enum SessionStatus {
    ACTIVE,
    COMPLETED,
}
