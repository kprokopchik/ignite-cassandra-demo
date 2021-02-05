package com.example.demoapp.compute;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Range;

import java.io.Serializable;

/**
 * Holds conditions required to perform calculation task.
 */
@RequiredArgsConstructor
@Data
@Builder
public class CountRecordsTaskDef<T, V> implements Serializable {

    private final String cacheName;
    private final Class<T> cachedType;
    private final Range<V> valueRange;
    private final String fieldName;
}
