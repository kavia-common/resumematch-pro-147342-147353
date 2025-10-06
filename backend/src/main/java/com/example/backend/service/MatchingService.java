package com.example.backend.service;

import com.example.backend.model.Analysis;
import com.example.backend.model.Match;
import com.example.backend.repo.MatchRepository;
import org.springframework.stereotype.Service;

/**
 * Persists match results.
 */
@Service
public class MatchingService {

    private final MatchRepository matches;

    public MatchingService(MatchRepository matches) {
        this.matches = matches;
    }

    // PUBLIC_INTERFACE
    public Match createMatch(String userId, Analysis analysis) {
        Match m = new Match();
        m.setUserId(userId);
        m.setResumeId(analysis.getResumeId());
        m.setJobId(analysis.getJobId());
        m.setScore(analysis.getScore());
        m.setAnalysisId(analysis.getId());
        return matches.save(m);
    }
}
