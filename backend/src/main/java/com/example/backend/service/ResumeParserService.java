package com.example.backend.service;

import com.example.backend.model.Resume;
import com.example.backend.repo.ResumeRepository;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Extracts text and parses simple skills/keywords from resumes.
 */
@Service
public class ResumeParserService {

    private final ResumeRepository resumes;
    private final Tika tika = new Tika();

    private static final List<String> KNOWN_SKILLS = Arrays.asList(
            "java","spring","mongodb","mysql","react","node","aws","docker","kubernetes","python","tensorflow","nlp","sql","git","ci","cd","rest","graphql","typescript"
    );

    public ResumeParserService(ResumeRepository resumes) {
        this.resumes = resumes;
    }

    // PUBLIC_INTERFACE
    public Resume parseAndPersist(String userId, String uploadId, String filePath) throws IOException {
        String text = extractText(filePath);
        Resume r = new Resume();
        r.setUserId(userId);
        r.setUploadId(uploadId);
        r.setFullText(text);
        r.setSkills(extractSkills(text));
        r.setKeywords(extractKeywords(text));
        return resumes.save(r);
    }

    private String extractText(String path) throws IOException {
        try {
            return tika.parseToString(new File(path));
        } catch (TikaException e) {
            throw new IOException("Failed to parse file", e);
        }
    }

    private List<String> extractSkills(String text) {
        String lower = text.toLowerCase();
        Set<String> found = new LinkedHashSet<>();
        for (String s : KNOWN_SKILLS) {
            if (lower.contains(s)) {
                found.add(s);
            }
        }
        return new ArrayList<>(found);
    }

    private List<String> extractKeywords(String text) {
        String[] tokens = text.toLowerCase().split("[^a-z0-9+#]+");
        Map<String, Integer> freq = new HashMap<>();
        for (String t : tokens) {
            if (t.length() < 3) continue;
            if (Pattern.matches("\\d+", t)) continue;
            freq.put(t, freq.getOrDefault(t, 0) + 1);
        }
        return freq.entrySet().stream()
                .sorted((a,b) -> b.getValue() - a.getValue())
                .limit(30)
                .map(Map.Entry::getKey)
                .toList();
    }
}
