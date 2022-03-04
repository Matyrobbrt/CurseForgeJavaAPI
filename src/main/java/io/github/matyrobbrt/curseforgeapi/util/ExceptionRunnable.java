package io.github.matyrobbrt.curseforgeapi.util;

/**
 * A special {@link Runnable} implementation which can throw an exception.
 * 
 * @param <E> the type of the exception that can be thrown
 */
@FunctionalInterface
public interface ExceptionRunnable<E extends Exception> extends Runnable {

    /**
     * Takes the action, potentially throwing an exception.
     */
    void runWithException() throws E;

    /**
     * {@inheritDoc}
     *
     * @implSpec This calls {@link #runWithException()}, and rethrows any exception using {@link Utils#sneakyThrow(Throwable)}.
     */
    @Override
    default void run() {
        try {
            runWithException();
        } catch (Throwable t) {
            Utils.sneakyThrow(t);
        }
    }

}
