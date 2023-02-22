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

package io.github.matyrobbrt.curseforgeapi.schemas.mod;

import java.time.Instant;
import java.util.List;

import io.github.matyrobbrt.curseforgeapi.annotation.CurseForgeSchema;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.schemas.Category;
import io.github.matyrobbrt.curseforgeapi.schemas.file.File;
import io.github.matyrobbrt.curseforgeapi.schemas.file.FileIndex;

/**
 * @param dateCreated  the creation date of the mod
 * @param dateReleased the release date of the mod. Note: this seems to be the date when the latest file was released
 * @param dateModified the last time the mod was modified
 */
@CurseForgeSchema("https://docs.curseforge.com/#schemamod")
public record Mod(int id, int gameId, String name, String slug, ModLinks links, String summary, ModStatus status,
    double downloadCount, boolean isFeatured, int primaryCategoryId, List<Category> categories,
    @Nullable Integer classId, List<ModAuthor> authors, ModAsset logo, List<ModAsset> screenshots, int mainFileId,
    List<File> latestFiles, List<FileIndex> latestFilesIndexes,
    String dateCreated, String dateModified, String dateReleased,
    @Nullable Boolean allowModDistribution, int gamePopularityRank, boolean isAvailable, @Nullable Integer thumbsUpCount) {

    public Instant getDateModifiedAsInstant() {
        return Instant.parse(dateModified);
    }

    public Instant getDateReleasedAsInstant() {
        return Instant.parse(dateReleased);
    }

    public Instant getDateCreatedAsInstant() {
        return Instant.parse(dateCreated);
    }

}
