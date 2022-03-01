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

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import io.github.matyrobbrt.curseforgeapi.annotation.AcceptsArgs;
import io.github.matyrobbrt.curseforgeapi.annotation.Arg;
import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.annotation.ParametersAreNonnullByDefault;
import io.github.matyrobbrt.curseforgeapi.request.objects.ModSearchQuery;
import io.github.matyrobbrt.curseforgeapi.schemas.Category;
import io.github.matyrobbrt.curseforgeapi.schemas.Game;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.Mod;

import static io.github.matyrobbrt.curseforgeapi.annotation.Arg.Type.*;

/**
 * A utility class that contains methods for creating common requests. Please
 * see <a href="https://docs.curseforge.com/">the CurseForge documentation</a>
 * for more information.
 * 
 * @author matyrobbrt
 *
 */
@ParametersAreNonnullByDefault
public final class Requests {

    /**********************************
     * 
     * Games
     *
     ***********************************/

    /**
     * Get all games that are available to your API key.
     * 
     * @param  args the arguments for the request. Can be {@link Arguments#EMPTY} or
     *              {@code null}.
     * @return      a request which will get all the games available to the API key.
     */
    @AcceptsArgs({
        @Arg(name = "index", description = "A zero based index of the first item to include in the response.", type = INTEGER),
        @Arg(name = "pageSize", description = "The number of items to include in the response", type = INTEGER)
    })
    public static Request<List<Game>> getGames(@Nullable Arguments args) {
        return new Request<>(format("/v1/games", args), Method.GET, "data", Types.GAME_LIST);
    }

    /**********************************
     * 
     * Categories
     *
     ***********************************/

    /**
     * Get all of the categories which match the given arguments,
     * 
     * @param  args the arguments to use for the query
     * @return      the request
     */
    @AcceptsArgs({
        @Arg(name = "gameId", description = "The unique game id to search categories for", type = INTEGER, required = true),
        @Arg(name = "classId", description = "The unique class id to search categories for", type = INTEGER)
    })
    public static Request<List<Category>> getCategories(@Nonnull Arguments args) {
        return new Request<>(format("/v1/categories", args), Method.GET, "data", Types.CATEGORY_LIST);
    }

    /**********************************
     * 
     * Mods
     *
     ***********************************/

    /**
     * Get the mod with the specified ID
     * 
     * @param  modId the mod id to search for (the project ID)
     * @return       the request
     */
    public static Request<Mod> getMod(int modId) {
        return new Request<>("/v1/mods/" + modId, Method.GET, "data", Types.MOD);
    }

    /**
     * Get the description of the mod with the specified ID in the HTML format.
     * 
     * @param  modId the mod id of the mod to request the description of (the
     *               project ID)
     * @return       the request
     */
    public static Request<String> getModDescription(int modId) {
        return new Request<>("/v1/mods/%s/description".formatted(modId), Method.GET, "data", Types.STRING);
    }

    /**
     * Searches for mods based on the specified query.
     * 
     * @param  query the query to search
     * @return       the request
     */
    public static Request<List<Mod>> searchMods(ModSearchQuery query) {
        return new Request<>(format("/v1/mods/search", query.toArgs()), Method.GET, "data", Types.MOD_LIST);
    }

    public static String format(String str, @Nullable Arguments args) {
        if (args == null) { return str; }
        return str + "?" + args.build();
    }

    //@formatter:off
    public static final class Types {
        public static final Type GAME_LIST = new TypeToken<List<Game>>() {}.getType();
        
        public static final Type CATEGORY_LIST = new TypeToken<List<Category>>() {}.getType();
        
        public static final Type MOD = new TypeToken<Mod>() {}.getType();
        public static final Type MOD_LIST = new TypeToken<List<Mod>>() {}.getType();
        
        public static final Type STRING = new TypeToken<String>() {}.getType();
    }
}
