// PLAN: Riddle-based hints implementation
// - On correct answer, return the description of the NEXT question as a riddle/clue
// - Update response DTO if needed
// - Ensure answer flow logic fetches the next question
package com.applabs.geo_quest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.applabs.geo_quest.dto.request.AnswerSubmissionRequest;
import com.applabs.geo_quest.dto.response.AnswerResultResponse;
import com.applabs.geo_quest.service.AnswerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/answers")
/**
 * Controller for handling answer submissions in GeoQuest.
 * <p>
 * This controller manages the submission of answers for questions during an
 * active session.
 * It delegates answer validation and scoring to the AnswerService, and returns
 * the result
 * including correctness, points awarded, updated score, cooldown, and the next
 * location hint (riddle).
 * <p>
 * Endpoints:
 * <ul>
 * <li>POST /api/answers/submit — Submit an answer for a question</li>
 * </ul>
 * <p>
 * The nextHint field in the response provides a riddle/clue for the next
 * location, sent only on correct answers.
 *
 * @author fl4nk3r
 */
public class AnswerController {

    private final AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    /**
     * POST /api/answers/submit
     * Submits an answer for a question in the context of an active session.
     *
     * Request body:
     * {
     * "sessionId": "...",
     * "questionId": "...",
     * "answer": "Paris"
     * }
     *
     * Response:
     * {
     * "correct": true,
     * "message": "Correct! +50 points",
     * "pointsAwarded": 50,
     * "totalScore": 150,
     * "cooldownUntil": "2026-03-11T10:00:00Z",
     * "nextHint": "Where students fuel up before a 9 AM class"
     * }
     *
     * The nextHint field is a riddle/clue for the next location, sent only on
     * correct answer.
     */
    @PostMapping("/submit")
    public ResponseEntity<AnswerResultResponse> submitAnswer(
            @Valid @RequestBody AnswerSubmissionRequest request,
            @AuthenticationPrincipal String uid) {

        AnswerResultResponse result = answerService.submitAnswer(request, uid);
        return ResponseEntity.ok(result);
    }
}
