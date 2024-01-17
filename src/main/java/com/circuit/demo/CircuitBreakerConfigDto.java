package com.circuit.demo;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CircuitBreakerConfigDto {
    private Boolean disable;
    private int minimumNumberOfCalls;
    private int waitDurationInOpenState;
    private float failureRateThreshold;
    private int slidingWindowSize;
    private CircuitBreakerConfig.SlidingWindowType slidingWindowType;
    private int permittedNumberOfCallsInHalfOpenState;
}
