package io.github.matyrobbrt.curseforgeapi.util;

import java.util.function.Consumer;

/**
 * Represents an operation that accepts a single input argument and returns no
 * result. Unlike most other functional interfaces, {@code Consumer} is expected
 * to operate via side-effects. This {@link Consumer} implementation can throw
 * an exception.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #accept(Object)}.
 *
 * @param <T> the type of the input to the operation
 *
 * @since     1.8
 */
@FunctionalInterface
public interface ExceptionConsumer<T, E extends Exception> extends Consumer<T> {

    /**
     * Performs this operation on the given argument, potentially throwing an
     * exception.
     *
     * @param t the input argument
     */
    void acceptWithException(T t) throws E;

    /**
     * {@inheritDoc}
     *
     * @implSpec This calls {@link #acceptWithException(Object)}, and rethrows any
     *           exception using {@link Utils#sneakyThrow(Throwable)}.
     */
    @Override
    default void accept(T t) {
        try {
            acceptWithException(t);
        } catch (final Throwable e) {
            Utils.sneakyThrow(e);
        }
    }

}
