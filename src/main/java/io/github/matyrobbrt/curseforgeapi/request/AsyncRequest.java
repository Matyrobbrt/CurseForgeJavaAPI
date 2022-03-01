/*
 * This file is part of the CurseForge Java API library and is licensed under
 * the MIT license:
 *
 * MIT License
 *
 * Copyright (c) 2022 Matyrobbrt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.matyrobbrt.curseforgeapi.request;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.util.ExceptionFunction;

/**
 * An object used for async requests.
 * 
 * @author     matyrobbrt
 *
 * @param  <T> the response type
 */
public final class AsyncRequest<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncRequest.class);
    @Nonnull
    private static Executor futureExecutor = Executors
        .newSingleThreadExecutor(r -> new Thread(r, "AsyncRequestHandler"));

    public static void setFutureExecutor(@Nonnull Executor executor) {
        futureExecutor = Objects.requireNonNull(executor);
    }
    
    public static <T> AsyncRequest<T> empty() {
        return new AsyncRequest<>(() -> CompletableFuture.failedFuture(null));
    }

    private final Supplier<CompletableFuture<? super T>> future;

    public AsyncRequest(Supplier<CompletableFuture<? super T>> future) {
        this.future = future;
    }

    /**
     * Maps this request
     * 
     * @param  <U>    the new type of the request
     * @param  mapper the mapper to use
     * @return        the request
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public <U> AsyncRequest<U> map(@Nonnull Function<? super T, ? super U> mapper) {
        return new AsyncRequest<>(
            () -> future.get().thenCompose(o -> CompletableFuture.completedFuture(mapper.apply((T) o))));
    }

    /**
     * Intermediate operator that returns a modified {@link AsyncRequest}.
     *
     * @param  flatMap The mapping function to apply to the action result, must
     *                 return an {@link AsyncRequest}
     *
     * @param  <O>     The target output type
     *
     * @return         {@link AsyncRequest} for the mapped type
     *
     * @see            #map(Function)
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public <U> AsyncRequest<U> flatMap(@Nonnull Function<? super T, AsyncRequest<U>> mapper) {
        return new AsyncRequest<>(() -> future.get().thenCompose(o -> mapper.apply((T) o).future.get()));
    }

    /**
     * Intermediate operator that returns a modified {@link AsyncRequest}. <br>
     * Opposed to {@link #flatMap(Function)}, this method allows the mapper to throw
     * exceptions, which will be caught and a failed future will be thrown in such
     * cases.
     *
     * @param  flatMap The mapping function to apply to the action result, must
     *                 return an {@link AsyncRequest}
     *
     * @param  <O>     The target output type
     * @param  <O>     The exception type thrown by the mapper
     *
     * @return         {@link AsyncRequest} for the mapped type
     *
     * @see            #flatMap(Function)
     * @see            #map(Function)
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public <U, E extends Exception> AsyncRequest<U> flatMapWithException(
        @Nonnull ExceptionFunction<? super T, AsyncRequest<U>, E> mapper) {
        return new AsyncRequest<>(() -> future.get().thenCompose(o -> {
            try {
                return mapper.apply((T) o).future.get();
            } catch (Exception e) {
                try {
                    @SuppressWarnings("unused")
                    final var castException = (E) e;
                } catch (ClassCastException cc) {  // exception is not of the type we wanted
                    if (e instanceof RuntimeException er) {
                        throw er;
                    }
                }
                return CompletableFuture.failedFuture(e);
            }
        }));
    }

    /**
     * Queues the action for later completion.
     */
    public void queue() {
        queue(null, null);
    }

    /**
     * Queues the action for later completion.
     * 
     * @param onSuccess the consumer to run when the action succeeded
     */
    public void queue(@Nullable Consumer<? super T> onSuccess) {
        queue(onSuccess, null);
    }

    /**
     * Queues the action for later completion.
     * 
     * @param onSuccess the consumer to run when the action succeeded
     * @param onFailure the consumer to run when the action fails
     */
    @SuppressWarnings("unchecked")
    public void queue(@Nullable Consumer<? super T> onSuccess, @Nullable Consumer<? super Throwable> onFailure) {
        futureExecutor.execute(() -> {
            try {
                future.get().whenComplete((o, t) -> {
                    if (o != null && onSuccess != null) {
                        onSuccess.accept((T) o);
                    }
                    if (t != null && onFailure != null) {
                        onFailure.accept(t);
                    }
                }).get();
            } catch (ExecutionException e) {
                LOGGER.error("Exception while awaiting request!", e);
            } catch (InterruptedException e) {
                LOGGER.error("InterruptedException while awaiting request!", e);
                throw new RuntimeException(e);
            }
        });
    }

}
