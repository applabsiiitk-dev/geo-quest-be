package com.applabs.geo_quest.controller;

import com.applabs.geo_quest.dto.request.AnswerSubmissionRequest;
import com.applabs.geo_quest.dto.response.AnswerResultResponse;
import com.applabs.geo_quest.service.AnswerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/answers")
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
     *   "sessionId": "...",
     *   "questionId": "...",
     *   "answer": "Paris"
     * }
     *
     * Response:
     * {
     *   "correct": true,
     *   "message": "Correct! +50 points",
     *   "pointsAwarded": 50,
     *   "totalScore": 150
     * }
     */
    @PostMapping("/submit")
    public ResponseEntity<AnswerResultResponse> submitAnswer(
            @Valid @RequestBody AnswerSubmissionRequest request,
            @AuthenticationPrincipal String uid) {

        AnswerResultResponse result = answerService.submitAnswer(request, uid);
        return ResponseEntity.ok(result);
    }
}
