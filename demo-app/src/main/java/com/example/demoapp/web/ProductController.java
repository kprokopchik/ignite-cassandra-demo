package com.example.demoapp.web;

import com.example.demoapp.cache.repository.ProductCachedRepository;
import com.example.data.model.Product;
import com.example.demoapp.repository.ProductReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductController implements ProductReactiveApi {

    private final ProductReactiveRepository productReactiveRepository;
    private final ProductCachedRepository productCachedRepository;

    @Override
    public Mono<Product> getById(UUID id, boolean useCache) {
        Mono<Product> res;
        if (useCache) {
            res = Mono.justOrEmpty(productCachedRepository.cache().get(id));
        } else {
            res = productReactiveRepository.findById(id);
        }
        return res;
    }

}
