package com.circuit.demo;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class RegistryEventConsumerImpl implements RegistryEventConsumer<CircuitBreaker>  {

    private static final Logger log = LogManager.getLogger(RegistryEventConsumerImpl.class);
    @Override
    public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> entryAddedEvent) {
        entryAddedEvent.getAddedEntry().getEventPublisher()
                .onEvent(event -> {
                    if (CircuitBreakerEvent.Type.NOT_PERMITTED.equals(event.getEventType())) {
                        log.info("Circuit breaker : does not permitted call", event.getCircuitBreakerName());
                    }
                    if (!CircuitBreakerEvent.Type.SUCCESS.equals(event.getEventType())) {
                        log.info(event.toString());
                    }
                });
    }

    @Override
    public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> entryRemoveEvent) {
        log.info("Circuit breaker : {} deleted", entryRemoveEvent.getRemovedEntry().getName());
    }

    @Override
    public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> entryReplacedEvent) {
        log.info("Circuit breaker configuration: {} replaced with : {}", entryReplacedEvent.getOldEntry()
                .getCircuitBreakerConfig(), entryReplacedEvent.getNewEntry().getCircuitBreakerConfig());
    }
}
