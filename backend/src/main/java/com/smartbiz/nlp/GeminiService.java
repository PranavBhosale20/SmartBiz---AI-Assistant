package com.smartbiz.nlp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;

    public GeminiService(@Value("${gemini.api.key}") String apiKey,
                          @Value("${gemini.api.url}") String apiUrl) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.restTemplate = new RestTemplate();
    }

    public IntentResult process(String userMessage) {
        try {
            String prompt = buildPrompt(userMessage);
            String responseJson = callGemini(prompt);
            return parseResponse(responseJson);
        } catch (Exception e) {
            System.err.println("GeminiService error: " + e.getMessage());
            e.printStackTrace();
            return IntentResult.unknown();
        }
    }

    private String buildPrompt(String userMessage) {
        return """
                You are an NLP assistant for a clinic management system called SmartBiz.
                Analyze the user message and return ONLY a JSON object, no other text,
                no markdown, no code fences.
                
                Valid intents:
                - BOOK_APPOINTMENT
                - CANCEL_APPOINTMENT
                - VIEW_SLOTS
                - CHECK_STOCK
                - GENERATE_BILL
                - VIEW_BILL
                - UNKNOWN
                
                Extract these entities if present:
                - doctorName (without Dr. prefix)
                - date (yyyy-MM-dd format)
                - time (HH:mm format)
                - appointmentId (numeric)
                - productName
                
                Return ONLY this JSON:
                {"intent":"INTENT_NAME","entities":{"key":"value"},"confidence":0.0}
                
                User message: "%s"
                """.formatted(userMessage);
    }

    private String callGemini(String prompt) {
        String url = apiUrl + "?key=" + apiKey;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        return response.getBody();
    }

    private IntentResult parseResponse(String geminiResponse) throws Exception {
        // Manual JSON parsing without ObjectMapper to avoid import issues
        // Extract the text field from Gemini's response structure
        int textStart = geminiResponse.indexOf("\"text\":");
        if (textStart == -1) return IntentResult.unknown();

        // Find the actual JSON content Gemini returned
        int jsonStart = geminiResponse.indexOf("{", textStart + 7);
        int jsonEnd = geminiResponse.lastIndexOf("}");
        if (jsonStart == -1 || jsonEnd == -1) return IntentResult.unknown();

        String extracted = geminiResponse.substring(jsonStart, jsonEnd + 1);

        // Strip any nested JSON artifacts from Gemini's wrapper
        // Find the innermost complete JSON that has "intent" field
        int intentIdx = extracted.indexOf("\"intent\"");
        if (intentIdx == -1) return IntentResult.unknown();

        // Find the object containing "intent"
        int objStart = extracted.lastIndexOf("{", intentIdx);
        int objEnd = extracted.indexOf("}", intentIdx);
        // Walk forward to find the closing brace of the full object
        int depth = 0;
        int pos = objStart;
        int finalEnd = objStart;
        while (pos <= extracted.length() - 1) {
            char c = extracted.charAt(pos);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) { finalEnd = pos; break; }
            }
            pos++;
        }

        String intentJson = extracted.substring(objStart, finalEnd + 1);

        // Parse intent
        String intentStr = extractJsonValue(intentJson, "intent");
        IntentType intent;
        try {
            intent = IntentType.valueOf(intentStr.toUpperCase().trim());
        } catch (Exception e) {
            return IntentResult.unknown();
        }

        // Parse confidence
        double confidence;
        try {
            confidence = Double.parseDouble(extractJsonValue(intentJson, "confidence"));
        } catch (Exception e) {
            confidence = 0.7;
        }

        // Parse entities - simple key extraction
        Map<String, String> entities = new HashMap<>();
        String[] entityKeys = {"doctorName", "date", "time", "appointmentId", "productName"};
        for (String key : entityKeys) {
            String val = extractJsonValue(intentJson, key);
            if (val != null && !val.isEmpty()) {
                entities.put(key, val);
            }
        }

        return new IntentResult(intent, entities, confidence);
    }

    /**
     * Simple regex-free JSON value extractor for flat string/number values.
     * Sufficient for our known fixed JSON shape from Gemini.
     */
    private String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return null;

        int colonIdx = json.indexOf(":", idx + search.length());
        if (colonIdx == -1) return null;

        // Skip whitespace after colon
        int valueStart = colonIdx + 1;
        while (valueStart < json.length() &&
                (json.charAt(valueStart) == ' ' || json.charAt(valueStart) == '\n')) {
            valueStart++;
        }

        if (json.charAt(valueStart) == '"') {
            // String value
            int end = json.indexOf("\"", valueStart + 1);
            return end == -1 ? null : json.substring(valueStart + 1, end);
        } else {
            // Numeric value
            int end = valueStart;
            while (end < json.length() &&
                    json.charAt(end) != ',' && json.charAt(end) != '}') {
                end++;
            }
            return json.substring(valueStart, end).trim();
        }
    }
}