package com.smartbiz.nlp;

import java.util.Map;

/**
 * What the NLP engine returns for every message.
 *
 * intent     - what the user wants to do
 * entities   - extracted values (e.g. "doctorName"->"Dr. Mehta",
 *              "date"->"2026-07-02", "productName"->"paracetamol")
 * confidence - 0.0 to 1.0, how confident the engine is.
 *              Below a threshold (e.g. 0.5), Phase 9 will
 *              route to Gemini fallback instead of acting.
 */
public class IntentResult {

    private final IntentType intent;
    private final Map<String, String> entities;
    private final double confidence;

    public IntentResult(IntentType intent, Map<String, String> entities, double confidence) {
        this.intent = intent;
        this.entities = entities;
        this.confidence = confidence;
    }

    // Convenience constructor for UNKNOWN with no entities
    public static IntentResult unknown() {
        return new IntentResult(IntentType.UNKNOWN, Map.of(), 0.0);
    }

    public IntentType getIntent() { return intent; }
    public Map<String, String> getEntities() { return entities; }
    public double getConfidence() { return confidence; }

    @Override
    public String toString() {
        return "IntentResult{intent=" + intent +
                ", entities=" + entities +
                ", confidence=" + confidence + "}";
    }
}