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

import java.util.List;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public record WrappedJson(JsonObject json) {

    public int getInt(String name) {
        return json.get(name).getAsInt();
    }
    
    public Integer getIntNullable(String name) {
        if (json.get(name) == null) {
            return null;
        }
        return json.get(name).getAsInt();
    }

    public String getString(String name) {
        return json.get(name).isJsonPrimitive()
            ? json.get(name).getAsString()
            : null;
    }

    public boolean getBoolean(String name) {
        if (json.get(name) == null) {
            return false;
        }
        return json.get(name).getAsBoolean();
    }

    public <T> List<T> getList(String name, Function<? super JsonElement, T> deserializer) {
        final var array = json.get(name).getAsJsonArray();
        return Utils.jsonArrayToList(array).stream().map(deserializer).toList();
    }

    public <T> List<T> getListJsonObject(String name, Function<WrappedJson, T> deserializer) {
        return getList(name, e -> {
            if (e instanceof JsonObject jObj) { return deserializer.apply(new WrappedJson(jObj)); }
            throw new IllegalArgumentException();
        });
    }

    public WrappedJson getJsonObject(String name) {
        return new WrappedJson(json.get(name).getAsJsonObject());
    }

}
