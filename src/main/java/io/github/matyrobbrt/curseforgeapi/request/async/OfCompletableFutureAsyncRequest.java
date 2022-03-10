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

package io.github.matyrobbrt.curseforgeapi.request.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;
import io.github.matyrobbrt.curseforgeapi.request.AsyncRequest;

public record OfCompletableFutureAsyncRequest<T> (@Nonnull CompletableFuture<T> future) implements AsyncRequest<T> {

    public static void setFutureExecutor(@Nonnull Executor executor) {
        AsyncRequestValues.setFutureExecutor(executor);
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public void queue(Consumer<? super T> onSuccess, Consumer<? super Throwable> onFailure) {
        AsyncRequestValues.futureExecutor.execute(() -> {
            try {
                final var tFuture = this.future.whenComplete((o, t) -> {
                    if (o != null && onSuccess != null) {
                        onSuccess.accept(o);
                    }
                    if (t != null) {
                        if (onFailure != null) {
                            onFailure.accept(t);
                        } else {
                            AsyncRequestValues.defaultFailure.accept(t);
                        }
                    }
                });
                while (!tFuture.isDone()) {
                    Thread.sleep(100);
                }
                tFuture.get();
            } catch (ExecutionException e) {
                if (onFailure != null) {
                    onFailure.accept(e.getCause());
                }
            } catch (InterruptedException e) {
                LOGGER.error("InterruptedException while awaiting request!", e);
                throw new RuntimeException(e);
            }
        });
    }

}
