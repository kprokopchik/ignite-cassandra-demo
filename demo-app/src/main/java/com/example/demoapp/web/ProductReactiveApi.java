package com.example.demoapp.web;

import com.example.data.model.Product;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Api(tags = "product")
@RequestMapping("/product")
public interface ProductReactiveApi {

    @GetMapping("/{id}")
    Mono<Product> getById(
            @PathVariable("id") UUID id,
            @RequestParam(value = "useCache", required = false, defaultValue = "true") boolean useCache
    );

}
