package com.gokiwi.loan_origination_system.onboarding.service.form;

import com.gokiwi.loan_origination_system.enums.Occupation;
import com.gokiwi.loan_origination_system.onboarding.dto.FormDto;
import com.gokiwi.loan_origination_system.onboarding.dto.FormValidationError;
import com.gokiwi.loan_origination_system.onboarding.dto.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Enhanced Form Validation Service with comprehensive validation rules
 * Provides detailed error messages and validation codes for better UX
 */
@Service
@Slf4j
public class FormValidationService {

    // Validation constants
    private static final BigDecimal MIN_INCOME = new BigDecimal("20000");
    private static final BigDecimal MAX_INCOME = new BigDecimal("10000000");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    private static final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]{1}$");
    private static final Pattern PINCODE_PATTERN = Pattern.compile("^[1-9][0-9]{5}$");

    public List<FormValidationError> validateFormData(FormDto formDto) {
        List<FormValidationError> errors = new ArrayList<>();
        
        for (FormDto.FormData formData : formDto.getFormData()) {
            ValidationResult result = validateField(formData.getKey(), formData.getValue());
            if (result.hasError()) {
                errors.add(createFormValidationError(formData.getKey(), result));
            }
        }
        
        return errors;
    }

    public ValidationResult validateField(String fieldKey, String value) {
        if (!StringUtils.hasText(value)) {
            return ValidationResult.failure("FIELD_REQUIRED", 
                "This field is required");
        }

        switch (fieldKey.toLowerCase()) {
            case "occupation":
                return validateOccupation(value);
            case "currentmonthlyincome":
                return validateIncome(value);
            case "email":
                return validateEmail(value);
            case "pan":
            case "pancard":
                return validatePan(value);
            case "pincode":
                return validatePincode(value);
            case "fathername":
                return validateName(value, "Father's name");
            case "companyname":
                return validateCompanyName(value);
            default:
                return ValidationResult.success();
        }
    }

    private ValidationResult validateOccupation(String value) {
        try {
            Occupation.valueOf(value.toUpperCase());
            return ValidationResult.success();
        } catch (IllegalArgumentException e) {
            List<String> validOccupations = Arrays.stream(Occupation.values())
                    .map(Enum::name)
                    .toList();
            return ValidationResult.failure("INVALID_OCCUPATION", 
                "Please select a valid occupation", validOccupations);
        }
    }

    private ValidationResult validateIncome(String income) {
        try {
            BigDecimal amount = new BigDecimal(income.trim());
            
            if (amount.compareTo(MIN_INCOME) < 0) {
                return ValidationResult.failure("INCOME_TOO_LOW", 
                    "Minimum monthly income required: ₹" + MIN_INCOME.toString());
            }
            
            if (amount.compareTo(MAX_INCOME) > 0) {
                return ValidationResult.failure("INCOME_TOO_HIGH", 
                    "Maximum monthly income allowed: ₹" + MAX_INCOME.toString());
            }
            
            return ValidationResult.success();
        } catch (NumberFormatException e) {
            return ValidationResult.failure("INVALID_FORMAT", 
                "Please enter a valid numeric amount");
        }
    }

    private ValidationResult validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return ValidationResult.failure("INVALID_EMAIL_FORMAT", 
                "Please enter a valid email address");
        }
        return ValidationResult.success();
    }

    private ValidationResult validatePan(String pan) {
        String cleanPan = pan.trim().toUpperCase();
        if (!PAN_PATTERN.matcher(cleanPan).matches()) {
            return ValidationResult.failure("INVALID_PAN_FORMAT", 
                "PAN should be in format: ABCDE1234F");
        }
        return ValidationResult.success();
    }

    private ValidationResult validatePincode(String pincode) {
        if (!PINCODE_PATTERN.matcher(pincode.trim()).matches()) {
            return ValidationResult.failure("INVALID_PINCODE_FORMAT", 
                "Please enter a valid 6-digit pincode");
        }
        return ValidationResult.success();
    }

    private ValidationResult validateName(String name, String fieldDisplayName) {
        String trimmedName = name.trim();
        if (trimmedName.length() < 2) {
            return ValidationResult.failure("NAME_TOO_SHORT", 
                fieldDisplayName + " must be at least 2 characters long");
        }
        if (trimmedName.length() > 50) {
            return ValidationResult.failure("NAME_TOO_LONG", 
                fieldDisplayName + " cannot exceed 50 characters");
        }
        if (!trimmedName.matches("^[a-zA-Z\\s.'-]+$")) {
            return ValidationResult.failure("INVALID_NAME_FORMAT", 
                fieldDisplayName + " should contain only letters, spaces, and common punctuation");
        }
        return ValidationResult.success();
    }

    private ValidationResult validateCompanyName(String companyName) {
        String trimmedName = companyName.trim();
        if (trimmedName.length() < 2) {
            return ValidationResult.failure("COMPANY_NAME_TOO_SHORT", 
                "Company name must be at least 2 characters long");
        }
        if (trimmedName.length() > 100) {
            return ValidationResult.failure("COMPANY_NAME_TOO_LONG", 
                "Company name cannot exceed 100 characters");
        }
        return ValidationResult.success();
    }

    private FormValidationError createFormValidationError(String fieldKey, ValidationResult result) {
        Map<String, Object> errorDetail = new HashMap<>();
        errorDetail.put("key", fieldKey);
        errorDetail.put("errorCode", result.getErrorCode());
        errorDetail.put("message", result.getMessage());
        
        if (!result.getDetails().isEmpty()) {
            errorDetail.put("details", result.getDetails());
        }
        
        return new FormValidationError().setErrors(List.of(errorDetail));
    }
}