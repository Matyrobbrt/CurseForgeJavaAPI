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
