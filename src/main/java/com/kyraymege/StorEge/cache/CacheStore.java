package com.kyraymege.StorEge.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class CacheStore<K,V> {
    private final Cache<K, V> cache;

    public CacheStore(int expiryDuration, TimeUnit timeUnit) {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(expiryDuration, timeUnit)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build();
    }

    public V get(@NotNull K key) {
        log.info("CacheStore get called with key: {}", key.toString());
        return cache.getIfPresent(key);
    }

    public void put(@NotNull K key, @NotNull V value) {
        log.info("CacheStore put called with key: {} and value: {}", key.toString(), value.toString());
        cache.put(key, value);
    }

    public void remove(@NotNull K key) {
        log.info("CacheStore remove called with key: {}", key.toString());
        cache.invalidate(key);
    }
}
