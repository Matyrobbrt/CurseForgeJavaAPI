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

import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;

import static io.github.matyrobbrt.curseforgeapi.request.AsyncRequest.LOGGER;

public class AsyncRequestValues {

    @Nonnull
    static Executor futureExecutor = Executors
        .newSingleThreadExecutor(r -> new Thread(r, "AsyncRequestHandler"));

    static Consumer<? super Throwable> defaultFailure = t -> {
        if (t instanceof CancellationException || t instanceof TimeoutException)
            LOGGER.debug(t.getMessage());
        else if (t.getCause() != null)
            LOGGER.error("AsyncRequest queue returned failure: [{}] {}", t.getClass().getSimpleName(), t.getMessage(),
                t.getCause());
        else
            LOGGER.error("AsyncRequest queue returned failure: [{}] {}", t.getClass().getSimpleName(), t.getMessage());
    };
    
    @Nonnull
    public static Consumer<? super Throwable> getDefaultFailure() {
        return defaultFailure;
    }

    public static void setDefaultFailure(final Consumer<? super Throwable> callback) {
        defaultFailure = callback == null ? t -> {} : callback;
    }

    public static void setFutureExecutor(@Nonnull Executor executor) {
        futureExecutor = Objects.requireNonNull(executor);
    }
    
}
