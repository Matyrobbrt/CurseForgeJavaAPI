package io.github.matyrobbrt.curseforgeapi.request.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;
import io.github.matyrobbrt.curseforgeapi.request.AsyncRequest;
import io.github.matyrobbrt.curseforgeapi.util.Pair;
import io.github.matyrobbrt.curseforgeapi.util.Utils;

public record OfHttpResponseAsyncRequest<T> (@Nonnull CompletableFuture<T> future) implements AsyncRequest<T> {

    @Override
    public <U> AsyncRequest<U> map(Function<? super T, ? extends U> mapper) {
        return new OfHttpResponseAsyncRequest<>(
            future.thenCompose(o -> CompletableFuture.completedFuture(mapper.apply(o))));
    }

    @Override
    public <U> AsyncRequest<U> flatMap(Function<? super T, ? extends AsyncRequest<U>> mapper) {
        return new OfHttpResponseAsyncRequest<>(
            future.thenCompose(Utils.rethrowFunction(o -> CompletableFuture.completedFuture(mapper.apply(o).get()))));
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
        future.whenComplete((o, t) -> {
            if (onSuccess != null && o != null) {
                onSuccess.accept(o);
            }
            if (t != null && onFailure != null) {
                onFailure.accept(t);
            }
        });
    }

}
