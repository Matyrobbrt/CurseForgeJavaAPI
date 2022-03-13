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
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.matyrobbrt.curseforgeapi.request.AsyncRequest;

@SuppressWarnings("unchecked")
public final class EmptyAsyncRequest<T> implements AsyncRequest<T> {
    public static final EmptyAsyncRequest<?> INSTANCE = new EmptyAsyncRequest<>();
    private EmptyAsyncRequest() {}

    @Override
    public <U> AsyncRequest<U> map(Function<? super T, ? extends U> mapper) {
        return (AsyncRequest<U>) INSTANCE;
    }

    @Override
    public <U> AsyncRequest<U> flatMap(Function<? super T, ? extends AsyncRequest<U>> mapper) {
        return (AsyncRequest<U>) INSTANCE;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        throw new ExecutionException(new AsyncRequest.EmptyRequestException());
    }

    @Override
    public void queue(Consumer<? super T> onSuccess, Consumer<? super Throwable> onFailure) {
        if (onFailure != null && AsyncRequestValues.emptyRequestThrows) {
            onFailure.accept(new AsyncRequest.EmptyRequestException());
        }
    }

}
