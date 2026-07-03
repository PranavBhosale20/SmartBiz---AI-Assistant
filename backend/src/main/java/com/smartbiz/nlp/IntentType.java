package com.smartbiz.nlp;

public enum IntentType {

    // PATIENT intents
    BOOK_APPOINTMENT,
    CANCEL_APPOINTMENT,
    VIEW_SLOTS,

    // STAFF intents
    CHECK_STOCK,
    GENERATE_BILL,
    VIEW_BILL,

    // Fallback - no confident match found,
    // Phase 8 Gemini will handle this case
    UNKNOWN
}