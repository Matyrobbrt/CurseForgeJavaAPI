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
            for (final var i : gameVersions) {
                arr.add(i);
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