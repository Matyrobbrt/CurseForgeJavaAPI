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