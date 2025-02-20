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

package io.github.matyrobbrt.curseforgeapi.request.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import io.github.matyrobbrt.curseforgeapi.CurseForgeAPI;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.request.AsyncRequest;
import io.github.matyrobbrt.curseforgeapi.request.Request;
import io.github.matyrobbrt.curseforgeapi.request.Requests;
import io.github.matyrobbrt.curseforgeapi.request.Response;
import io.github.matyrobbrt.curseforgeapi.request.async.OfCompletableFutureAsyncRequest;
import io.github.matyrobbrt.curseforgeapi.request.query.FeaturedModsQuery;
import io.github.matyrobbrt.curseforgeapi.request.query.FileListQuery;
import io.github.matyrobbrt.curseforgeapi.request.query.GetFuzzyMatchesQuery;
import io.github.matyrobbrt.curseforgeapi.request.query.ModSearchQuery;
import io.github.matyrobbrt.curseforgeapi.request.query.PaginationQuery;
import io.github.matyrobbrt.curseforgeapi.schemas.Category;
import io.github.matyrobbrt.curseforgeapi.schemas.PaginatedData;
import io.github.matyrobbrt.curseforgeapi.schemas.file.File;
import io.github.matyrobbrt.curseforgeapi.schemas.fingerprint.FingerprintFuzzyMatch;
import io.github.matyrobbrt.curseforgeapi.schemas.fingerprint.FingerprintsMatchesResult;
import io.github.matyrobbrt.curseforgeapi.schemas.game.Game;
import io.github.matyrobbrt.curseforgeapi.schemas.game.GameVersionType;
import io.github.matyrobbrt.curseforgeapi.schemas.game.GameVersionsByType;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.FeaturedMods;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.Mod;
import io.github.matyrobbrt.curseforgeapi.util.CurseForgeException;

/**
 * A helper class for making direct requests.
 * 
 * @author matyrobbrt
 *
 */
public class AsyncRequestHelper implements IRequestHelper {

    private final CurseForgeAPI api;

