package com.dmarket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public void setValues(String key, String data) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    @Transactional
    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if (values.get(key) == null) {
            return "false";
        }
        return (String) values.get(key);
    }

    public boolean checkExistsValue(String value) {
        return !value.equals("false");
    }

    @Transactional
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

//    @Transactional
//    public void expireValues(String key, int timeout) {
//        redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
//    }
//
//    @Transactional
//    public void setHashOps(String key, Map<String, String> data) {
//        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
//        values.putAll(key, data);
//    }
//
//    public String getHashOps(String key, String hashKey) {
//        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
//        return Boolean.TRUE.equals(values.hasKey(key, hashKey)) ? (String) redisTemplate.opsForHash().get(key, hashKey) : "";
//    }
//
//    @Transactional
//    public void deleteHashOps(String key, String hashKey) {
//        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
//        values.delete(key, hashKey);
//    }
}
