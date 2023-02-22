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

package io.github.matyrobbrt.curseforgeapi.schemas.file;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import io.github.matyrobbrt.curseforgeapi.annotation.CurseForgeSchema;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.schemas.SortableGameVersion;

@CurseForgeSchema("https://docs.curseforge.com/#tocS_File")
public record File(int id, int gameId, int modId, boolean isAvailable, String displayName, String fileName,
    FileReleaseType releaseType, FileStatus fileStatus, List<FileHash> hashes, String fileDate, int fileLength,
    int downloadCount, String downloadUrl, List<String> gameVersions, List<SortableGameVersion> sortableGameVersions,
    List<FileDependency> dependencies, @Nullable Boolean exposeAsAlternative, @Nullable Integer parentProjectFileId,
    @Nullable Integer alternateFileId, @Nullable Boolean isServerPack, @Nullable Integer serverPackFileId,
    int fileFingerprint, List<FileModule> modules) {

    /**
     * Attempts to download the file to the specified {@code path}, creating any
     * directories to it, if they do not exist.
     * 
     * @param  path        the path to save the file to
     * @throws IOException if an exception occurs while downloading
     */
    public void download(Path path) throws IOException {
        final var url = new URL(downloadUrl());
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        if (path.toFile().exists()) { throw new FileAlreadyExistsException(path.toString()); }
        try (final var readChannel = Channels.newChannel(url.openStream());
            final var fos = new FileOutputStream(path.toFile())) {
            final var writeChannel = fos.getChannel();
            writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);
        }
    }

    public Instant getFileDateAsInstant() {
        return Instant.parse(fileDate);
    }
}
