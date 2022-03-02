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

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.annotation.ParametersAreNonnullByDefault;
import io.github.matyrobbrt.curseforgeapi.util.Constants;
import io.github.matyrobbrt.curseforgeapi.util.ExceptionFunction;
import io.github.matyrobbrt.curseforgeapi.util.ExceptionSupplier;

/**
 * A response object which may or may not contain a value. Similar
 * Implementation to {@link Optional}.
 * 
 * @author        matyrobbrt
 *
 * @param  <T>the type of value
 * @see           Optional
 */
@ParametersAreNonnullByDefault
public final class Response<T> {

    /**
     * Returns an empty {@code Response} instance. No value is present for this
     * {@code Response}.
     * 
     * @param  statusCode the <b>nullable</b> status code of the {@link Response}
     * @param  <T>        The type of the non-existent value
     * @return            an empty {@code Response}
     */
    public static <T> Response<T> empty(@Nullable Integer statusCode) {
        return new Response<>(null, statusCode);
    }

    /**
     * Returns an {@code Response} describing the given non-{@code null} value.
     *
     * @param  value                the value to describe, which must be
     *                              non-{@code null}
     * @param  statusCode           the <b>nullable</b> status code of the
     *                              {@link Response}
     * @param  <T>                  the type of the value
     * @return                      an {@code Response} with the value present
     * @throws NullPointerException if value is {@code null}
     */
    public static <T> Response<T> of(@Nonnull T value, @Nullable Integer statusCode) {
        return new Response<>(Objects.requireNonNull(value), statusCode);
    }

    /**
     * Returns an {@code Response} describing the given value, if non-{@code null},
     * otherwise returns an empty {@code Response}.
     *
     * @param  value      the possibly-{@code null} value to describe
     * @param  statusCode the <b>nullable</b> status code of the {@link Response}
     * @param  <T>        the type of the value
     * @return            an {@code Response} with a present value if the specified
     *                    value is non-{@code null}, otherwise an empty
     *                    {@code Response}
     */
    public static <T> Response<T> ofNullable(@Nullable T value, @Nullable Integer statusCode) {
        return value == null ? empty(statusCode) : of(value, statusCode);
    }
    
    public static <T> Response<T> ofNullableAndStatusCode(@Nullable T value, @Nullable Integer statusCode) {
        if (statusCode == Constants.StatusCodes.NOT_FOUND) {
            return empty(statusCode);
        }
        return value == null ? empty(statusCode) : of(value, statusCode);
    }

    private final T value;
    private final Integer statusCode;

    private Response(@Nullable T value, @Nullable Integer statusCode) {
        this.value = value;
        this.statusCode = statusCode;
    }

    /**
     * @return the status code of this response
     */
    @Nonnull
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     * @return {@code true} if a value is present, otherwise {@code false}
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * @return {@code true} if a value is not present, otherwise {@code false}
     */
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * If a value is present, performs the given action with the value, otherwise
     * does nothing.
     *
     * @param  action               the action to be performed, if a value is
     *                              present
     * @throws NullPointerException if value is present and the given action is
     *                              {@code null}
     */
    public void ifPresent(Consumer<? super T> action) {
        if (value != null) {
            action.accept(value);
        }
    }

