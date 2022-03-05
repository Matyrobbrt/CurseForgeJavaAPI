package io.github.matyrobbrt.curseforgeapi.request.uploadapi;

import java.util.List;

import com.google.gson.reflect.TypeToken;

import io.github.matyrobbrt.curseforgeapi.annotation.ParametersAreNonnullByDefault;
import io.github.matyrobbrt.curseforgeapi.request.Method;

/**
 * A utility class that contains methods for creating common requests to the
 * Upload API. Please see <a href=
 * "https://support.curseforge.com/en/support/solutions/articles/9000197321-curseforge-upload-api">the
 * CurseForge Upload API documentation</a> for more information.
 * 
 * @author matyrobbrt
 *
 */
@ParametersAreNonnullByDefault
public final class UploadApiRequests {

    /**
     * Retrieves a list of game dependencies.
     * 
     * @return the request
     */
    public static UploadApiRequest<List<GameDependency>> getGameDependencies() {
        return new UploadApiRequest<>("/api/game/dependencies", Method.GET, null,
            (gson, json) -> gson.fromJson(json, new TypeToken<List<GameDependency>>() {}.getType()));
    }
    
    /**
     * Retrieve a list of game versions
     * @return the request
     */
    public static UploadApiRequest<List<GameVersion>> getGameVersions() {
        return new UploadApiRequest<>("/api/game/versions", Method.GET, null,
            (gson, json) -> gson.fromJson(json, new TypeToken<List<GameVersion>>() {}.getType()));
    }

}
