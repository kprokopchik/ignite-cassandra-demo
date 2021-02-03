package com.example.demoapp.repository;

import com.example.data.model.Product;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductReactiveRepository extends ReactiveCassandraRepository<Product, UUID> {
}
