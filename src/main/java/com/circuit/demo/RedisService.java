package com.circuit.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.resilience4j.circuitbreaker.internal.CircuitBreakerStateMachine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RedisService {

    @Autowired
    @Qualifier("circuitBreakerRedis")
    private RedisTemplate<String, String> circuitBreakerRedis;

    private static final Logger log = LogManager.getLogger(RedisService.class);

    //write code to connect with redis and expose methods like save, get, delete etc
    public void save(String key, Object object) {
        ValueOperations<String, String> ops = circuitBreakerRedis.opsForValue();
        String objectAsString = null;
        try {
            objectAsString = RedisObjectSerializer.serialize(object);
            ops.set(key, objectAsString);
        } catch (JsonProcessingException e) {
            log.error("Error while serializing object", e);
        }
    }

    public <T> T get(String key, Class<T> type) {
        ValueOperations<String, String> ops = circuitBreakerRedis.opsForValue();
        String objectAsString = ops.get(key);
        try {
            return objectAsString != null ? RedisObjectSerializer.deserialize(objectAsString, type) : null;
        } catch (IOException e) {
            log.error("Error while deserializing object", e);
        }
        return null;
    }
}
