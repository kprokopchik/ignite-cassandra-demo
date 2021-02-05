package com.example.demoapp.web;

import com.example.demoapp.async.FutureAdapter;
import com.example.demoapp.cache.repository.ProductCachedRepository;
import com.example.data.model.Product;
import com.example.demoapp.compute.CountRecordsClosure;
import com.example.demoapp.compute.CountRecordsTaskDef;
import com.example.demoapp.compute.ElapsedTaskResult;
import com.example.demoapp.compute.PriceType;
import com.example.demoapp.repository.ProductReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.lang.IgniteFuture;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductController implements ProductReactiveApi {

    private static final BigDecimal MAX_PRICE = BigDecimal.valueOf(Long.MAX_VALUE);

    private final ProductReactiveRepository productReactiveRepository;
    private final ProductCachedRepository productCachedRepository;

    private final Ignite ignite;

    @Override
    public Mono<Product> getById(UUID id, boolean useCache) {
        Mono<Product> res;
        if (useCache) {
            IgniteFuture<Product> igniteFuture = productCachedRepository.cache().getAsync(id);
            return FutureAdapter.asMono(igniteFuture);
        } else {
            res = productReactiveRepository.findById(id);
        }
        return res;
    }

    @Override
    public Mono<ElapsedTaskResult<Long>> countInCacheByPriceRange(BigDecimal from, BigDecimal to, PriceType priceType) {
        BigDecimal min = from == null ? BigDecimal.ZERO : from;
        BigDecimal max = to == null ? MAX_PRICE : to;
        Range<BigDecimal> priceRange = Range.between(min, max);
        CountRecordsTaskDef<Product, BigDecimal> taskDef = new CountRecordsTaskDef<Product, BigDecimal>(
                "Product",
                Product.class,
                priceRange,
                priceType.field()
        );
        CountRecordsClosure<Product, BigDecimal> closure = new CountRecordsClosure<>(taskDef);
        IgniteCompute igniteCompute = ignite.compute();
        IgniteFuture<Collection<ElapsedTaskResult<Long>>> igniteFuture = igniteCompute.broadcastAsync(closure, taskDef);
        return FutureAdapter.asMono(igniteFuture, (result1, result2) -> new ElapsedTaskResult<>(
                result1.getValue() + result2.getValue(),
                result1.getElapsedNanos() + result2.getElapsedNanos()
        ));
    }

}
