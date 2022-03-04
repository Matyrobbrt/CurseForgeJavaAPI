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

package io.github.matyrobbrt.curseforgeapi.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public final class Utils {

    public static <T> T makeWithSupplier(Supplier<T> supplier) {
        return supplier.get();
    }

    public static List<JsonElement> jsonArrayToList(JsonArray array) {
        final var list = new ArrayList<JsonElement>();
        array.forEach(list::add);
        return list;
    }

    /**
     * Sneakily throws the given exception, bypassing compile-time checks for
     * checked exceptions.
     *
     * <p>
     * <strong>This method will never return normally.</strong> The exception passed
     * to the method is always rethrown.
     * </p>
     *
     * @param ex the exception to sneakily rethrow
     */
    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void sneakyThrow(Throwable ex) throws E {
        throw (E) ex;
    }

    /**
     * Converts the given exception supplier into a regular supplier, rethrowing any
     * exception as needed.
     *
     * @param  supplier the exception supplier to convert
     * @param  <T>      the type of results supplied by this supplier
     * @return          a supplier which returns the result from the given throwing
     *                  supplier, rethrowing any exceptions
     * @see             #sneakyThrow(Throwable)
     */
    public static <T> Supplier<T> rethrowSupplier(final ExceptionSupplier<T, ?> supplier) {
        return supplier::getNoException;
    }

    /**
     * Converts the given exception function into a regular function, rethrowing any
     * exception as needed.
     *
     * @param  function the exception function to convert
     * @param  <T>      the type of the input to the function
     * @param  <R>      the type of the result of the function
     * @return          a function which applies the given throwing function,
     *                  rethrowing any exceptions
     * @see             #sneakyThrow(Throwable)
     */
    public static <T, R> Function<T, R> rethrowFunction(final ExceptionFunction<T, R, ?> function) {
        return function::applyNoException;
    }

    /**
     * Converts the given exception consumer into a regular consumer, rethrowing any
     * exception as needed.
     *
     * @param  consumer the exception consumer to convert
     * @param  <T>      the type of the input to the operation
     * @return          a runnable which passes to the given throwing consumer,
     *                  rethrowing any exceptions
     * @see             #sneakyThrow(Throwable)
     */
    public static <T> Consumer<T> rethrowConsumer(final ExceptionConsumer<T, ?> consumer) {
        return consumer;
    }

    /**
     * Converts the given exception runnable into a regular runnable, rethrowing any
     * exception as needed.
     *
     * @param  runnable the exception runnable to convert
     * @return          a runnable which runs the given throwing runnable,
     *                  rethrowing any exceptions
     * @see             #sneakyThrow(Throwable)
     */
    public static Runnable rethrowRunnable(final ExceptionRunnable<?> runnable) {
        return runnable;
    }
}
