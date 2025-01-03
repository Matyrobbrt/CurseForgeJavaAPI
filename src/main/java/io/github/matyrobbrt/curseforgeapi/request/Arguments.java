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

import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.util.MultiMap;

/**
 * A list of keys and values for specifying arguments when send a request to
 * CurseForge.
 * 
 * @author matyrobbrt
 *
 */
public class Arguments {

    public static final Arguments EMPTY = new Arguments(new MultiMap<>()).immutable();

    public static Arguments of(String key, Object value) {
        return new Arguments(new MultiMap<>()).put(key, value);
    }

    final MultiMap<String, String> args;

    Arguments(MultiMap<String, String> args) {
        this.args = args;
    }

    public Arguments put(String key, @Nullable Object value) {
        if (value != null) {
            args.put(key, value.toString());
        }
        return this;
    }
    
    public Arguments copy() {
        return new Arguments(new MultiMap<>(args));
    }
    
    public Arguments putAll(@Nullable Arguments other) {
        if (other == null) {
            return this;
        }
        args.putAll(other.args);
        return this;
    }

    public String build() {
        return String.join("&", args.entrySet().stream().map(e -> "%s=%s".formatted(e.getKey(), e.getValue())).toArray(String[]::new));
    }

    @Override
    public String toString() {
        return build();
    }
    
    public Arguments immutable() {
        return new Immutable(args);
    }
    
    private static final class Immutable extends Arguments {

        Immutable(MultiMap<String, String> args) {
            super(new MultiMap<>(args));
        }
        
        @Override
        public Arguments put(String key, Object value) {
            final var newArgs = new MultiMap<>(args);
            if (value == null) {
                return new Arguments(newArgs);
            }
            newArgs.put(key, value.toString());
            return new Arguments(newArgs);
        }
        
        @Override
        public Arguments putAll(@Nullable Arguments other) {
            final var newArgs = new MultiMap<>(args);
            if (other == null) {
                return new Arguments(newArgs);
            }
            newArgs.putAll(other.args);
            return new Arguments(newArgs);
        }
        
    }
}
