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

package io.github.matyrobbrt.curseforgeapi.schemas;

import java.time.Instant;

import io.github.matyrobbrt.curseforgeapi.util.WrappedJson;

public record Game(int id, String name, String slug, String dateModified, Assets asstes, Status status,
    ApiStatus apiStatus) {

    public Game(WrappedJson j) {
        this(j.getInt("id"), j.getString("name"), j.getString("slug"), j.getString("dateModified"),
            new Assets(j.getJsonObject("assets")), Status.byId(j.getInt("status")), ApiStatus.byId(j.getInt("apiStatus")));
    }
    
    public Instant dateModifiedAsInstant() {
        return Instant.parse(dateModified);
    }
    
    public record Assets(String iconUrl, String tileUrl, String coverUrl) {

        public Assets(WrappedJson j) {
            this(j.getString("iconUrl"), j.getString("tileUrl"), j.getString("coverUrl"));
        }

    }

}
