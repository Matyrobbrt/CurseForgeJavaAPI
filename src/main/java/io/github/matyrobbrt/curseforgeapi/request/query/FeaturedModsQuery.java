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