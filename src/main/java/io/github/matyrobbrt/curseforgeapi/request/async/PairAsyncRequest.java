package io.github.matyrobbrt.curseforgeapi.request.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.matyrobbrt.curseforgeapi.request.AsyncRequest;
import io.github.matyrobbrt.curseforgeapi.util.Pair;
import io.github.matyrobbrt.curseforgeapi.util.Utils;

public record PairAsyncRequest<F, S> (AsyncRequest<F> first, AsyncRequest<S> second)
    implements AsyncRequest<Pair<F, S>> {

    @Override
    public <U> AsyncRequest<U> map(Function<? super Pair<F, S>, ? extends U> mapper) {
        return new OfCompletableFutureAsyncRequest<>(CompletableFuture
            .supplyAsync(Utils.rethrowSupplier(() -> mapper.apply(Pair.of(first.get(), second.get())))));
    }

    @Override
    public <U> AsyncRequest<U> flatMap(Function<? super Pair<F, S>, ? extends AsyncRequest<U>> mapper) {
        return Utils.rethrowSupplier(() -> {
            final var f = first.get();
            final var s = second.get();
            return mapper.apply(Pair.of(f, s));
        }).get();
    }

    @Override
    public <U> AsyncRequest<Pair<Pair<F, S>, U>> and(AsyncRequest<U> other) {
        return new PairAsyncRequest<>(this, other);
    }

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

}
