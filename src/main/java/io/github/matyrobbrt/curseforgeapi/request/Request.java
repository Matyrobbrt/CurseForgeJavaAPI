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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.annotation.ParametersAreNonnullByDefault;

import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.Function;

@ParametersAreNonnullByDefault
public class Request<R> extends GenericRequest {
    private Type type = null;

    private final BiFunction<Gson, JsonObject, R> responseDecoder;

    public Request(String endpoint, Method method, JsonElement body, BiFunction<Gson, JsonObject, R> responseDecoder) {
        super(endpoint, method, body);
        this.responseDecoder = responseDecoder;
    }
    
    public Request(String endpoint, Method method, BiFunction<Gson, JsonObject, R> responseDecoder) {
        this(endpoint, method, null, responseDecoder);
    }
    
    public Request(String endpoint, Method method, JsonElement body, String responseObjectName, Type type) {
        super(endpoint, method, body);
        this.type = type;
        this.responseDecoder = (g, j) -> {
            final var dataElement = j.get(responseObjectName);
            if (dataElement.isJsonPrimitive()) {
                return g.fromJson(dataElement.getAsJsonPrimitive(), type);
            }
            return g.fromJson(dataElement.isJsonArray() ? dataElement.getAsJsonArray() : dataElement.getAsJsonObject(), type);
        };
    }
    
    public Request(String endpoint, Method method, String responseObjectName, Type type) {
        this(endpoint, method, null, responseObjectName, type);
    }

    public R decodeResponse(Gson gson, JsonObject response) {
        return responseDecoder.apply(gson, response);
    }

    public <T> Request<T> map(Function<R, T> mapper) {
        return new Request<>(endpoint(), method(), body(), (gson, jsonObject) -> mapper.apply(decodeResponse(gson, jsonObject)));
    }

    @Nullable
    public Type getType() {
        return type;
    }
}
