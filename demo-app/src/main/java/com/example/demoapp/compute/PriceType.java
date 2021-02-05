package com.example.demoapp.compute;

import com.example.data.model.Product;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.function.Function;

@RequiredArgsConstructor
public enum PriceType {
    LIST(Product::getListPrice, "listPrice"),
    SELL(Product::getSalePrice, "sellPrice");

    private final Function<Product, BigDecimal> extractor;
    private final String fieldName;

    public Function<Product, BigDecimal> getter() {
        return extractor;
    }

    public BigDecimal get(Product product) {
        return extractor.apply(product);
    }

    public String field() {
        return fieldName;
    }
}
