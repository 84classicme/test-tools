package com.example.country;

import reactor.core.publisher.MonoSink;

import javax.xml.ws.AsyncHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Bridge JAX-WS Async API to Reactor.
 *
 * <h2>Usage:</h2>
 *
 * <pre>
 *     Mono.create(sink -> endpoint.method(argument, com.example.feature.ReactorAsyncHandler.into(sink));
 * </pre>
 */
public class ReactorAsyncHandler {
    public static <T> AsyncHandler<T> into(MonoSink<T> sink) {
        return res -> {
            try {
                sink.success(res.get(1, TimeUnit.MILLISECONDS));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                sink.error(e);
            }
        };
    }
}
