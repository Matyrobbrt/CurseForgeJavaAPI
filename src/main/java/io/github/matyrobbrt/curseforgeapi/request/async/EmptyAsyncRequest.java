package io.github.matyrobbrt.curseforgeapi.request.async;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.matyrobbrt.curseforgeapi.request.AsyncRequest;
import io.github.matyrobbrt.curseforgeapi.util.Pair;

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
    public <U> AsyncRequest<Pair<T, U>> and(AsyncRequest<U> other) {
        return new PairAsyncRequest<>(this, other);
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        throw new ExecutionException(new AsyncRequest.EmptyRequestException());
    }

    @Override
    public void queue(Consumer<? super T> onSuccess, Consumer<? super Throwable> onFailure) {
        onFailure.accept(new AsyncRequest.EmptyRequestException());
    }

}
