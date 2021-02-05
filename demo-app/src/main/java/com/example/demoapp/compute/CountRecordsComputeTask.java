package com.example.demoapp.compute;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobAdapter;
import org.apache.ignite.compute.ComputeJobContext;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeJobSibling;
import org.apache.ignite.compute.ComputeTaskSession;
import org.apache.ignite.compute.ComputeTaskSessionFullSupport;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.JobContextResource;
import org.apache.ignite.resources.TaskSessionResource;
import org.springframework.util.ReflectionUtils;

import javax.cache.Cache;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Since Ignite randomly distribute computational tasks among nodes we can not use it to properly calculate
 * cached records (i.e. some nodes may execute the task multiple times while other nodes might miss the task)
 */
@Slf4j
@ComputeTaskSessionFullSupport
public class CountRecordsComputeTask<T, V> extends ComputeTaskSplitAdapter<CountRecordsTaskDef<T, V>, ElapsedTaskResult<Long>> {

    public static final String ELAPSED_NANOS_ATTR = "ELAPSED_NANOS";

    @Override
    protected Collection<? extends ComputeJob> split(int gridSize, CountRecordsTaskDef<T, V> def) throws IgniteException {
        log.info("Split task in {} jobs to use the whole grid", gridSize);
        ArrayList<ComputeJob> computeJobs = new ArrayList<>();


        for (int i = 0; i < gridSize; i++) {
            computeJobs.add(new ComputeJobAdapter() {

                // Auto-injected task session.
                @TaskSessionResource
                private ComputeTaskSession ses;

                // Auto-injected job context.
                @JobContextResource
                private ComputeJobContext jobCtx;

                @IgniteInstanceResource
                private Ignite ignite;

                @Override
                public Object execute() throws IgniteException {

                    long ts = System.nanoTime();

                    Field field = ReflectionUtils.findField(def.getCachedType(), def.getFieldName());
                    field.setAccessible(true);
                    ScanQuery<Object, T> query = new ScanQuery<>((k, v) ->
                            def.getValueRange().contains((V) ReflectionUtils.getField(field, v))
                    );
                    query.setLocal(true);

                    IgniteCache<Object, T> cache = ignite.cache(def.getCacheName());

                    QueryCursor<Cache.Entry<Object, T>> cursor = cache.query(query);

                    Long count = StreamSupport.stream(cursor.spliterator(), false)
                            .count();

                    log.info("Job {} calculated records: {}", jobCtx.getJobId(), count);

                    // Tell other jobs that STEP1 is complete.
                    ses.setAttribute(jobCtx.getJobId(), "STEP1");

                    log.info("Job {} waiting for others...", jobCtx.getJobId());

                    // Wait for other jobs to complete STEP1.
                    for (ComputeJobSibling sibling : ses.getJobSiblings()) {
                        try {
                            ses.waitForAttribute(sibling.getJobId(), "STEP1", 0);
                        } catch (InterruptedException e) {
                            log.warn("Job {} interrupted", jobCtx.getJobId());
                        }
                    }

                    log.info("Others are finished.");

                    long elapsedNanos = System.nanoTime() - ts;

                    jobCtx.setAttribute(ELAPSED_NANOS_ATTR, elapsedNanos);

                    return count;
                }
            });
        }
        return computeJobs;
    }

    @Override
    public ElapsedTaskResult<Long> reduce(List<ComputeJobResult> results) throws IgniteException {
        long sum = results.stream()
                .map(ComputeJobResult::getData)
                .filter(ObjectUtils::isNotEmpty)
                .mapToLong(Long.class::cast)
                .sum();
        long elapsedNanosMax = results.stream()
                .map(ComputeJobResult::getJobContext)
                .map(ctx -> ctx.getAttribute(ELAPSED_NANOS_ATTR))
                .filter(ObjectUtils::isNotEmpty)
                .mapToLong(Long.class::cast)
                .max().orElseThrow();
        return new ElapsedTaskResult<>(sum, elapsedNanosMax);
    }
}
