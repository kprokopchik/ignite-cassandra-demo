package com.example.demoapp.compute;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.springframework.util.ReflectionUtils;

import javax.cache.Cache;
import java.lang.reflect.Field;
import java.util.stream.StreamSupport;

/**
 * The closure intended to be used for broadcasting therefore it processes only local cache on a node.
 *
 * The implementation uses reflection - it is not optimal solution for production and used here just for experiments.
 */
@Slf4j
public class CountRecordsClosure<T, V> implements IgniteClosure<CountRecordsTaskDef<T, V>, ElapsedTaskResult<Long>> {
    private final CountRecordsTaskDef<T, V> taskDef;

    @IgniteInstanceResource
    private Ignite ignite;

    public CountRecordsClosure(CountRecordsTaskDef<T, V> taskDef) {
        this.taskDef = taskDef;
    }

    @Override
    public ElapsedTaskResult<Long> apply(CountRecordsTaskDef<T, V> taskDef) {
        long ts = System.nanoTime();
        Field field = ReflectionUtils.findField(taskDef.getCachedType(), taskDef.getFieldName());
        field.setAccessible(true);
        IgniteCache<Object, Object> cache = ignite.cache(this.taskDef.getCacheName());
        ScanQuery<Object, T> localQuery = new ScanQuery<>((k, v) -> {
            V fieldVal = (V) ReflectionUtils.getField(field, v);
            return taskDef.getValueRange().contains(fieldVal);
        });
        localQuery.setLocal(true);
        QueryCursor<Cache.Entry<Object, T>> localCursor = cache.query(localQuery);
        Long count = StreamSupport.stream(localCursor.spliterator(), false).count();
        long elapsedNanos = System.nanoTime() - ts;
        return new ElapsedTaskResult<>(count, elapsedNanos);
    }
}
