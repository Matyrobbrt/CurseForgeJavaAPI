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

package io.github.matyrobbrt.curseforgeapi.request.uploadapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;
import io.github.matyrobbrt.curseforgeapi.schemas.file.FileRelationType;
import io.github.matyrobbrt.curseforgeapi.schemas.file.FileReleaseType;
import io.github.matyrobbrt.curseforgeapi.util.Pair;

public class UploadQuery {
    private UploadQuery(@Nonnull String changelog, @Nonnull FileReleaseType releaseType) {
        this.changelog = changelog;
        this.releaseType = releaseType;
    }
    
    public static UploadQuery make(@Nonnull String changelog, @Nonnull FileReleaseType releaseType) {
        return new UploadQuery(changelog, releaseType);
    }
    
    private final String changelog;
    private ChangelogType changelogType;
    private String displayName;
    private Integer parentFileID = null;
    private int[] gameVersions;
    private final FileReleaseType releaseType;
    private final List<Pair<String, FileRelationType>> relations = new ArrayList<>();

    public UploadQuery changelogType(ChangelogType changelogType) {
        this.changelogType = changelogType;
        return this;
    }
    
    public UploadQuery displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }
    
    public UploadQuery parentFileID(int parentFileID) {
        this.parentFileID = parentFileID;
        return this;
    }
    
    public UploadQuery gameVersions(int... gameVersions) {
        this.gameVersions = gameVersions;
        return this;
    }
    
    public UploadQuery addRelation(@Nonnull String slug, @Nonnull FileRelationType type) {
        this.relations.add(Pair.of(slug, type));
        return this;
    }
    
    public JsonObject toJson() {
        final var json = new JsonObject();
        json.addProperty("changelog", changelog);
        if (changelogType != null ) {
            json.addProperty("changelogType", changelogType.toString().toLowerCase(Locale.ENGLISH));
        }
        if (displayName != null) {
            json.addProperty("displayName", displayName);
        }
        if (parentFileID != null) {
            json.addProperty("parentFileID", parentFileID);
        }
        {
            final var arr = new JsonArray();
            if (gameVersions != null) {
                for (final var i : gameVersions) {
                    arr.add(i);
                }
            }
            json.add("gameVersions", arr);
        }
        json.addProperty("releaseType", releaseType.toString().toLowerCase(Locale.ENGLISH));
        {
            final var arr = new JsonArray();
            relations.forEach(pair -> pair.accept((slug, type) -> {
                final var o = new JsonObject();
                o.addProperty("slug", slug);
                o.addProperty("type", type.uploadApiName());
                arr.add(o);
            }));
            final var nO = new JsonObject();
            nO.add("projects", arr);
            json.add("relations", nO);
        }
        return json;
    }
}