package com.circuit.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class RedisObjectSerializer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(Object object) throws JsonProcessingException {

        return objectMapper.writeValueAsString(object);
    }

    public static <T> T deserialize(String source, Class<T> type) throws IOException {
        return objectMapper.readValue(source, type);
    }
}
