package com.example.demoapp.cache.repository;

import com.example.data.model.Product;
import org.apache.ignite.springdata20.repository.IgniteRepository;
import org.apache.ignite.springdata20.repository.config.RepositoryConfig;

import java.util.UUID;

@RepositoryConfig(
        cacheName = "Product",
        autoCreateCache = true
)
public interface ProductCachedRepository extends IgniteRepository<Product, UUID> {
}
