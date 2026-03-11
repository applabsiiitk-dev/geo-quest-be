package com.applabs.geo_quest.service;

import com.applabs.geo_quest.model.Session;
import com.applabs.geo_quest.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionService {

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Generates a randomized question trail for a new session.
     * Fetches all question IDs, shuffles them, and assigns to the session.
     * 
     * @return List<Long> randomized question trail
     */
    public List<String> generateRandomTrail() {
        List<String> questionIds = questionRepository.findAll()
                .stream()
                .map(q -> q.getQuestionId())
                .collect(Collectors.toList());
        Collections.shuffle(questionIds);
        return questionIds;
    }

    /**
     * Creates a new session with a randomized question trail.
     * 
     * @param session Session object to initialize
     */
    public void assignRandomTrailToSession(Session session) {
        session.setQuestionTrail(generateRandomTrail());
        session.setCurrentTrailIndex(0);
    }
}
