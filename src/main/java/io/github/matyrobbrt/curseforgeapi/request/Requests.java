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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.annotation.ParametersAreNonnullByDefault;
import io.github.matyrobbrt.curseforgeapi.request.query.FeaturedModsQuery;
import io.github.matyrobbrt.curseforgeapi.request.query.GetFuzzyMatchesQuery;
import io.github.matyrobbrt.curseforgeapi.request.query.ModSearchQuery;
import io.github.matyrobbrt.curseforgeapi.request.query.PaginationQuery;
import io.github.matyrobbrt.curseforgeapi.request.query.Query;
import io.github.matyrobbrt.curseforgeapi.schemas.Category;
import io.github.matyrobbrt.curseforgeapi.schemas.PaginatedData;
import io.github.matyrobbrt.curseforgeapi.schemas.Pagination;
import io.github.matyrobbrt.curseforgeapi.schemas.file.File;
import io.github.matyrobbrt.curseforgeapi.schemas.fingerprint.FingerprintFuzzyMatch;
import io.github.matyrobbrt.curseforgeapi.schemas.fingerprint.FingerprintsMatchesResult;
import io.github.matyrobbrt.curseforgeapi.schemas.game.Game;
import io.github.matyrobbrt.curseforgeapi.schemas.game.GameVersionType;
import io.github.matyrobbrt.curseforgeapi.schemas.game.GameVersionsByType;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.FeaturedMods;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.Mod;

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
     * Get a single game. A private game is only accessible by its respective API
     * key.
     * 
     * @param  gameId the id of the game to get
     * @return
     */
    public static Request<Game> getGame(int gameId) {
        return new Request<>("/v1/games/" + gameId, Method.GET, "data", Types.GAME);
    }

    /**
     * Get all games that are available to your API key.
     * 
     * @return a request which will get all the games available to the API key.
     */
    public static Request<List<Game>> getGames() {
        return getGames(null);
    }

    /**
     * Get all games that are available to your API key.
     * 
     * @param  paginationQuery the pagination query used for the request
     * @return                 a request which will get all the games available to
     *                         the API key.
     */
    public static Request<List<Game>> getGames(@Nullable PaginationQuery paginationQuery) {
        return new Request<>(format("/v1/games", paginationQuery), Method.GET, "data", Types.GAME_LIST);
    }

    /**
     * Get all available versions for each known version type of the specified game.
     * A private game is only accessible to its respective API key.
     * 
     * @param  gameId the game id to get the versions for
     * @return        the request
     */
    public static Request<List<GameVersionsByType>> getGameVersions(int gameId) {
        return new Request<>("/v1/games/%s/versions".formatted(gameId), Method.GET, "data",
            Types.GAME_VERSIONS_BY_TYPE_LIST);
    }

    /**
     * Get all available version types of the specified game.
     * 
     * A private game is only accessible to its respective API key.
     * 
     * Currently, when creating games via the CurseForge Core Console, you are
     * limited to a single game version type. This means that this endpoint is
     * probably not useful in most cases and is relevant mostly when handling
     * existing games that have multiple game versions such as World of Warcraft and
     * Minecraft (e.g. 517 for wow_retail).
     * 
     * @param  gameId the game id to get the version types for
     * @return        the request
     */
    public static Request<List<GameVersionType>> getGameVersionTypes(int gameId) {
        return new Request<>("/v1/games/%s/version-types".formatted(gameId), Method.GET, "data",
            Types.GAME_VERSION_TYPE_LIST);
    }

    /**********************************
     * 
     * Categories
     *
     ***********************************/

    /**
     * Get all of the categories which match the given {@code gameId}.
     * 
     * @param  gameId The unique game id to search categories for
     * @return        the request
     */
    public static Request<List<Category>> getCategories(int gameId) {
        return new Request<>("/v1/categories?gameId=" + gameId, Method.GET, "data", Types.CATEGORY_LIST);
    }

    /**
     * Get all of the categories which match the given {@code gameId} and
     * {@code classId}.
     * 
     * @param  gameId  The unique game id to search categories for
     * @param  classId The unique class id to search categories for
     * @return         the request
     */
    public static Request<List<Category>> getCategories(int gameId, int classId) {
        return new Request<>("/v1/categories?gameId=%s&classId=%s".formatted(gameId, classId), Method.GET, "data",
            Types.CATEGORY_LIST);
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
     * @param  modId the mod id of the mod to request the description of (project
     *               id)
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
    
    /**
     * Searches for mods based on the specified query. <br>
     * This request provides the {@link Pagination} as well.
     * 
     * @param  query the query to search
     * @return       the request
     */
    public static Request<PaginatedData<List<Mod>>> searchModsPaginated(ModSearchQuery query) {
        return new Request<>(format("/v1/mods/search", query.toArgs()), Method.GET, (g, j) -> PaginatedData.fromJson(g, j, Types.MOD_LIST));
    }


    /**
     * Get a list of featured, popular and recently updated mods.
     * 
     * @param  query the query used for requesting the mods
     * @return       the request
     */
    public static Request<FeaturedMods> getFeaturedMods(FeaturedModsQuery query) {
        return new Request<>("/v1/mods/featured", Method.POST, query.toJson(), "data", Types.FEATURED_MODS);
    }

    /**********************************
     * 
     * Files
     *
     ***********************************/

    /**
     * Get a single file of the specified mod.
     * 
     * @param  modId  the mod id the file belongs to (project id)
     * @param  fileId the file id
     * @return        the request
     */
    public static Request<File> getModFile(int modId, int fileId) {
        return new Request<>("/v1/mods/%s/files/%s".formatted(modId, fileId), Method.GET, "data", Types.FILE);
    }

    /**
     * Get all files of the specified mod.
     * 
     * @param  mod the mod
     * @return     the request
     */
    public static Request<List<File>> getModFiles(Mod mod) {
        return getModFiles(mod.id());
    }

    /**
     * Get all files of the specified mod.
     * 
     * @param  modid the mod id the files belong to (project id)
     * @return       the request
     */
    public static Request<List<File>> getModFiles(int modid) {
        return getModFiles(modid, null, null);
    }

    /**
     * Get all files of the specified mod.
     * 
     * @param  modId             the mod id the files belong to (project id)
     * @param  gameVersionTypeId the game version to search for
     * @param  paginationQuery   the pagination query used for the request
     * @return                   the request
     */
    public static Request<List<File>> getModFiles(int modId, @Nullable Integer gameVersionTypeId,
        @Nullable PaginationQuery paginationQuery) {
        return new Request<>(
            format("/v1/mods/%s/files".formatted(modId),
                Arguments.EMPTY.put("gameVersionTypeId", gameVersionTypeId)
                    .putAll(paginationQuery == null ? null : paginationQuery.toArgs())),
            Method.GET, "data", Types.FILE_LIST);
    }

    /**
     * Get all files of the specified mod.
     *
     * @param  modId             the mod id the files belong to (project id)
     * @param  gameVersionTypeId the game version to search for
     * @param  paginationQuery   the pagination query used for the request
     * @return                   the request
     */
    public static Request<PaginatedData<List<File>>> getPaginatedModFiles(int modId, @Nullable Integer gameVersionTypeId,
        @Nullable PaginationQuery paginationQuery) {
        return new Request<>(
            format("/v1/mods/%s/files".formatted(modId),
                Arguments.EMPTY.put("gameVersionTypeId", gameVersionTypeId)
                    .putAll(paginationQuery == null ? null : paginationQuery.toArgs())),
            Method.GET, (g, j) -> PaginatedData.fromJson(g, j, Types.FILE_LIST));
    }

    /**
     * Get a list of files.
     * 
     * @param  fileIds a list of file ids to fetch
     * @return         the request
     */
    public static Request<List<File>> getFiles(int... fileIds) {
        final var body = new JsonObject();
        final var array = new JsonArray();
        for (final var id : fileIds) {
            array.add(id);
        }
        body.add("fileIds", array);
        return new Request<>("/v1/mods/files", Method.POST, body, "data", Types.FILE_LIST);
    }

    /**
     * Get the changelog of a file in HTML format.
     * 
     * @param  modId  the mod id (project id) the file belongs to
     * @param  fileId the file id
     * @return        the request
     */
    public static Request<String> getModFileChangelog(int modId, int fileId) {
        return new Request<>("/v1/mods/%s/files/%s/changelog".formatted(modId, fileId), Method.GET, "data",
            Types.STRING);
    }

    /**
     * Get a download url for a specific file.
     * 
     * @param  modId  the mod id (project id) the file belongs to
     * @param  fileId the file id
     * @return        the request
     */
    public static Request<String> getModFileDownloadURL(int modId, int fileId) {
        return new Request<>("/v1/mods/%s/files/%s/download-url".formatted(modId, fileId), Method.GET, "data",
            Types.STRING);
    }

    /**********************************
     * 
     * Fingerprints
     *
     ***********************************/

    /**
     * Get mod files that match a list of fingerprints.
     * 
     * @param  fingerprints the fingerprints to search for
     * @return              the request
     */
    public static Request<FingerprintsMatchesResult> getFingerprintMatches(long... fingerprints) {
        final var jObj = new JsonObject();
        final var array = new JsonArray();
        for (var f : fingerprints) {
            array.add(f);
        }
        jObj.add("fingerprints", array);
        return new Request<>("/v1/fingerprints", Method.POST, jObj, "data", Types.FINGERPRINTS_MATCHES);
    }

    /**
     * Get mod files that match a list of fingerprints using fuzzy matching.
     * 
     * @param  query the query to search for
     * @return       the request
     */
    public static Request<List<FingerprintFuzzyMatch>> getFingerprintsFuzzyMatches(
        @Nonnull GetFuzzyMatchesQuery query) {
        return new Request<>("/v1/fingerprints/fuzzy", Method.POST, query.toJson(),
            (gson, json) -> gson.fromJson(json.get("data").getAsJsonObject().get("fuzzyMatches").getAsJsonArray(),
                Types.FINGERPRINTS_FUZY_MATCH_LIST));
    }

    public static String format(String str, @Nullable Query query) {
        return format(str, query == null ? null : query.toArgs());
    }

    public static String format(String str, @Nullable Arguments args) {
        if (args == null) { return str; }
        return str + "?" + args.build();
    }

    //@formatter:off
    public static final class Types {
        public static final Type GAME = new TypeToken<Game>() {}.getType();
        public static final Type GAME_LIST = new TypeToken<List<Game>>() {}.getType();
        public static final Type GAME_VERSION_TYPE_LIST = new TypeToken<List<GameVersionType>>() {}.getType();
        public static final Type GAME_VERSIONS_BY_TYPE_LIST = new TypeToken<List<GameVersionsByType>>() {}.getType();
        
        public static final Type CATEGORY_LIST = new TypeToken<List<Category>>() {}.getType();
        
        public static final Type MOD = new TypeToken<Mod>() {}.getType();
        public static final Type MOD_LIST = new TypeToken<List<Mod>>() {}.getType();
        public static final Type FEATURED_MODS = new TypeToken<FeaturedMods>() {}.getType();
        
        public static final Type FILE = new TypeToken<File>() {}.getType();
        public static final Type FILE_LIST = new TypeToken<List<File>>() {}.getType();
        
        public static final Type FINGERPRINTS_MATCHES = new TypeToken<FingerprintsMatchesResult>() {}.getType();
        public static final Type FINGERPRINTS_FUZY_MATCH_LIST = new TypeToken<List<FingerprintFuzzyMatch>>() {}.getType();
        
        public static final Type STRING = new TypeToken<String>() {}.getType();
    }
}
