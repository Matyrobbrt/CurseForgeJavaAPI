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
import io.github.matyrobbrt.curseforgeapi.annotation.ParametersAreNonnullByDefault;
import io.github.matyrobbrt.curseforgeapi.schemas.game.Game;
import io.github.matyrobbrt.curseforgeapi.util.Pair;

@ParametersAreNonnullByDefault
@CurseForgeSchema("https://docs.curseforge.com/#tocS_GetFuzzyMatchesRequestBody")
public class GetFuzzyMatchesQuery {
    
    public static GetFuzzyMatchesQuery forGame(Game game) {
        return forGame(game.id());
    }
    
    public static GetFuzzyMatchesQuery forGame(int gameId) {
        return new GetFuzzyMatchesQuery(gameId);
    }

    private final int gameId;
    private final List<Pair<String, int[]>> fingerprints = new ArrayList<>();
    
    private GetFuzzyMatchesQuery(int gameId) {
        this.gameId = gameId;
    }
    
    public GetFuzzyMatchesQuery addFingerprint(String foldername, int... fingerprints) {
        this.fingerprints.add(Pair.of(foldername, fingerprints));
        return this;
    }
    
    public JsonObject toJson() {
        final var jObj = new JsonObject();
        jObj.addProperty("gameId", gameId);
        final var fgArray = new JsonArray();
        fingerprints.forEach(pair -> {
            final var obj = new JsonObject();
            obj.addProperty("foldername", pair.first());
            final var ar = new JsonArray();
            for (var i : pair.second()) {
                ar.add(i);
            }
            obj.add("fingerprints", ar);
            fgArray.add(obj);
        });
        return jObj;
    }
}