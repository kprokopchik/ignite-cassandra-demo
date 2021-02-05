package com.example.ignitenode;

import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMetrics;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import javax.cache.configuration.Factory;
import javax.cache.expiry.ExpiryPolicy;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class IgniteInfoReporter implements InfoContributor {

    private final Ignite ignite;

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("ignite-cache-Stats", collectAllCachesMetrics());
        builder.withDetail("ignite-cache-ExpiryPolicy", collectAllCachesExpiryPolicy());
    }

    private Map<String, CacheMetrics> collectAllCachesMetrics() {
        return ignite.cacheNames().stream()
                .map(ignite::cache)
                .collect(toMap(IgniteCache::getName, IgniteCache::metrics));
    }

    private Map<String, ExpiryPolicy> collectAllCachesExpiryPolicy() {
        return ignite.cacheNames().stream()
                .map(ignite::cache)
                .collect(toMap(IgniteCache::getName, this::getExpiryPolicy));
    }

    private <K, V> ExpiryPolicy getExpiryPolicy(IgniteCache<K, V> cache) {
        CacheConfiguration<K, V> cacheConfiguration = cache.getConfiguration(CacheConfiguration.class);
        Factory<ExpiryPolicy> expiryPolicyFactory = cacheConfiguration.getExpiryPolicyFactory();
        return expiryPolicyFactory.create();
    }
}
