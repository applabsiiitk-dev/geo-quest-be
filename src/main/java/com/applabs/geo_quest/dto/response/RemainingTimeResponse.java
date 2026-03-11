/**
 * Response sent to the client with remaining session time in GeoQuest.
 * <p>
 * Contains remaining seconds and session activity status.
 * <p>
 * Fields:
 * <ul>
 *   <li><b>remainingSeconds</b>: Number of seconds left in the session.</li>
 *   <li><b>sessionActive</b>: Whether the session is still active.</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Used by controllers to return session timing info to the client.</li>
 *   <li>Flutter client uses this for countdown timers and session state.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemainingTimeResponse {
    private long remainingSeconds;
    private boolean sessionActive;
}
