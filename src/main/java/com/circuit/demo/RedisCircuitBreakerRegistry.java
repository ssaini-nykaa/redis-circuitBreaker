package com.circuit.demo;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.RegistryStore;
import io.github.resilience4j.core.registry.AbstractRegistry;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class RedisCircuitBreakerRegistry extends AbstractRegistry<CircuitBreaker, CircuitBreakerConfig> implements CircuitBreakerRegistry {


    @Autowired
    @Qualifier("redisRegistryStore")
    private RegistryStore<CircuitBreaker> redisRegistryStore;

    @Autowired
    private com.circuit.demo.CircuitBreakerConfig circuitBreakerConfig;

    @Autowired
    private RegistryEventConsumer<CircuitBreaker> registryEventConsumer;

    public RedisCircuitBreakerRegistry() {
        super(CircuitBreakerConfig.ofDefaults());
    }

    @Override
    public Seq<CircuitBreaker> getAllCircuitBreakers() {
        return Array.ofAll(redisRegistryStore.values());
    }

    @Override
    public CircuitBreaker circuitBreaker(String name) {
        return redisRegistryStore.computeIfAbsent(name, n -> {
            CircuitBreakerConfig config = CircuitBreakerConfig.ofDefaults();
            return this.circuitBreaker(name, config);
        });
    }

    @Override
    public CircuitBreaker circuitBreaker(String name, Map<String, String> tags) {
        return null;
    }

    @Override
    public CircuitBreaker circuitBreaker(String name, CircuitBreakerConfig config) {
        return this.circuitBreaker(name, config, HashMap.empty());
    }

    @Override
    public CircuitBreaker circuitBreaker(String name, CircuitBreakerConfig config, Map<String, String> tags) {
        return redisRegistryStore.computeIfAbsent(name, n -> CircuitBreaker.of(name, config));
    }

    @Override
    public CircuitBreaker circuitBreaker(String name, String configName) {
        return this.circuitBreaker(name, configName, HashMap.empty());
    }

    @Override
    public CircuitBreaker circuitBreaker(String name, String configName, Map<String, String> tags) {
//        return redisRegistryStore.computeIfAbsent(name, key -> {
//            CircuitBreakerConfig circuitBreakerConfig = this.getConfiguration(configName)
//                    .orElseThrow(() -> new ConfigurationNotFoundException(configName));
//            return CircuitBreaker.of(name, circuitBreakerConfig, this.getAllTags(tags));
//        });
        return null;
    }

    @Override
    public CircuitBreaker circuitBreaker(String name, Supplier<CircuitBreakerConfig> circuitBreakerConfigSupplier) {
        return null;
    }

    @Override
    public CircuitBreaker circuitBreaker(String name, Supplier<CircuitBreakerConfig> circuitBreakerConfigSupplier, Map<String, String> tags) {
        return null;
    }
}
