package com.smartbiz.nlp;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Core NLP engine - rule-based keyword + regex matching.
 * Takes a raw message, returns an IntentResult.
 *
 * Strategy: each intent has a set of trigger keywords AND
 * optional regex patterns for entity extraction. A message
 * scores higher confidence if it matches both keywords AND
 * yields extractable entities.
 *
 * If no intent scores above 0.5 confidence, returns UNKNOWN
 * so Phase 9 can route to Gemini fallback.
 */
@Service
public class NlpService {

    // --- Keyword sets per intent ---
    private static final String[] BOOK_KEYWORDS =
            {"book", "schedule", "appointment", "reserve", "set up"};
    private static final String[] CANCEL_KEYWORDS =
            {"cancel", "cancell", "remove", "delete appointment"};
    private static final String[] SLOTS_KEYWORDS =
            {"slot", "slots", "available", "free", "timing", "timings", "when"};
    private static final String[] STOCK_KEYWORDS =
            {"stock", "inventory", "available", "quantity", "how many", "check"};
    private static final String[] GENERATE_BILL_KEYWORDS =
            {"generate bill", "create bill", "make bill", "bill generate"};
    private static final String[] VIEW_BILL_KEYWORDS =
            {"view bill", "show bill", "get bill", "bill for", "bill of"};

    // --- Regex patterns for entity extraction ---

    // Matches "Dr. Mehta", "dr mehta", "doctor mehta"
    private static final Pattern DOCTOR_PATTERN =
            Pattern.compile("(?:dr\\.?|doctor)\\s+([a-zA-Z]+)", Pattern.CASE_INSENSITIVE);

    // Matches "10:00", "10:00am", "10 am", "10am"
    private static final Pattern TIME_PATTERN =
            Pattern.compile("\\b(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?\\b",
                    Pattern.CASE_INSENSITIVE);

    // Matches "tomorrow", "today", or "2026-07-02"
    private static final Pattern DATE_PATTERN =
            Pattern.compile("\\b(today|tomorrow|\\d{4}-\\d{2}-\\d{2})\\b",
                    Pattern.CASE_INSENSITIVE);

    // Matches "appointment 3", "appointment id 3", "appt 3"
    private static final Pattern APPOINTMENT_ID_PATTERN =
            Pattern.compile("(?:appointment|appt|apt)\\s*(?:id)?\\s*(\\d+)",
                    Pattern.CASE_INSENSITIVE);

    // Matches product names - any word(s) after "of", "for", "stock of"
    private static final Pattern PRODUCT_PATTERN =
            Pattern.compile("(?:of|for)\\s+([a-zA-Z]+(?:\\s+[a-zA-Z]+)?)",
                    Pattern.CASE_INSENSITIVE);

    /**
     * Main entry point - takes raw user message, returns IntentResult.
     */
    public IntentResult process(String message) {
        if (message == null || message.isBlank()) {
            return IntentResult.unknown();
        }

        String normalized = message.toLowerCase().trim();

        // Try each intent in priority order - more specific first
        // (GENERATE_BILL before VIEW_BILL since both contain "bill")
        IntentResult result;

        result = tryGenerateBill(normalized);
        if (result.getConfidence() >= 0.5) return result;

        result = tryViewBill(normalized);
        if (result.getConfidence() >= 0.5) return result;

        result = tryBookAppointment(normalized);
        if (result.getConfidence() >= 0.5) return result;

        result = tryCancelAppointment(normalized);
        if (result.getConfidence() >= 0.5) return result;

        result = tryViewSlots(normalized);
        if (result.getConfidence() >= 0.5) return result;

        result = tryCheckStock(normalized);
        if (result.getConfidence() >= 0.5) return result;

        return IntentResult.unknown();
    }

    // -------------------------------------------------------
    // PATIENT intents
    // -------------------------------------------------------

    private IntentResult tryBookAppointment(String msg) {
        if (!containsAny(msg, BOOK_KEYWORDS)) return IntentResult.unknown();

        Map<String, String> entities = new HashMap<>();
        double confidence = 0.5; // base: keyword matched

        // Extract doctor name
        Matcher dm = DOCTOR_PATTERN.matcher(msg);
        if (dm.find()) {
            entities.put("doctorName", capitalize(dm.group(1)));
            confidence += 0.2;
        }

        // Extract date
        Matcher dateMatcher = DATE_PATTERN.matcher(msg);
        if (dateMatcher.find()) {
            entities.put("date", resolveDate(dateMatcher.group(1)));
            confidence += 0.2;
        }

        // Extract time
        Matcher timeMatcher = TIME_PATTERN.matcher(msg);
        if (timeMatcher.find()) {
            entities.put("time", resolveTime(
                    timeMatcher.group(1),
                    timeMatcher.group(2),
                    timeMatcher.group(3)));
            confidence += 0.1;
        }

        return new IntentResult(IntentType.BOOK_APPOINTMENT, entities,
                Math.min(confidence, 1.0));
    }

