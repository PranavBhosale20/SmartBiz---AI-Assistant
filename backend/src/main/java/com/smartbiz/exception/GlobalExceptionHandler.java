package com.smartbiz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * WHY this class exists:
 * Before Phase 5, an unhandled exception thrown anywhere in a Service
 * (e.g. the duplicate-booking RuntimeException) propagated all the way
 * up to Spring's default error handling, which dumps the full Java
 * stack trace into the HTTP response body as JSON. That's useless (and
 * a bit dangerous - it leaks internal class names/package structure) to
 * any real client like a frontend or another service.
 *
 * @RestControllerAdvice makes this class a single, global interceptor
 * for exceptions thrown from ANY @RestController in the app. Instead of
 * wrapping every Service call in try/catch inside every Controller
 * method (which would be repetitive and easy to forget), we centralize
 * the translation from "exception type" to "HTTP response" in one place.
 *
 * Each @ExceptionHandler method below declares which exception type it
 * catches. Spring inspects the exception thrown and routes it to the
 * matching handler automatically - we never call these methods directly.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Catches ResourceNotFoundException from anywhere in the app
     * (Service layer, or the Mapper layer until that refactor lands)
     * and converts it into a 404 with just the message - no stack
     * trace, no internal class names.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Catches BusinessException - the request was valid and the
     * entities exist, but the operation itself isn't allowed (past
     * date, clash, insufficient stock, etc.). 400 is the correct
     * status here: the client sent something the server understood
     * but can't act on as requested.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(BusinessException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Safety net: catches anything NOT already handled above (a real
     * bug, a NullPointerException, whatever). Without this, an
     * unexpected exception type would fall through to Spring's default
     * handling and we'd be back to raw stack traces for THAT case.
     *
     * This intentionally returns a generic message rather than
     * ex.getMessage() - an unexpected exception's message might contain
     * internal details (SQL, field names, etc.) we don't want leaking
     * to a client. Logging the real exception (not yet wired up - see
     * note below) is how you'd actually debug this in practice.
     *
     * TODO (later phase): inject a Logger here and log ex with full
     * stack trace server-side, so 500s are still debuggable from the
     * server logs even though the client only sees a generic message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred. Please try again later."));
    }
}