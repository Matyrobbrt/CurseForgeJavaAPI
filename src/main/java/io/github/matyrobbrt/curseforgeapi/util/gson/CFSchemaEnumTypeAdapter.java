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

package io.github.matyrobbrt.curseforgeapi.util.gson;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class CFSchemaEnumTypeAdapter<E extends Enum<E>> extends TypeAdapter<E> {

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> CFSchemaEnumTypeAdapter<E> constructUnsafe(Class<?> clazz) {
        return new CFSchemaEnumTypeAdapter<>((Class<E>) clazz);
    }

    private final Class<E> clazz;

    public CFSchemaEnumTypeAdapter(Class<E> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void write(JsonWriter out, E value) throws IOException {
        out.value(value.ordinal() + 1);
    }

    @Override
    public E read(JsonReader in) throws IOException {
        return clazz.getEnumConstants()[in.nextInt() - 1];
    }

}
