package io.github.matyrobbrt.curseforgeapi.request.async;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.matyrobbrt.curseforgeapi.request.AsyncRequest;
import io.github.matyrobbrt.curseforgeapi.util.Pair;

public record OfValueAsyncRequest<T>(T value) implements AsyncRequest<T> {

    @Override
    public <U> AsyncRequest<U> map(Function<? super T, ? extends U> mapper) {
        return new OfValueAsyncRequest<>(mapper.apply(value));
    }

    @Override
    public <U> AsyncRequest<U> flatMap(Function<? super T, ? extends AsyncRequest<U>> mapper) {
        return mapper.apply(value);
    }

    @Override
    public <U> AsyncRequest<Pair<T, U>> and(AsyncRequest<U> other) {
        return new PairAsyncRequest<>(this, other);
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return value;
    }

    @Override
    public void queue(Consumer<? super T> onSuccess, Consumer<? super Throwable> onFailure) {
        if (value != null) {
            if (onSuccess != null) {
                onSuccess.accept(value);
            }
        } else {
            if (onFailure != null) {
                onFailure.accept(new NullPointerException());
            }
        }
    }

}
