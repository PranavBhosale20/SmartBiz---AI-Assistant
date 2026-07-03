package com.smartbiz.exception;

/**
 * Thrown when a lookup (by id, usually) finds nothing.
 *
 * WHY a dedicated class instead of plain RuntimeException:
 * GlobalExceptionHandler needs a way to tell "this thing doesn't exist"
 * (-> should be HTTP 404) apart from "this thing exists but the request
 * breaks a business rule" (-> should be HTTP 400). A generic
 * RuntimeException can't be distinguished by type, so the handler would
 * have no reliable way to pick the right status code. Two distinct
 * exception classes let the handler map each one to the correct status
 * just by catching the type.
 *
 * Used for: User not found, Doctor not found, Appointment not found,
 * Product not found, VisitType not found, Bill not found, etc.
 * Always the SAME shape - "X not found with id: Y" - which is why one
 * class (not one-per-entity) is enough; the message carries the detail.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Convenience overload so call sites don't have to hand-format the
     * message every time. Keeps every "not found" throw site consistent:
     *   throw new ResourceNotFoundException("User", id);
     * instead of:
     *   throw new ResourceNotFoundException("User not found with id: " + id);
     */
    public ResourceNotFoundException(String entityName, Object id) {
        super(entityName + " not found with id: " + id);
    }
}