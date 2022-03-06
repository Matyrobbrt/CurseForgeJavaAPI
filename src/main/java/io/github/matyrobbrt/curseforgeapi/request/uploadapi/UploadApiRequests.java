package io.github.matyrobbrt.curseforgeapi.request.uploadapi;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;

import com.github.mizosoft.methanol.MultipartBodyPublisher;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
     * 
     * @return the request
     */
    public static UploadApiRequest<List<GameVersion>> getGameVersions() {
        return new UploadApiRequest<>("/api/game/versions", Method.GET, null,
            (gson, json) -> gson.fromJson(json, new TypeToken<List<GameVersion>>() {}.getType()));
    }

    /**
     * Upload a file.
     * 
     * @param  projectId             the ID of the project to upload to
     * @param  uploadQuery           the data to use for publishing
     * @param  filePath              the path of the file to upload
     * @return                       the request
     * @throws FileNotFoundException if a file with the given {@code filePath} could
     *                               not be found
     */
    public static UploadApiRequest<Integer> uploadFile(int projectId, UploadQuery uploadQuery, Path filePath)
        throws FileNotFoundException {
        final var multipartBody = MultipartBodyPublisher.newBuilder()
            .textPart("metadata", uploadQuery.toJson().toString()).filePart("file", filePath).build();

        return new UploadApiRequest<>("/api/projects/%s/upload-file".formatted(projectId), Method.POST, multipartBody,
            new BiFunction<Gson, JsonElement, Integer>() {

                @Override
                public Integer apply(Gson t, JsonElement u) {
                    return u.getAsJsonObject().get("id").getAsInt();
                }
            }, "multipart/form-data");
    }

}
