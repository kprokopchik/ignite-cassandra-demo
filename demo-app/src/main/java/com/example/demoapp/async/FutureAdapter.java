package com.example.demoapp.async;

import org.apache.ignite.lang.IgniteFuture;
import org.apache.ignite.lang.IgniteInClosure;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;

public class FutureAdapter<T> extends CompletableFuture<T> {

    private final IgniteFuture<T> igniteFuture;

    public FutureAdapter(IgniteFuture<T> igniteFuture) {
        this.igniteFuture = igniteFuture;

        igniteFuture.listen((IgniteInClosure<IgniteFuture<T>>) completedFuture -> {
            try {
                complete(completedFuture.get());
            } catch (Exception e) {
                completeExceptionally(e);
            }
        });
    }

    @Override
    public T get() {
        return igniteFuture.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) {
        return igniteFuture.get(timeout, unit);
    }

    @Override
    public T getNow(T valueIfAbsent) {
        return igniteFuture.isDone() ? igniteFuture.get() : valueIfAbsent;
    }

    @Override
    public T join() {
        return igniteFuture.get();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return igniteFuture.cancel();
    }

    @Override
    public boolean isCancelled() {
        return igniteFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return igniteFuture.isDone();
    }

    @Override
    public CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
        return super.whenComplete(action);
    }

    public static <T> Mono<T> asMono(IgniteFuture<T> igniteFuture) {
        return Mono.fromFuture(new FutureAdapter<>(igniteFuture));
    }

    public static <T> Mono<T> asMono(IgniteFuture<Collection<T>> igniteFuture, BinaryOperator<T> f) {
        CompletableFuture<Collection<T>> completableFuture = new FutureAdapter<>(igniteFuture);
        CompletableFuture<T> accumulated = completableFuture.thenApply(collection -> collection.stream()
                .reduce(f)
                .orElse(null));
        return Mono.fromFuture(accumulated);
    }

}
