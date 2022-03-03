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

package io.github.matyrobbrt.curseforgeapi.request.helper;

import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.annotation.ParametersAreNonnullByDefault;
import io.github.matyrobbrt.curseforgeapi.request.Requests;
import io.github.matyrobbrt.curseforgeapi.request.query.FeaturedModsQuery;
import io.github.matyrobbrt.curseforgeapi.request.query.GetFuzzyMatchesQuery;
import io.github.matyrobbrt.curseforgeapi.request.query.ModSearchQuery;
import io.github.matyrobbrt.curseforgeapi.request.query.PaginationQuery;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.Mod;
import io.github.matyrobbrt.curseforgeapi.util.CurseForgeException;

@ParametersAreNonnullByDefault
public interface IRequestHelper {

    /**********************************
     * 
     * Games
     *
     ***********************************/

    /**
     * @see Requests#getGame(int)
     */
    Object getGame(int gameId) throws CurseForgeException;

    /**
     * @see Requests#getGames()
     */
    Object getGames() throws CurseForgeException;

    /**
     * @see Requests#getGames(PaginationQuery)
     */
    Object getGames(@Nullable PaginationQuery paginationQuery) throws CurseForgeException;

    /**
     * @see Requests#getGameVersions(int)
     */
    Object getGameVersions(int gameId) throws CurseForgeException;

    /**
     * @see Requests#getGameVersionTypes(int)
     */
    Object getGameVersionTypes(int gameId) throws CurseForgeException;

    /**********************************
     * 
     * Categories
     *
     ***********************************/

    /**
     * @see Requests#getCategories(int)
     */
    Object getCategories(int gameId) throws CurseForgeException;

    /**
     * @see Requests#getCategories(int, int)
     */
    Object getCategories(int gameId, int classId) throws CurseForgeException;

    /**********************************
     * 
     * Mods
     *
     ***********************************/

    /**
     * @see Requests#getMod(int)
     */
    Object getMod(int modId) throws CurseForgeException;

    /**
     * @see Requests#getModDescription(int)
     */
    Object getModDescription(int modId) throws CurseForgeException;

    /**
     * @see Requests#searchMods(ModSearchQuery)
     */
    Object searchMods(ModSearchQuery query) throws CurseForgeException;

    /**
     * @see Requests#getFeaturedMods(FeaturedModsQuery)
     */
    Object getFeaturedMods(FeaturedModsQuery query) throws CurseForgeException;

    /**********************************
     * 
     * Files
     *
     ***********************************/

    /**
     * @see Requests#getModFile(int, int)
     */
    Object getModFile(int modId, int fileId) throws CurseForgeException;

    /**
     * @see Requests#getModFiles(int)
     */
    Object getModFiles(int modId) throws CurseForgeException;

    /**
     * @see Requests#getModFiles(int, Integer, PaginationQuery)
     */
    Object getModFiles(int modId, @Nullable Integer gameVersionTypeId, @Nullable PaginationQuery paginationQuery)
        throws CurseForgeException;

    /**
     * @see Requests#getModFiles(Mod)
     */
    Object getModFiles(Mod mod) throws CurseForgeException;

    /**
     * @see Requests#getFiles(int...)
     */
    Object getFiles(int... fileIds) throws CurseForgeException;

    /**
     * @see Requests#getModFileChangelog(int, int)
     */
    Object getModFileChangelog(int modId, int fileId) throws CurseForgeException;

    /**
     * @see Requests#getModFileDownloadURL(int, int)
     */
    Object getModFileDownloadURL(int modId, int fileId) throws CurseForgeException;

    /**********************************
     * 
     * Fingerprints
     *
     ***********************************/

    /**
     * @see Requests#getFingerprintMatches(int...)
     */
    Object getFingerprintMatches(int... fingerprints) throws CurseForgeException;

    /**
     * @see Requests#getFingerprintsFuzzyMatches(GetFuzzyMatchesQuery)
     */
    Object getFingerprintsFuzzyMatches(@Nonnull GetFuzzyMatchesQuery query) throws CurseForgeException;
}
