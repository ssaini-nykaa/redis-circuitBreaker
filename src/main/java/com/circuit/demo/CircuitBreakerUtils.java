package com.circuit.demo;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.internal.CircuitBreakerStateMachine;

import java.util.function.Supplier;

public class CircuitBreakerUtils {

    public static CircuitBreakerStateInfo toDTO(CircuitBreakerStateMachine cbStateMachine) {
        CircuitBreakerStateInfo dto = new CircuitBreakerStateInfo();
        dto.setName(cbStateMachine.getName());
        dto.setState(cbStateMachine.getState().name());
        dto.setConfig(convertToConfigDto(cbStateMachine.getCircuitBreakerConfig()));
        dto.setState(cbStateMachine.getState().name());
        dto.setTags(cbStateMachine.getTags());
        return dto;
    }

    private static CircuitBreakerConfigDto convertToConfigDto(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig circuitBreakerConfig) {
        CircuitBreakerConfigDto circuitBreakerConfigDto = new CircuitBreakerConfigDto();
        circuitBreakerConfigDto.setFailureRateThreshold(circuitBreakerConfig.getFailureRateThreshold());
        circuitBreakerConfigDto.setMinimumNumberOfCalls(circuitBreakerConfig.getMinimumNumberOfCalls());
        circuitBreakerConfigDto.setPermittedNumberOfCallsInHalfOpenState(circuitBreakerConfig.getPermittedNumberOfCallsInHalfOpenState());
        circuitBreakerConfigDto.setSlidingWindowSize(circuitBreakerConfig.getSlidingWindowSize());
        circuitBreakerConfigDto.setSlidingWindowType(circuitBreakerConfig.getSlidingWindowType());
        return circuitBreakerConfigDto;
    }

    private static CircuitBreakerConfig convertFromConfigDto(CircuitBreakerConfigDto dto) {
        CircuitBreakerConfig.Builder configBuilder = CircuitBreakerConfig.custom();

        configBuilder.failureRateThreshold(dto.getFailureRateThreshold());
        configBuilder.minimumNumberOfCalls(dto.getMinimumNumberOfCalls());
        configBuilder.permittedNumberOfCallsInHalfOpenState(dto.getPermittedNumberOfCallsInHalfOpenState());
        configBuilder.slidingWindowSize(dto.getSlidingWindowSize());

        if (dto.getSlidingWindowType() != null) {
            configBuilder.slidingWindowType(CircuitBreakerConfig.SlidingWindowType.valueOf(String.valueOf(dto.getSlidingWindowType())));
        }
        return configBuilder.build();
    }
    public static CircuitBreakerStateMachine fromDTO(CircuitBreakerStateInfo dto) {
        Supplier<CircuitBreakerConfig> configSupplier = () -> convertFromConfigDto(dto.getConfig());
        CircuitBreakerStateMachine circuitBreakerStateMachine = new CircuitBreakerStateMachine(dto.getName(), configSupplier, dto.getTags());
        setState(CircuitBreaker.State.valueOf(dto.getState()), circuitBreakerStateMachine);
        return circuitBreakerStateMachine;
    }

    private static void setState(CircuitBreaker.State state, CircuitBreakerStateMachine circuitBreakerStateMachine) {
        switch (state) {
            case CLOSED:
                circuitBreakerStateMachine.transitionToClosedState();
                break;
            case OPEN:
                circuitBreakerStateMachine.transitionToOpenState();
                break;
            case HALF_OPEN:
                circuitBreakerStateMachine.transitionToHalfOpenState();
                break;
            default:
                circuitBreakerStateMachine.reset();
                break;
        }
    }


}