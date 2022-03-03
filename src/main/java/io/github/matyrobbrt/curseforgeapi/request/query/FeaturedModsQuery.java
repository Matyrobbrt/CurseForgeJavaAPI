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

package io.github.matyrobbrt.curseforgeapi.request.query;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.matyrobbrt.curseforgeapi.annotation.CurseForgeSchema;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;

@CurseForgeSchema("https://docs.curseforge.com/?php#tocS_GetFeaturedModsRequestBody")
public class FeaturedModsQuery {
    
    public static FeaturedModsQuery of(int gameId, int... excludedModIds) {
        return new FeaturedModsQuery(gameId).excludeMods(excludedModIds);
    }
    
    private FeaturedModsQuery(int gameId) {
        this.gameId = gameId;
    }

    private final int gameId;
    private final List<Integer> excludedModIds = new ArrayList<>();
    @Nullable
    private Integer gameVersionTypeId;
    
    public FeaturedModsQuery excludeMod(int modId) {
        excludedModIds.add(modId);
        return this;
    }
    
    public FeaturedModsQuery excludeMods(int... modIds) {
        for (final var id : modIds) {
            excludedModIds.add(id);
        }
        return this;
    }
    
    public FeaturedModsQuery gameVersionTypeId(int gameVersionTypeId) {
        this.gameVersionTypeId = gameVersionTypeId;
        return this;
    }
    
    public JsonObject toJson() {
        final var json = new JsonObject();
        json.addProperty("gameId", gameId);
        final var excArray = new JsonArray();
        excludedModIds.forEach(excArray::add);
        json.add("excludedModIds", excArray);
        if (gameVersionTypeId != null) {
            json.addProperty("gameVersionTypeId", gameVersionTypeId);
        }
        return json;
    }
}