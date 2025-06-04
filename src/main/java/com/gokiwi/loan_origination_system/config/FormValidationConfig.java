package com.gokiwi.loan_origination_system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration for form validation rules
 * Allows external configuration of validation parameters
 */
@Configuration
@ConfigurationProperties(prefix = "loan.form.validation")
@Data
public class FormValidationConfig {
    
    // Income validation
    private BigDecimal minIncome = new BigDecimal("20000");
    private BigDecimal maxIncome = new BigDecimal("10000000");
    
    // Occupation validation
    private List<String> allowedOccupations = Arrays.asList("SALARIED", "SELF_EMPLOYED");
    
    // Name validation
    private Integer minNameLength = 2;
    private Integer maxNameLength = 50;
    private Integer maxCompanyNameLength = 100;
    
    // Custom validation rules
    private Map<String, ValidationRule> customRules = new HashMap<>();
    
    // Field-specific configurations
    private Map<String, FieldConfig> fieldConfigs = new HashMap<>();
    
    @Data
    public static class ValidationRule {
        private String pattern;
        private String errorCode;
        private String errorMessage;
        private boolean required = true;
        private Integer minLength;
        private Integer maxLength;
        private List<String> allowedValues;
    }
    
    @Data
    public static class FieldConfig {
        private boolean required = true;
        private boolean editable = true;
        private String validationPattern;
        private String placeholder;
        private String helpText;
        private List<String> dependencies; // Fields that depend on this field
    }
    
    /**
     * Get validation rule for a specific field
     */
    public ValidationRule getValidationRule(String fieldName) {
        return customRules.get(fieldName);
    }
    
    /**
     * Get field configuration
     */
    public FieldConfig getFieldConfig(String fieldName) {
        return fieldConfigs.getOrDefault(fieldName, new FieldConfig());
    }
    
    /**
     * Check if field is required
     */
    public boolean isFieldRequired(String fieldName) {
        return getFieldConfig(fieldName).isRequired();
    }
}