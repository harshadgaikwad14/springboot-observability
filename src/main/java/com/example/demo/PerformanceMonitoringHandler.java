package com.example.demo;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PerformanceMonitoringHandler implements ObservationHandler<Observation.Context> {

    private static final Logger log = LoggerFactory.getLogger(PerformanceMonitoringHandler.class);

    @Override
    public void onStart(Observation.Context context) {
        //context.getAllKeyValues().stream().iterator().forEachRemaining(System.out::println);
        log.info("Execution Started: {}", context.getName());
        context.put("executionTime", System.currentTimeMillis());
    }

    @Override
    public void onStop(Observation.Context context) {
        //context.getAllKeyValues().stream().iterator().forEachRemaining(System.out::println);
        
        long startTime = context.getOrDefault("executionTime", 0L);
        long executionTime = System.currentTimeMillis() - startTime;
        log.info("Execution Stopped: {} | Total Time Taken: {} ms", context.getName(), executionTime);
    }

    @Override
    public void onError(Observation.Context context) {
        log.info("Error Encountered: {} | Error: {}", context.getName(), (context.getError() != null ? context.getError().getMessage() : null));
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }
}