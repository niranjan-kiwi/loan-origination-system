package com.gokiwi.loan_origination_system.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * Enhanced validation result with detailed error codes and messages
 * Provides better error handling and internationalization support
 */
@Data
@AllArgsConstructor
public class ValidationResult {
    private final boolean valid;
    private final String errorCode;
    private final String message;
    private final List<String> details;

    public static ValidationResult success() {
        return new ValidationResult(true, null, null, Collections.emptyList());
    }

    public static ValidationResult failure(String errorCode, String message) {
        return new ValidationResult(false, errorCode, message, Collections.emptyList());
    }

    public static ValidationResult failure(String errorCode, String message, List<String> details) {
        return new ValidationResult(false, errorCode, message, details);
    }

    public boolean isValid() {
        return valid;
    }

    public boolean hasError() {
        return !valid;
    }
}