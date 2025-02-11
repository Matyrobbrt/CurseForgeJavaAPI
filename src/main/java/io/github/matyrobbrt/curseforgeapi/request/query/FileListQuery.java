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
import io.github.matyrobbrt.curseforgeapi.schemas.mod.ModLoaderType;

/**
 * A builders for file list search queries.
 *
 * @author matyrobbrt
 */
@CurseForgeSchema("https://docs.curseforge.com/rest-api/#get-mod-files")
public final class FileListQuery extends PaginatedImpl<FileListQuery> {

    public static FileListQuery of() {
        return new FileListQuery();
    }

    private String gameVersion;
    private Integer gameVersionTypeId;
    private ModLoaderType modLoaderType;

    private FileListQuery() {
    }

    /**
     * Filter by game version string
     *
     * @param gameVersion
     * @return
     */
    public FileListQuery gameVersion(final String gameVersion) {
        this.gameVersion = gameVersion;
        return this;
    }

    /**
     * Filter only files associated to a given modloader (Forge, Fabric ...). Must be
     * coupled with {@link #gameVersion(String)}.
     */
    public FileListQuery modLoaderType(final ModLoaderType modLoaderType) {
        this.modLoaderType = modLoaderType;
        return this;
    }

    /**
     * Filter only files tagged with versions of the given
     * {@code gameVersionTypeId}.
     *
     * @param gameVersionTypeId game version id
     */
    public FileListQuery gameVersionTypeId(final int gameVersionTypeId) {
        this.gameVersionTypeId = gameVersionTypeId;
        return this;
    }

    @Override
    public Arguments toArgs() {
        return super.toArgs()
                .put("gameVersion", gameVersion)
                .put("modLoaderType", modLoaderType != null ? modLoaderType.ordinal() : null)
                .put("gameVersionTypeId", gameVersionTypeId);
    }

    @Override
    public String toString() {
        return toArgs().build();
    }
}
