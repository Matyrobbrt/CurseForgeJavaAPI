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

import java.util.List;

import io.github.matyrobbrt.curseforgeapi.CurseForgeAPI;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.request.AsyncRequest;
import io.github.matyrobbrt.curseforgeapi.request.Requests;
import io.github.matyrobbrt.curseforgeapi.request.Response;
import io.github.matyrobbrt.curseforgeapi.request.query.ModSearchQuery;
import io.github.matyrobbrt.curseforgeapi.request.query.PaginationQuery;
import io.github.matyrobbrt.curseforgeapi.schemas.Category;
import io.github.matyrobbrt.curseforgeapi.schemas.file.File;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public AsyncRequest<Response<List<File>>> getModFiles(int modId, @Nullable Integer gameVersionTypeId, @Nullable PaginationQuery paginationQuery)
        throws CurseForgeException {
        return api.makeAsyncRequest(Requests.getModFiles(modId, gameVersionTypeId, paginationQuery));
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

}
