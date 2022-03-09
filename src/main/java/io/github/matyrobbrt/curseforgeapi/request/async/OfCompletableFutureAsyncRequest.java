package io.github.matyrobbrt.curseforgeapi.request.async;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;
import io.github.matyrobbrt.curseforgeapi.request.AsyncRequest;
import io.github.matyrobbrt.curseforgeapi.util.Pair;
import io.github.matyrobbrt.curseforgeapi.util.Utils;

public record OfCompletableFutureAsyncRequest<T> (@Nonnull CompletableFuture<T> future) implements AsyncRequest<T> {

    @Nonnull
    private static Executor futureExecutor = Executors
        .newSingleThreadExecutor(r -> new Thread(r, "AsyncRequestHandler"));
    
    public static void setFutureExecutor(@Nonnull Executor executor) {
        futureExecutor = Objects.requireNonNull(executor);
    }
    
    @Override
    public <U> AsyncRequest<U> map(Function<? super T, ? extends U> mapper) {
        return new OfCompletableFutureAsyncRequest<>(
            future.thenCompose(o -> CompletableFuture.completedFuture(mapper.apply(o))));
    }

    @Override
    public <U> AsyncRequest<U> flatMap(Function<? super T, ? extends AsyncRequest<U>> mapper) {
        return new OfCompletableFutureAsyncRequest<>(future
            .thenCompose(Utils.rethrowFunction(o -> CompletableFuture.completedFuture(mapper.apply(o).get()))));
    }

    @Override
    public <U> AsyncRequest<Pair<T, U>> and(AsyncRequest<U> other) {
        return new PairAsyncRequest<>(this, other);
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public void queue(Consumer<? super T> onSuccess, Consumer<? super Throwable> onFailure) {
        futureExecutor.execute(() -> {
            try {
                final var tFuture = this.future.whenComplete((o, t) -> {
                    if (o != null && onSuccess != null) {
                        onSuccess.accept(o);
                    }
                    if (t != null && onFailure != null) {
                        onFailure.accept(t);
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
