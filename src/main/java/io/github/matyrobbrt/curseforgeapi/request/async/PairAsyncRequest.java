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

import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.github.matyrobbrt.curseforgeapi.request.AsyncRequest;
import io.github.matyrobbrt.curseforgeapi.request.DoubleAsyncRequest;
import io.github.matyrobbrt.curseforgeapi.util.Pair;

public record PairAsyncRequest<F, S> (AsyncRequest<F> first, AsyncRequest<S> second)
    implements DoubleAsyncRequest<F, S> {

    @Override
    public Pair<F, S> get() throws InterruptedException, ExecutionException {
        return Pair.of(first.get(), second.get());
    }
    
    @Override
    public void queue(Consumer<? super Pair<F, S>> onSuccess, Consumer<? super Throwable> onFailure) {
        first.queue(f -> second.queue(s -> {
            if (onSuccess != null) {
                onSuccess.accept(Pair.of(f, s));
            }
        }, onFailure), onFailure);
    }

    @Override
    public void queue(BiConsumer<? super F, ? super S> onSuccess, Consumer<? super Throwable> onFailure) {
        first.queue(f -> second.queue(s -> {
            if (onSuccess != null) {
                onSuccess.accept(f, s);
            }
        }, onFailure), onFailure);
    }
}
