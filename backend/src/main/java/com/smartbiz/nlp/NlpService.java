package com.smartbiz.nlp;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NlpService {

    // NEW (Phase 8): Gemini fallback when no intent matches
    private final GeminiService geminiService;

    public NlpService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

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

    private static final Pattern DOCTOR_PATTERN =
            Pattern.compile("(?:dr\\.?|doctor)\\s+([a-zA-Z]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TIME_PATTERN =
            Pattern.compile("\\b(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?\\b",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern DATE_PATTERN =
            Pattern.compile("\\b(today|tomorrow|\\d{4}-\\d{2}-\\d{2})\\b",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern APPOINTMENT_ID_PATTERN =
            Pattern.compile("(?:appointment|appt|apt)\\s*(?:id)?\\s*(\\d+)",
                    Pattern.CASE_INSENSITIVE);
    private static final Pattern PRODUCT_PATTERN =
            Pattern.compile("(?:of|for)\\s+([a-zA-Z]+(?:\\s+[a-zA-Z]+)?)",
                    Pattern.CASE_INSENSITIVE);

    public IntentResult process(String message) {
        if (message == null || message.isBlank()) {
            return IntentResult.unknown();
        }

        String normalized = message.toLowerCase().trim();

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

        // CHANGED (Phase 8): instead of returning UNKNOWN,
        // hand off to Gemini for AI-based intent detection
        return geminiService.process(message);
    }

    private IntentResult tryBookAppointment(String msg) {
        if (!containsAny(msg, BOOK_KEYWORDS)) return IntentResult.unknown();

        Map<String, String> entities = new HashMap<>();
        double confidence = 0.5;

        Matcher dm = DOCTOR_PATTERN.matcher(msg);
        if (dm.find()) {
            entities.put("doctorName", capitalize(dm.group(1)));
            confidence += 0.2;
        }

        Matcher dateMatcher = DATE_PATTERN.matcher(msg);
        if (dateMatcher.find()) {
            entities.put("date", resolveDate(dateMatcher.group(1)));
            confidence += 0.2;
        }

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
        return raw;
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