    public AsyncRequestHelper(CurseForgeAPI api) {
        this.api = api;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<File>> getModFile(int modId, int fileId) throws CurseForgeException {
        return api.makeAsyncRequest(Requests.getModFile(modId, fileId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<List<File>>> getModFiles(int modId) throws CurseForgeException {
        return api.makeAsyncRequest(Requests.getModFiles(modId));
    }

    public AsyncRequest<Response<Iterator<AsyncRequest<File>>>> listModFiles(int modId) throws CurseForgeException {
        return listModFiles(modId, null);
    }

    @Override
    public AsyncRequest<Response<Iterator<AsyncRequest<File>>>> listModFiles(int modId, @Nullable FileListQuery query) throws CurseForgeException {
        return paginated(pg -> Requests.getPaginatedModFiles(modId, (query == null ? FileListQuery.of() : query).paginated(pg)), Function.identity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<List<File>>> getModFiles(int modId, @Nullable FileListQuery query) throws CurseForgeException {
        return api.makeAsyncRequest(Requests.getModFiles(modId, query));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<List<Category>>> getCategories(int gameId) throws CurseForgeException {
        return api.makeAsyncRequest(Requests.getCategories(gameId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<List<Category>>> getCategories(int gameId, int classId) throws CurseForgeException {
        return api.makeAsyncRequest(Requests.getCategories(gameId, classId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<Mod>> getMod(int modId) throws CurseForgeException {
        return api.makeAsyncRequest(Requests.getMod(modId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<List<Mod>>> searchMods(ModSearchQuery query) throws CurseForgeException {
        return api.makeAsyncRequest(Requests.searchMods(query));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<PaginatedData<List<Mod>>>> searchModsPaginated(ModSearchQuery query) throws CurseForgeException {
        return api.makeAsyncRequest(Requests.searchModsPaginated(query));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<Game>> getGame(int gameId) throws CurseForgeException {
        return mr(Requests.getGame(gameId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<List<Game>>> getGames() throws CurseForgeException {
        return mr(Requests.getGames());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<List<Game>>> getGames(PaginationQuery paginationQuery) throws CurseForgeException {
        return mr(Requests.getGames(paginationQuery));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<List<GameVersionsByType>>>  getGameVersions(int gameId) throws CurseForgeException {
        return mr(Requests.getGameVersions(gameId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<List<GameVersionType>>>  getGameVersionTypes(int gameId) throws CurseForgeException {
        return mr(Requests.getGameVersionTypes(gameId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<String>> getModDescription(int modId) throws CurseForgeException {
        return mr(Requests.getModDescription(modId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<FeaturedMods>> getFeaturedMods(FeaturedModsQuery query) throws CurseForgeException {
        return mr(Requests.getFeaturedMods(query));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<List<File>>> getModFiles(Mod mod) throws CurseForgeException {
        return mr(Requests.getModFiles(mod));
    }

    @Override
    public AsyncRequest<Response<Iterator<AsyncRequest<File>>>> listModFiles(Mod mod) throws CurseForgeException {
        return paginated(query -> Requests.getPaginatedModFiles(mod.id(), FileListQuery.of().paginated(query)), Function.identity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<List<File>>> getFiles(int... fileIds) throws CurseForgeException {
        return mr(Requests.getFiles(fileIds));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<String>> getModFileChangelog(int modId, int fileId) throws CurseForgeException {
        return mr(Requests.getModFileChangelog(modId, fileId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<String>> getModFileDownloadURL(int modId, int fileId) throws CurseForgeException {
        return mr(Requests.getModFileDownloadURL(modId, fileId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<FingerprintsMatchesResult>> getFingerprintMatches(long... fingerprints) throws CurseForgeException {
        return mr(Requests.getFingerprintMatches(fingerprints));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<List<FingerprintFuzzyMatch>>> getFingerprintsFuzzyMatches(GetFuzzyMatchesQuery query) throws CurseForgeException {
        return mr(Requests.getFingerprintsFuzzyMatches(query));
    }

    @Override
    public <T, R> AsyncRequest<Response<Iterator<AsyncRequest<R>>>> paginated(Function<PaginationQuery, Request<PaginatedData<T>>> requester, Function<T, List<R>> collector) throws CurseForgeException {
        return mr(requester.apply(PaginationQuery.of(0, 50))).map(baseResponse -> {
            if (baseResponse.isEmpty()) {
                return Response.empty(baseResponse.getStatusCode());
            }
            final var paginationData = baseResponse.get().pagination();

            return Response.of(new Iterator<>() {
                private final AtomicInteger currentIndex = new AtomicInteger(-1);
                private final AtomicInteger size = new AtomicInteger();
                private final List<OfCompletableFutureAsyncRequest<R>> requests = Collections.synchronizedList(new ArrayList<>());
                private final AtomicReference<List<R>> currentResponse = new AtomicReference<>();
                private final AtomicInteger currentListIndex = new AtomicInteger(-1);

                {
                    size.set(paginationData.totalCount());
                    currentResponse.set(collector.apply(baseResponse.get().data()));
                }

                @Override
                public boolean hasNext() {
                    return currentIndex.get() + 1 < size.get();
                }

                @Override
                public AsyncRequest<R> next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException("No more elements left");
                    }

                    if (currentListIndex.get() + 1 >= 50) {
                        requery();
                    } else if (currentResponse.get() != null) {
                        currentIndex.getAndIncrement();
                        final var req = new OfCompletableFutureAsyncRequest<R>(CompletableFuture.completedFuture(currentResponse.get().get(currentListIndex.incrementAndGet())));
                        requests.add(req);
                        return req;
                    }

                    currentIndex.getAndIncrement();
                    currentListIndex.incrementAndGet();
                    final var req = new OfCompletableFutureAsyncRequest<R>(new CompletableFuture<>());
                    requests.add(req);
                    return req;
                }

                private synchronized void requery() {
                    try {
                        mr(requester.apply(PaginationQuery.of(currentIndex.get() + 1, 50)))
                                .queue(res -> {
                                    if (res.isEmpty()) {
                                        currentResponse.set(List.of());
                                    } else {
                                        var data = res.orElseThrow();
                                        final var values = collector.apply(data.data());
                                        for (int i = data.pagination().index(); i < data.pagination().index() + data.pagination().resultCount(); i++) {
                                            if (i < requests.size()) {
                                                requests.get(i).future().complete(values.get(i - data.pagination().index()));
                                            }
                                        }
                                        currentResponse.set(values);
                                    }
                                });
                        currentResponse.set(null);
                        currentListIndex.set(-1);
                    } catch (CurseForgeException exception) {
                        throw new RuntimeException(exception);
                    }
                }
            }, baseResponse.getStatusCode());
        });
    }

    private <T> AsyncRequest<Response<T>> mr(Request<T> req) throws CurseForgeException {
        return api.makeAsyncRequest(req);
    }
}
