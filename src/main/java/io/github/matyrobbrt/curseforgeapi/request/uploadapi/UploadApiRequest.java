package io.github.matyrobbrt.curseforgeapi.request.uploadapi;

import java.net.http.HttpRequest.BodyPublisher;
import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.annotation.ParametersAreNonnullByDefault;
import io.github.matyrobbrt.curseforgeapi.request.Method;

@ParametersAreNonnullByDefault
public record UploadApiRequest<T> (String endpoint, Method method, @Nullable BodyPublisher bodyPublisher,
    BiFunction<Gson, JsonElement, T> responseDecoder) {

}