    /**
     * If a value is present, performs the given action with the value, otherwise
     * performs the given empty-based action.
     *
     * @param  action               the action to be performed, if a value is
     *                              present
     * @param  emptyAction          the empty-based action to be performed, if no
     *                              value is present
     * @throws NullPointerException if a value is present and the given action is
     *                              {@code null}, or no value is present and the
     *                              given empty-based action is {@code null}.
     */
    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        if (value != null) {
            action.accept(value);
        } else {
            emptyAction.run();
        }
    }

    /**
     * If a value is present, and the value matches the given predicate, returns an
     * {@code Response} describing the value, otherwise returns an empty
     * {@code Response}. The {@link #statusCode} is copied in the new
     * {@link Response}.
     *
     * @param  predicate            the predicate to apply to a value, if present
     * @return                      an {@code Response} describing the value of this
     *                              {@code Response}, if a value is present and the
     *                              value matches the given predicate, otherwise an
     *                              empty {@code Response}
     * @throws NullPointerException if the predicate is {@code null}
     */
    public Response<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent()) {
            return this;
        } else {
            return predicate.test(value) ? this : empty(statusCode);
        }
    }

    /**
     * If a value is present, returns an {@code Response} describing (as if by
     * {@link #ofNullable}) the result of applying the given mapping function to the
     * value, otherwise returns an empty {@code Response}. <br>
     * The {@link #statusCode} is copied in the new {@link Response}.
     *
     * <p>
     * If the mapping function returns a {@code null} result then this method
     * returns an empty {@code Response}.
     *
     * @param  mapper               the mapping function to apply to a value, if
     *                              present
     * @param  <U>                  The type of the value returned from the mapping
     *                              function
     * @return                      an {@code Response} describing the result of
     *                              applying a mapping function to the value of this
     *                              {@code Response}, if a value is present,
     *                              otherwise an empty {@code Response}
     * @throws NullPointerException if the mapping function is {@code null}
     */
    public <U> Response<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty(statusCode);
        } else {
            return Response.ofNullable(mapper.apply(value), statusCode);
        }
    }

    /**
     * Map the value inside the {@link Response} to another one if present, or else,
     * return the supplied {@code orElse}.
     * 
     * @param  <U>    the type of the new value
     * @param  mapper the mapper if the value is present
     * @param  orElse the value to return is the value is not present
     * @return        the mapped value
     */
    public <U> U mapOrElse(Function<? super T, ? extends U> mapper, Supplier<U> orElse) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return mapper.apply(value);
        } else {
            return orElse.get();
        }
    }

    /**
     * Map the value inside the {@link Response} to another one if present, or else,
     * return the supplied {@code orElse}. <br>
     * Opposed to {@link #mapOrElse(Function, Supplier)}, this method accepts an
     * {@link ExceptionFunction} and {@link ExceptionSupplier} for providing the
     * mapper and the supplier.
     * 
     * @param  <U>    the type of the new value
     * @param  mapper the mapper if the value is present
     * @param  orElse the value to return is the value is not present
     * @return        the mapped value
     */
    public <U> U mapOrElseWithException(Function<? super T, ? extends U> mapper, Supplier<U> orElse) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return mapper.apply(value);
        } else {
            return orElse.get();
        }
    }

    /**
     * If a value is present, returns the result of applying the given
     * {@code Response}-bearing mapping function to the value, otherwise returns an
     * empty {@code Response}.
     *
     * <p>
     * This method is similar to {@link #map(Function)}, but the mapping function is
     * one whose result is already an {@code Response}, and if invoked,
     * {@code flatMap} does not wrap it within an additional {@code Response}.
     *
     * @param  <U>                  The type of value of the {@code Response}
     *                              returned by the mapping function
     * @param  mapper               the mapping function to apply to a value, if
     *                              present
     * @return                      the result of applying an
     *                              {@code Response}-bearing mapping function to the
     *                              value of this {@code Response}, if a value is
     *                              present, otherwise an empty {@code Response}
     * @throws NullPointerException if the mapping function is {@code null} or
     *                              returns a {@code null} result
     */
    public <U> Response<U> flatMap(Function<? super T, ? extends Response<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return empty(statusCode);
        } else {
            @SuppressWarnings("unchecked")
            Response<U> r = (Response<U>) mapper.apply(value);
            return Objects.requireNonNull(r);
        }
    }

    /**
     * If a response value is present, returns an {@code Response} describing the
     * value, otherwise returns an {@code Response} produced by the supplying
     * function.
     *
     * @param  supplier             the supplying function that produces an
     *                              {@code Response} to be returned
     * @return                      returns an {@code Response} describing the value
     *                              of this {@code Response}, if a value is present,
     *                              otherwise an {@code Response} produced by the
     *                              supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *                              produces a {@code null} result
     */
    public Response<T> or(Supplier<? extends Response<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        if (isPresent()) {
            return this;
        } else {
            @SuppressWarnings("unchecked")
            Response<T> r = (Response<T>) supplier.get();
            return Objects.requireNonNull(r);
        }
    }

    /**
     * If a response value is present, returns a sequential {@link Stream}
     * containing only that value, otherwise returns an empty {@code Stream}.
     *
     * @apiNote This method can be used to transform a {@code Stream} of responses
     *          to a {@code Stream} of present value elements:
     *
     * @return  the response value as a {@code Stream}
     */
    public Stream<T> stream() {
        if (!isPresent()) {
            return Stream.empty();
        } else {
            return Stream.of(value);
        }
    }

    /**
     * If a value is present, returns the value, otherwise returns {@code other}.
     *
     * @param  other the value to be returned, if no value is present. May be
     *               {@code null}.
     * @return       the value, if present, otherwise {@code other}
     */
    public T orElse(T other) {
        return value != null ? value : other;
    }

    /**
     * If a value is present, returns the value, otherwise returns the result
     * produced by the supplying function.
     *
     * @param  supplier             the supplying function that produces a value to
     *                              be returned
     * @return                      the value, if present, otherwise the result
     *                              produced by the supplying function
     * @throws NullPointerException if no value is present and the supplying
     *                              function is {@code null}
     */
    public T orElseGet(Supplier<? extends T> supplier) {
        return value != null ? value : supplier.get();
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @apiNote                        The preferred alternative to this method is
     *                                 {@link #orElseThrow()}.
     *
     * @return                         the non-{@code null} value described by this
     *                                 {@code Response}
     * @throws  NoSuchElementException if no value is present
     */
    public T get() {
        if (value == null) { throw new NoSuchElementException("No value present"); }
        return value;
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return                        the non-{@code null} value described by this
     *                                {@code Response}
     * @throws NoSuchElementException if no value is present
     */
    public T orElseThrow() {
        return orElseThrow(() -> new NoSuchElementException("No value present"));
    }

    /**
     * If a value is present, returns the value, otherwise throws an exception
     * produced by the exception supplying function.
     *
     * @apiNote                      A method reference to the exception constructor
     *                               with an empty argument list can be used as the
     *                               supplier. For example,
     *                               {@code IllegalStateException::new}
     *
     * @param   <X>                  Type of the exception to be thrown
     * @param   exceptionSupplier    the supplying function that produces an
     *                               exception to be thrown
     * @return                       the value, if present
     * @throws  X                    if no value is present
     * @throws  NullPointerException if no value is present and the exception
     *                               supplying function is {@code null}
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Converts this {@link Request} to an {@link Optional}. The returned
     * {@link Optional} is empty if the {@link Response} is empty.
     * 
     * @return the {@link Optional}
     */
    public Optional<T> toOptional() {
        return isPresent() ? Optional.of(value) : Optional.empty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }

        return obj instanceof Response<?> other && Objects.equals(value, other.value) && statusCode == other.statusCode;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value) + 31 * Objects.hashCode(statusCode);
    }

}