    private IntentResult tryCancelAppointment(String msg) {
        if (!containsAny(msg, CANCEL_KEYWORDS)) return IntentResult.unknown();

        Map<String, String> entities = new HashMap<>();
        double confidence = 0.6;

        Matcher idMatcher = APPOINTMENT_ID_PATTERN.matcher(msg);
        if (idMatcher.find()) {
            entities.put("appointmentId", idMatcher.group(1));
            confidence += 0.3;
        }

        return new IntentResult(IntentType.CANCEL_APPOINTMENT, entities, confidence);
    }

    private IntentResult tryViewSlots(String msg) {
        if (!containsAny(msg, SLOTS_KEYWORDS)) return IntentResult.unknown();

        // "slots" alone is too vague - require doctor or date context
        Map<String, String> entities = new HashMap<>();
        double confidence = 0.4;

        Matcher dm = DOCTOR_PATTERN.matcher(msg);
        if (dm.find()) {
            entities.put("doctorName", capitalize(dm.group(1)));
            confidence += 0.3;
        }

        Matcher dateMatcher = DATE_PATTERN.matcher(msg);
        if (dateMatcher.find()) {
            entities.put("date", resolveDate(dateMatcher.group(1)));
            confidence += 0.2;
        }

        return new IntentResult(IntentType.VIEW_SLOTS, entities, confidence);
    }

    // -------------------------------------------------------
    // STAFF intents
    // -------------------------------------------------------

    private IntentResult tryCheckStock(String msg) {
        if (!containsAny(msg, STOCK_KEYWORDS)) return IntentResult.unknown();

        Map<String, String> entities = new HashMap<>();
        double confidence = 0.5;

        Matcher pm = PRODUCT_PATTERN.matcher(msg);
        if (pm.find()) {
            entities.put("productName", pm.group(1).trim());
            confidence += 0.4;
        }

        return new IntentResult(IntentType.CHECK_STOCK, entities, confidence);
    }

    private IntentResult tryGenerateBill(String msg) {
        if (!containsAny(msg, GENERATE_BILL_KEYWORDS)) return IntentResult.unknown();

        Map<String, String> entities = new HashMap<>();
        double confidence = 0.7;

        Matcher idMatcher = APPOINTMENT_ID_PATTERN.matcher(msg);
        if (idMatcher.find()) {
            entities.put("appointmentId", idMatcher.group(1));
            confidence += 0.3;
        }

        return new IntentResult(IntentType.GENERATE_BILL, entities, confidence);
    }

    private IntentResult tryViewBill(String msg) {
        if (!containsAny(msg, VIEW_BILL_KEYWORDS)) return IntentResult.unknown();

        Map<String, String> entities = new HashMap<>();
        double confidence = 0.6;

        Matcher idMatcher = APPOINTMENT_ID_PATTERN.matcher(msg);
        if (idMatcher.find()) {
            entities.put("appointmentId", idMatcher.group(1));
            confidence += 0.3;
        }

        return new IntentResult(IntentType.VIEW_BILL, entities, confidence);
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private boolean containsAny(String msg, String[] keywords) {
        for (String kw : keywords) {
            if (msg.contains(kw)) return true;
        }
        return false;
    }

    private String resolveDate(String raw) {
        if ("today".equalsIgnoreCase(raw))
            return LocalDate.now().toString();
        if ("tomorrow".equalsIgnoreCase(raw))
            return LocalDate.now().plusDays(1).toString();
        return raw; // already in yyyy-MM-dd format
    }

    private String resolveTime(String hour, String minute, String ampm) {
        int h = Integer.parseInt(hour);
        int m = (minute != null) ? Integer.parseInt(minute) : 0;
        if ("pm".equalsIgnoreCase(ampm) && h != 12) h += 12;
        if ("am".equalsIgnoreCase(ampm) && h == 12) h = 0;
        return LocalTime.of(h, m).format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private String capitalize(String word) {
        if (word == null || word.isEmpty()) return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}
