package com.smartbiz.exception;

/**
 * Thrown when a request is well-formed and the entities involved DO
 * exist, but the operation itself breaks a business rule.
 *
 * Examples from SmartBiz: booking a past-dated appointment, booking
 * more than 2 days in advance, a user/doctor double-booking clash,
 * cancelling an already-cancelled appointment, negative price/quantity,
 * prescribing more units than are in stock, OPD end time before start
 * time.
 *
 * Maps to HTTP 400 (Bad Request) in GlobalExceptionHandler - the
 * request was understood, but the operation is not allowed given the
 * current state of the data.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}