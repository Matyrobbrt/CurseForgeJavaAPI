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

package io.github.matyrobbrt.curseforgeapi;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;
import io.github.matyrobbrt.curseforgeapi.annotation.ParametersAreNonnullByDefault;
import io.github.matyrobbrt.curseforgeapi.request.AsyncRequest;
import io.github.matyrobbrt.curseforgeapi.request.GenericRequest;
import io.github.matyrobbrt.curseforgeapi.request.Request;
import io.github.matyrobbrt.curseforgeapi.request.Requests;
import io.github.matyrobbrt.curseforgeapi.request.Response;
import io.github.matyrobbrt.curseforgeapi.request.helper.AsyncRequestHelper;
import io.github.matyrobbrt.curseforgeapi.request.helper.RequestHelper;
import io.github.matyrobbrt.curseforgeapi.schemas.ApiStatus;
import io.github.matyrobbrt.curseforgeapi.schemas.HashAlgo;
import io.github.matyrobbrt.curseforgeapi.schemas.Status;
import io.github.matyrobbrt.curseforgeapi.schemas.file.FileRelationType;
import io.github.matyrobbrt.curseforgeapi.schemas.file.FileReleaseType;
import io.github.matyrobbrt.curseforgeapi.schemas.file.FileStatus;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.ModLoaderType;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.ModStatus;
import io.github.matyrobbrt.curseforgeapi.util.CurseForgeException;
import io.github.matyrobbrt.curseforgeapi.util.Utils;
import io.github.matyrobbrt.curseforgeapi.util.gson.CFSchemaEnumTypeAdapter;
import io.github.matyrobbrt.curseforgeapi.util.gson.RecordTypeAdapterFactory;

/**
 * The main class used for communicating with
 * <a href="https://docs.curseforge.com/">the CurseForge API</a> through HTTP
 * requests.
 * 
 * @author matyrobbrt
 *
 */
@ParametersAreNonnullByDefault
public class CurseForgeAPI {

    public static final String REQUEST_TARGET = "https://api.curseforge.com";

    private final String apiKey;
    private final HttpClient httpClient;
    private final Gson gson;
    private final Logger logger;
    
    private final RequestHelper helper = new RequestHelper(this);
    private final AsyncRequestHelper asyncHelper = new AsyncRequestHelper(this);

    //@formatter:off
    public CurseForgeAPI(final String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        
        final var gsonBuilder = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapterFactory(new RecordTypeAdapterFactory());
        
        final List<Class<? extends Enum<?>>> cfSchemaEnums = List.of(
            ApiStatus.class, FileRelationType.class, FileReleaseType.class, FileStatus.class,
            ModLoaderType.class, HashAlgo.class, Status.class, ModStatus.class
        );
        cfSchemaEnums.forEach(e -> gsonBuilder.registerTypeAdapter(e, CFSchemaEnumTypeAdapter.constructUnsafe(e)));
        
        this.gson = gsonBuilder.create();
        this.logger = LoggerFactory.getLogger(getClass());
    }
    //@formatter:on

    public CurseForgeAPI(final String apiKey, final HttpClient httpClient, final Gson gson, final Logger logger) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.gson = gson;
        this.logger = logger;
    }
    
    public Gson getGson() {
        return gson;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public HttpClient getHttpClient() {
        return httpClient;
    }
    
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @return the helper used for direct requests, without going through {@link Requests} first
     */
    public RequestHelper getHelper() {
        return helper;
    }
    
    /**
     * @return the helper used for direct async requests, without going through {@link Requests} first
     */
    public AsyncRequestHelper getAsyncHelper() {
        return asyncHelper;
    }
    
    /**
     * Sends a <b>blocking</b> request to the API
     * 
     * @param  <R>                 the type of the request result
     * @param  request             the request to send
     * @return                     the response of the request, deserialized from a
     *                             {@link JsonObject} using
     *                             {@link Request#decodeResponse(WrappedJson)}, if
     *                             present
     * @throws CurseForgeException
     */
    public <R> Response<R> makeRequest(Request<? extends R> request) throws CurseForgeException {
        return makeGenericRequest(request).map(j -> request.decodeResponse(getGson(), j));
    }

    /**
     * Sends a generic <b>blocking</b> request to the API.
     * 
     * @param  genericRequest      the request to send
     * @return                     the response of the request
     * @throws CurseForgeException
     */
    @Nonnull
    public Response<JsonObject> makeGenericRequest(GenericRequest genericRequest) throws CurseForgeException {
        int statusCode = 0;
        try {
            final URL target = new URL(REQUEST_TARGET + genericRequest.endpoint());
            final var httpRequest = Utils.<HttpRequest.Builder>makeWithSupplier(() -> {
                var r = HttpRequest.newBuilder(URI.create(target.toString())).header("Accept", "application/json")
                    .header("x-api-key", apiKey);
                r = switch (genericRequest.method()) {
                case GET -> r.GET();
                case POST -> r.POST(BodyPublishers.ofString(genericRequest.body().toString()));
                case PUT -> r.PUT(BodyPublishers.ofString(genericRequest.body().toString()));
                };
                return r;
            }).build();
            final var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            return Response.ofNullableAndStatusCode(gson.fromJson(response.body(), JsonObject.class), statusCode);
        } catch (InterruptedException ine) {
            logger.error(
                "InterruptedException while awaiting CurseForge response, which returned with the status code: ", ine);
            Thread.currentThread().interrupt();
            return Response.empty(statusCode);
        } catch (Exception e) {
            logger.info("Status code was {}", statusCode);
            throw new CurseForgeException(e);
        }
    }

    // Async

    /**
     * Sends an <b>async</b> request to the API
     * 
     * @param  <R>                 the type of the request result
     * @param  request             the request to send
     * @return                     the async request, which will be sent when
     *                             {@link AsyncRequest#queue} is called. The result
     *                             is deserialized from a {@link JsonObject} using
     *                             {@link Request#decodeResponse(WrappedJson)}, if
     *                             present
     * @throws CurseForgeException
     */
    public <R> AsyncRequest<Response<R>> makeAsyncRequest(Request<? extends R> request) throws CurseForgeException {
        return makeAsyncGenericRequest(request).map(r -> r.map(j -> request.decodeResponse(getGson(), j)));
    }

    /**
     * Sends an <b>async</b> generic request to the API.
     * 
     * @param  genericRequest      the request to send
     * @return                     the async request, which will be sent when
     *                             {@link AsyncRequest#queue} is called.
     * @throws CurseForgeException
     */
    @Nonnull
    public AsyncRequest<Response<JsonObject>> makeAsyncGenericRequest(GenericRequest genericRequest)
        throws CurseForgeException {
        try {
            final URL target = new URL(REQUEST_TARGET + genericRequest.endpoint());
            final var httpRequest = Utils.<HttpRequest.Builder>makeWithSupplier(() -> {
                var r = HttpRequest.newBuilder(URI.create(target.toString())).header("Accept", "application/json")
                    .header("x-api-key", apiKey);
                r = switch (genericRequest.method()) {
                case GET -> r.GET();
                case POST -> r.POST(BodyPublishers.ofString(genericRequest.body().toString()));
                case PUT -> r.PUT(BodyPublishers.ofString(genericRequest.body().toString()));
                };
                return r;
            }).build();
            return new AsyncRequest<>(() -> httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> Response.ofNullableAndStatusCode(gson.fromJson(response.body(), JsonObject.class),
                    response.statusCode())));
        } catch (Exception e) {
            throw new CurseForgeException(e);
        }
    }

}
