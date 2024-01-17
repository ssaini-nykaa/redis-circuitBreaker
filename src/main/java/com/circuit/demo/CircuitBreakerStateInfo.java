package com.circuit.demo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.vavr.collection.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CircuitBreakerStateInfo {
    private String state;
    private String name;
    private CircuitBreakerConfigDto config;
    @JsonDeserialize(using = VavrMapDeserializer.class)
    private Map<String, String> tags;
}
