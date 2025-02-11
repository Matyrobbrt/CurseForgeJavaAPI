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

import io.github.matyrobbrt.curseforgeapi.annotation.CurseForgeSchema;
import io.github.matyrobbrt.curseforgeapi.request.Arguments;
import io.github.matyrobbrt.curseforgeapi.schemas.Category;
import io.github.matyrobbrt.curseforgeapi.schemas.game.Game;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.ModLoaderType;

import java.util.List;
import java.util.stream.Collectors;

import static io.github.matyrobbrt.curseforgeapi.util.Utils.encodeURL;

/**
 * A builders for mod search queries.
 * 
 * @author matyrobbrt
 *
 */
@CurseForgeSchema("https://docs.curseforge.com/#search-mods")
public final class ModSearchQuery extends PaginatedImpl<ModSearchQuery> {

    public static ModSearchQuery of(Game game) {
        return of(game.id());
    }

    public static ModSearchQuery of(int gameId) {
        return new ModSearchQuery(gameId);
    }

    private int gameId;
    private Integer classId;
    private Integer categoryId;
    private String gameVersion;
    private String searchFilter;
    private SortField sortField;
    private SortOrder sortOrder;
    private List<ModLoaderType> modLoaderTypes;
    private Integer gameVersionTypeId;
    private String slug;

    private ModSearchQuery(final int gameId) {
        this.gameId = gameId;
    }

    /**
     * Filter by section id (discoverable via Categories)
     * @param classId
     * @return
     */
    public ModSearchQuery classId(final int classId) {
        this.classId = classId;
        return this;
    }

    /**
     * Filter by category id
     * @param categoryId
     * @return
     */
    public ModSearchQuery categoryId(final int categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    /**
     * Filter by category
     * @param category
     * @return
     */
    public ModSearchQuery category(Category category) {
        return categoryId(category.id());
    }

    /**
     * Filter by game version string
     * @param gameVersion
     * @return
     */
    public ModSearchQuery gameVersion(final String gameVersion) {
        this.gameVersion = gameVersion;
        return this;
    }

    /**
     * Filter by free text search in the mod name and author
     * @param searchFilter
     * @return
     */
    public ModSearchQuery searchFilter(final String searchFilter) {
        this.searchFilter = searchFilter;
        return this;
    }

    /**
     * Sort the responses using the specified order.
     * @param sortOrder
     * @return
     */
    public ModSearchQuery sortOrder(final SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    /**
     * Filter by {@link SortField} enumeration
     * @param sortField
     * @return
     */
    public ModSearchQuery sortField(final SortField sortField) {
        this.sortField = sortField;
        return this;
    }

    /**
     * Filter only mods associated to a given modloader (Forge, Fabric ...). Must be
     * coupled with {@link #gameVersion(String)}.
     */
    public ModSearchQuery modLoaderType(final ModLoaderType modLoaderType) {
        return modLoaderTypes(List.of(modLoaderType));
    }

    /**
     * Filter only mods associated to given modloaders (Forge, Fabric ...). Must be
     * coupled with {@link #gameVersion(String)}.
     *
     * <p>
     * If both this and {@link #modLoaderType(ModLoaderType)} are used, this takes priority.
     */
    public ModSearchQuery modLoaderTypes(final List<ModLoaderType> modLoaderTypes) {
        this.modLoaderTypes = modLoaderTypes;
        return this;
    }

    /**
     * Filter only mods that contain files tagged with versions of the given
     * {@code gameVersionTypeId}.
     * 
     * @param  gameVersionTypeId
     * @return
     */
    public ModSearchQuery gameVersionTypeId(final int gameVersionTypeId) {
        this.gameVersionTypeId = gameVersionTypeId;
        return this;
    }

    /**
     * Filter by slug (coupled with {@link #classId(int)} will result in a unique
     * result).
     * 
     * @param  slug
     * @return
     */
    public ModSearchQuery slug(final String slug) {
        this.slug = slug;
        return this;
    }

    @Override
    public Arguments toArgs() {
        return super.toArgs()
                .put("gameId", gameId)
                .put("classId", classId)
                .put("categoryId", categoryId)
                .put("gameVersion", encodeURL(gameVersion))
                .put("searchFilter", encodeURL(searchFilter))
                .put("sortField", sortField == null ? null : sortField.ordinal() + 1)
                .put("sortOrder", sortOrder == null ? null : sortOrder.toString())
                .put("modLoaderTypes", (modLoaderTypes == null || modLoaderTypes.isEmpty()) ? null : "[" + modLoaderTypes.stream()
                        .map(type -> String.valueOf(type.ordinal())).collect(Collectors.joining(",")) + "]")
                .put("gameVersionTypeId", gameVersionTypeId)
                .put("slug", encodeURL(slug));
    }

    @Override
    public String toString() {
        return toArgs().build();
    }

    public enum SortField {
        FEATURED, POPULARITY, LAST_UPDATED, NAME, AUTHOR, TOTAL_DOWNLOADS, CATEGORY, GAME_VERSION,
        EARLY_ACCESS, FEATURED_RELEASED, RELEASED_DATE, RATING
    }

    public enum SortOrder {
        ASCENDENT {

        @Override
        public String toString() {
            return "asc";
        }
        },
        DESCENDENT {

        @Override
        public String toString() {
            return "desc";
        }
        }
    }

}
