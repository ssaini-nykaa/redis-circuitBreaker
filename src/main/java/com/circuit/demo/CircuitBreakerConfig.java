package com.circuit.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;

@Configuration
public class CircuitBreakerConfig {
    ObjectMapper objectMapper= new ObjectMapper();

    private static final Logger logger = LogManager.getLogger(CircuitBreakerConfig.class);

    @Autowired
    @Qualifier("redisCircuitBreakerRegistry")
    private CircuitBreakerRegistry circuitBreakerRegistry;


    @PostConstruct
    public void init() {
        String configString = "{\"vpa_validation\":{\"disable\":false,\"minimumNumberOfCalls\":5,\"waitDurationInOpenState\":5,\"slidingWindowType\":\"COUNT_BASED\",\"slidingWindowSize\":10,\"failureRateThreshold\":50,\"permittedNumberOfCallsInHalfOpenState\":1},\"express_checkout\":{\"disable\":false,\"minimumNumberOfCalls\":5,\"waitDurationInOpenState\":5,\"slidingWindowType\":\"COUNT_BASED\",\"slidingWindowSize\":20,\"failureRateThreshold\":30,\"permittedNumberOfCallsInHalfOpenState\":1}}";
        try {
            updateCircuitBreakerConfig(configString);
        }
        catch (Exception ex){
            logger.info("Exception occurred while initialising circuit breakers", ex);
        }
    }

    public void updateCircuitBreakerConfig(String configString) {
        try {
            Map<String, CircuitBreakerConfigDto> configMap = objectMapper.readValue(configString,
                    new TypeReference<Map<String, CircuitBreakerConfigDto>>() {
                    });
            initializeCircuitBreakersInRegistry(configMap);
        }
        catch (Exception ex){
            logger.info("Exception occurred while initialising circuit breakers", ex);
        }
    }

    private void initializeCircuitBreakersInRegistry(Map<String, CircuitBreakerConfigDto> circuitBreakerConfigMap) {
        if (null == circuitBreakerConfigMap || circuitBreakerConfigMap.isEmpty()) {
            logger.info("No circuit breakers found for initialization");
            return;
        }
        try {
            for (Map.Entry<String, CircuitBreakerConfigDto> entry : circuitBreakerConfigMap.entrySet()) {
                CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(entry.getKey(),
                        circuitBreakerConfigDtoConverter(entry.getValue()));
                if (Boolean.TRUE.equals(entry.getValue().getDisable())) {
                    circuitBreaker.transitionToDisabledState();
                }
            }
        } catch (Exception e) {
            logger.error("Issue while registering circuit breakers {}", e);
        }
    }

    private io.github.resilience4j.circuitbreaker.CircuitBreakerConfig circuitBreakerConfigDtoConverter(CircuitBreakerConfigDto configDto) {
        return io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .waitDurationInOpenState(Duration.ofSeconds(configDto.getWaitDurationInOpenState()))
                .permittedNumberOfCallsInHalfOpenState(configDto.getPermittedNumberOfCallsInHalfOpenState())
                .slidingWindowType(configDto.getSlidingWindowType())
                .failureRateThreshold(configDto.getFailureRateThreshold())
                .minimumNumberOfCalls(configDto.getMinimumNumberOfCalls())
                .slidingWindowSize(configDto.getSlidingWindowSize())
                .enableAutomaticTransitionFromOpenToHalfOpen()
                .build();
    }
}
