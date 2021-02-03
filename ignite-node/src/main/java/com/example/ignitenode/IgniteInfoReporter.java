package com.example.ignitenode;

import lombok.RequiredArgsConstructor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMetrics;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IgniteInfoReporter implements InfoContributor {

    private final Ignite ignite;

    @Override
    public void contribute(Info.Builder builder) {
        IgniteCache<Object, Object> cache = ignite.getOrCreateCache("Product");
        CacheMetrics metrics = cache.metrics();
        builder.withDetail("ignite-cache-Product", metrics);
    }

}
