package com.example.backend.service;

import com.example.backend.model.JobDescription;
import com.example.backend.repo.JobRepository;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Extracts text and parses simple skills/keywords from job descriptions.
 */
@Service
public class JDParserService {

    private final JobRepository jobs;
    private final Tika tika = new Tika();

    private static final List<String> KNOWN_SKILLS = Arrays.asList(
            "java","spring","mongodb","mysql","react","node","aws","docker","kubernetes","python","tensorflow","nlp","sql","git","ci","cd","rest","graphql","typescript","leadership","communication"
    );

    public JDParserService(JobRepository jobs) {
        this.jobs = jobs;
    }

    // PUBLIC_INTERFACE
    public JobDescription parseAndPersist(String userId, String uploadId, String filePath) throws IOException {
        String text = extractText(filePath);
        JobDescription jd = new JobDescription();
        jd.setUserId(userId);
        jd.setUploadId(uploadId);
        jd.setFullText(text);
        jd.setSkills(extractSkills(text));
        jd.setKeywords(extractKeywords(text));
        jd.setTitle(guessTitle(text));
        return jobs.save(jd);
    }

    // PUBLIC_INTERFACE
    public JobDescription persistFromRawText(String userId, String title, String body) {
        JobDescription jd = new JobDescription();
        jd.setUserId(userId);
        jd.setFullText(body);
        jd.setSkills(extractSkills(body));
        jd.setKeywords(extractKeywords(body));
        jd.setTitle(title);
        return jobs.save(jd);
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

    private String guessTitle(String text) {
        String[] lines = text.split("\\r?\\n");
        for (String l : lines) {
            if (l.toLowerCase().contains("engineer") || l.toLowerCase().contains("developer") || l.toLowerCase().contains("manager")) {
                return l.trim();
            }
        }
        return "Job Description";
    }
}
