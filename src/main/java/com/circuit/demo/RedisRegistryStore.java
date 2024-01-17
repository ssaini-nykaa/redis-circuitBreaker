package com.circuit.demo;

import io.github.resilience4j.circuitbreaker.internal.CircuitBreakerStateMachine;
import io.github.resilience4j.core.RegistryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

@Component
public class RedisRegistryStore<CircuitBreakerStateMachine> implements RegistryStore<CircuitBreakerStateMachine> {

    @Autowired
    private RedisService redisService;

    @Override
    public CircuitBreakerStateMachine computeIfAbsent(String key, Function mappingFunction) {
        CircuitBreakerStateInfo circuitBreakerStateInfo = redisService.get(key, CircuitBreakerStateInfo.class);
        if (circuitBreakerStateInfo == null) {
            // Value not found in Redis, compute it
            CircuitBreakerStateMachine circuitBreakerStateMachine = (CircuitBreakerStateMachine) mappingFunction.apply(key);
            // Store the computed value in Redis
            circuitBreakerStateInfo = CircuitBreakerUtils.toDTO((io.github.resilience4j.circuitbreaker.internal.CircuitBreakerStateMachine) circuitBreakerStateMachine);

            redisService.save(key, circuitBreakerStateInfo);
            return circuitBreakerStateMachine;
        }

        return (CircuitBreakerStateMachine) CircuitBreakerUtils.fromDTO(circuitBreakerStateInfo);
    }

    @Override
    public CircuitBreakerStateMachine putIfAbsent(String key, Object value) {
        return null;
    }

    @Override
    public Optional<CircuitBreakerStateMachine> find(String key) {
        CircuitBreakerStateInfo circuitBreakerStateInfo = redisService.get(key, CircuitBreakerStateInfo.class);
        return Optional.of((CircuitBreakerStateMachine) CircuitBreakerUtils.fromDTO(circuitBreakerStateInfo));
    }

    @Override
    public Optional<CircuitBreakerStateMachine> remove(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<CircuitBreakerStateMachine> replace(String name, Object newEntry) {
        return Optional.empty();
    }

    @Override
    public Collection<CircuitBreakerStateMachine> values() {
        return null;
    }
}
