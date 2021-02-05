package com.example.demoapp.compute;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ElapsedTaskResult<T> {
    private final T value;
    private final long elapsedNanos;
}
