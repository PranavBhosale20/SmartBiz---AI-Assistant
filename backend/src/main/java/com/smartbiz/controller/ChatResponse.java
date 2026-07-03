package com.smartbiz.controller;

/**
 * Standard response shape for every /api/chat request.
 *
 * intent   - what the system understood the user to want
 * message  - human-readable status (success or failure reason)
 * data     - the actual result object (appointment, slots list,
 *            product stock, bill, etc.) - null if nothing to return
 *            or if the intent failed
 */
public class ChatResponse {

    private String intent;
    private String message;
    private Object data;

    public ChatResponse(String intent, String message, Object data) {
        this.intent = intent;
        this.message = message;
        this.data = data;
    }

    // Convenience: failed intent, no data
    public static ChatResponse error(String intent, String message) {
        return new ChatResponse(intent, message, null);
    }

    public String getIntent() { return intent; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
}