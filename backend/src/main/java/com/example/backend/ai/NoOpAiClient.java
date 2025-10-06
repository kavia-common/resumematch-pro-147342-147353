package com.example.backend.ai;

import org.springframework.stereotype.Component;

/**
 * No-op AI client used when no external API keys configured.
 */
@Component
public class NoOpAiClient implements AiClient {

    @Override
    public String suggestImprovements(String input) {
        return "Consider adding quantified results, relevant keywords, and tailoring achievements to the job.";
        }
}
