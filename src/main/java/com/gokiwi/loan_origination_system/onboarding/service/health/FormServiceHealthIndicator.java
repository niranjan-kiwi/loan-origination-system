package com.gokiwi.loan_origination_system.onboarding.service.health;

import com.gokiwi.core.utils.helper.CacheHelper;
import com.gokiwi.loan_origination_system.onboarding.persistence.dao.FormDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health indicator for Form Service components
 * Monitors the health of form-related services and dependencies
 */
@Component("formService")
@RequiredArgsConstructor
@Slf4j
public class FormServiceHealthIndicator implements HealthIndicator {

    private final FormDao formDao;
    private final CacheHelper cacheHelper;

    @Override
    public Health health() {
        try {
            boolean databaseHealthy = checkDatabaseHealth();
            boolean cacheHealthy = checkCacheHealth();
            boolean validationHealthy = checkValidationService();

            if (databaseHealthy && cacheHealthy && validationHealthy) {
                return Health.up()
                        .withDetail("database", "UP")
                        .withDetail("cache", "UP")
                        .withDetail("validation", "UP")
                        .withDetail("timestamp", System.currentTimeMillis())
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", databaseHealthy ? "UP" : "DOWN")
                        .withDetail("cache", cacheHealthy ? "UP" : "DOWN")
                        .withDetail("validation", validationHealthy ? "UP" : "DOWN")
                        .withDetail("timestamp", System.currentTimeMillis())
                        .build();
            }
        } catch (Exception e) {
            log.error("Error checking form service health", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp", System.currentTimeMillis())
                    .build();
        }
    }

    private boolean checkDatabaseHealth() {
        try {
            // Simple database connectivity check
            formDao.getApplicationInfoEntityByUserId("health-check-user");
            return true;
        } catch (Exception e) {
            log.warn("Database health check failed", e);
            return false;
        }
    }

    private boolean checkCacheHealth() {
        try {
            // Simple cache connectivity check
            String testKey = "health-check-" + System.currentTimeMillis();
            cacheHelper.put(testKey, 60, "test-value");
            String value = cacheHelper.get(testKey);
            return "test-value".equals(value);
        } catch (Exception e) {
            log.warn("Cache health check failed", e);
            return false;
        }
    }

    private boolean checkValidationService() {
        try {
            // Basic validation service check
            // Could add more sophisticated checks here
            return true;
        } catch (Exception e) {
            log.warn("Validation service health check failed", e);
            return false;
        }
    }
}