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

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.request.async.AsyncRequestValues;
import io.github.matyrobbrt.curseforgeapi.request.async.MapAsyncRequest;
import io.github.matyrobbrt.curseforgeapi.util.Pair;

/**
 * A variant of {@link AsyncRequest}, which contains 2 values.
 * 
 * @author     matyrobbrt
 *
 * @param  <F> the type of the first value
 * @param  <S> the type of the second value
 */
public interface DoubleAsyncRequest<F, S> extends AsyncRequest<Pair<F, S>> {
    
    /**
     * Maps this request
     * 
     * @param  <U>    the new type of the request
     * @param  mapper the mapper to use
     * @return        the request
     */
    @Nonnull
    default <U> AsyncRequest<U> map(@Nonnull BiFunction<? super F, ? super S, ? extends U> mapper) {
        return new MapAsyncRequest<>(this, p -> mapper.apply(p.first(), p.second()));
    }

    /**
     * Queues the action for later completion.
     * 
     * @param onSuccess the consumer to run when the action succeeded
     */
    default void queue(@Nullable BiConsumer<? super F, ? super S> onSuccess) {
        queue(onSuccess, AsyncRequestValues.getDefaultFailure());
    }

    /**
     * Queues the action for later completion.
     * 
     * @param onSuccess the consumer to run when the action succeeded
     * @param onFailure the consumer to run when the action fails, or is empty (it
     *                  fails with the {@link EmptyRequestException})
     */
    void queue(@Nullable BiConsumer<? super F, ? super S> onSuccess, @Nullable Consumer<? super Throwable> onFailure);

}
