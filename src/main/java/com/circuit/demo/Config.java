package com.circuit.demo;

import io.github.resilience4j.circuitbreaker.internal.CircuitBreakerStateMachine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class Config {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConFactory
                = new JedisConnectionFactory();
        jedisConFactory.setHostName(redisHost);
        jedisConFactory.setPort(redisPort);
        return jedisConFactory;
    }

    @Bean(name = "circuitBreakerRedis")
    public RedisTemplate<String, String> circuitRedisOperations(JedisConnectionFactory factory) {

        RedisTemplate<String, String> template = new RedisTemplate<>();

        Jackson2JsonRedisSerializer<String> serializer = new Jackson2JsonRedisSerializer<>(String.class);
        template.setConnectionFactory(factory);
        template.setDefaultSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }
}
