package com.circuit.demo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

import java.io.IOException;

public class VavrMapDeserializer extends JsonDeserializer<Map<?, ?>> {
    @Override
    public Map<?, ?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        java.util.Map<?, ?> javaMap = jsonParser.readValueAs(java.util.Map.class);
        return HashMap.ofAll(javaMap);
    }
}
