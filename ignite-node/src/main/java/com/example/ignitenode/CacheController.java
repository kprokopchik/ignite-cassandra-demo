package com.example.ignitenode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.cache.configuration.Factory;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
@Slf4j
public class CacheController {

    private final Ignite ignite;

    @PostMapping("/load/{cacheName}")
    public void load(
            @PathVariable("cacheName") String cacheName
    ) {
        IgniteCache<Object, Object> cache = ignite.cache(cacheName);
        cache.loadCache(null);
    }

    @PostMapping("/evict/{cacheName}")
    public void evict(
            @PathVariable("cacheName") String cacheName
    ) {
        IgniteCache<Object, Object> cache = ignite.cache(cacheName);
        cache.clear();
    }

    @PostMapping("/ttl/{cacheName}")
    public Duration ttl(
            @PathVariable("cacheName") String cacheName,
            @RequestParam(value = "timeUnit", required = false, defaultValue = "SECONDS") TimeUnit timeUnit,
            @RequestParam("ttl") long ttl
    ) {
        IgniteCache<Object, Object> cache = ignite.cache(cacheName);
        Duration ttlDuration = new Duration(timeUnit, ttl);
        Factory<ExpiryPolicy> newExpiryPolicyFactory = CreatedExpiryPolicy.factoryOf(ttlDuration);
        CacheConfiguration cacheConfiguration = cache.getConfiguration(CacheConfiguration.class);
        log.info("Destroying cache: {}", cache);
        ignite.destroyCache(cacheName);
        cacheConfiguration.setExpiryPolicyFactory(newExpiryPolicyFactory);
        log.info("Creating cache {} with modified TTL: {}", cache, cacheConfiguration);
        ignite.createCache(cacheConfiguration);
        return ttlDuration;
    }
}
