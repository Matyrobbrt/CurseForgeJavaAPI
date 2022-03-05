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

import java.lang.StackWalker.Option;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;
import io.github.matyrobbrt.curseforgeapi.annotation.ParametersAreNonnullByDefault;
import io.github.matyrobbrt.curseforgeapi.request.AsyncRequest;
import io.github.matyrobbrt.curseforgeapi.request.GenericRequest;
import io.github.matyrobbrt.curseforgeapi.request.Request;
import io.github.matyrobbrt.curseforgeapi.request.Requests;
import io.github.matyrobbrt.curseforgeapi.request.Response;
import io.github.matyrobbrt.curseforgeapi.request.helper.AsyncRequestHelper;
import io.github.matyrobbrt.curseforgeapi.request.helper.RequestHelper;
import io.github.matyrobbrt.curseforgeapi.request.uploadapi.UploadApiRequest;
import io.github.matyrobbrt.curseforgeapi.request.uploadapi.UploadApiRequests;
import io.github.matyrobbrt.curseforgeapi.schemas.ApiStatus;
import io.github.matyrobbrt.curseforgeapi.schemas.HashAlgo;
import io.github.matyrobbrt.curseforgeapi.schemas.Status;
import io.github.matyrobbrt.curseforgeapi.schemas.file.FileRelationType;
import io.github.matyrobbrt.curseforgeapi.schemas.file.FileReleaseType;
import io.github.matyrobbrt.curseforgeapi.schemas.file.FileStatus;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.ModLoaderType;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.ModStatus;
import io.github.matyrobbrt.curseforgeapi.util.Constants;
import io.github.matyrobbrt.curseforgeapi.util.CurseForgeException;
import io.github.matyrobbrt.curseforgeapi.util.Utils;
import io.github.matyrobbrt.curseforgeapi.util.Constants.GameIDs;
import io.github.matyrobbrt.curseforgeapi.util.Constants.StatusCodes;
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

    /**
     * The base URL for requests to the CurseForge API.
     */
    public static final String REQUEST_TARGET = "https://api.curseforge.com";
    /**
     * The base URL for requests to the CurseForge Upload API.
     */
    public static final String UPLOAD_REQUEST_TARGET = "https://%s.curseforge.com";

    // Defaults

    /**
     * The default {@link com.google.gson.Gson} used for decoding responses.
     */
    public static final Gson DEFAULT_GSON = Utils.makeWithSupplier(() -> {
        final var gsonBuilder = new GsonBuilder().setPrettyPrinting().setLenient().disableHtmlEscaping().serializeNulls()
            .registerTypeAdapterFactory(new RecordTypeAdapterFactory());

        final List<Class<? extends Enum<?>>> cfSchemaEnums = List.of(ApiStatus.class, FileRelationType.class,
            FileReleaseType.class, FileStatus.class, ModLoaderType.class, HashAlgo.class, Status.class,
            ModStatus.class);
        cfSchemaEnums.forEach(e -> gsonBuilder.registerTypeAdapter(e, CFSchemaEnumTypeAdapter.constructUnsafe(e)));
        return gsonBuilder.create();
    });
    /**
     * The factory that supplies default {@link java.net.http.HttpClient} used for
     * making HTTP requests.
     */
    //@formatter:off
    public static final Supplier<HttpClient> DEFAULT_HTTP_CLIENT_FACTORY = () -> HttpClient
        .newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();
    //@formatter:on

    @Nullable
    private final String apiKey;
    @Nullable
    private final String uploadApiToken;
    private final HttpClient httpClient;
    private final Gson gson;
    private final Logger logger;

    private final RequestHelper helper = new RequestHelper(this);
    private final AsyncRequestHelper asyncHelper = new AsyncRequestHelper(this);

    /**
     * @apiNote This constructor should only be used internally, by {@link Builder}.
     *          Any other illegal calls (by making the constructor
     *          {@link java.lang.reflect.Constructor#setAccessible(boolean)
     *          accessible}) to this constructor will result in an
     *          {@link IllegalCallerException}.
     */
    private CurseForgeAPI(@Nullable String apiKey, @Nullable String uploadApiToken, HttpClient httpClient, Gson gson,
        Logger logger) {
        // Make sure that the constructor is not called illegally, because that can
        // prevent
        // the token check, which is mandatory
        if (StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).getCallerClass() != Builder.class) {
            throw new IllegalCallerException("Illegal access to constructor!");
        }
        this.apiKey = apiKey;
        this.uploadApiToken = uploadApiToken;
        this.httpClient = httpClient;
        this.gson = gson;
        this.logger = logger;
    }

    /**
     * Creates a {@link Builder} instance for creating a
     * {@link io.github.matyrobbrt.curseforgeapi.CurseForgeAPI} instance.
     * 
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @deprecated        Use the {@link Builder} ({@link #builder()}). This
     *                    constructor will be removed in a future version.
     * @param      apiKey
     */
    @Deprecated(since = "1.5.0", forRemoval = true)
    public CurseForgeAPI(final String apiKey) {
        this.apiKey = apiKey;
        this.uploadApiToken = null;
        this.gson = DEFAULT_GSON;
        this.httpClient = DEFAULT_HTTP_CLIENT_FACTORY.get();
        this.logger = LoggerFactory.getLogger(CurseForgeAPI.class);
        if (!isAuthorized())
            throw new IllegalArgumentException("Invalid API Key!");
    }

    /**
     * @deprecated            Use the {@link Builder} ({@link #builder()}). This
     *                        constructor will be removed in a future version.
     * @param      apiKey
     * @param      httpClient
     * @param      gson
     * @param      logger
     */
    @Deprecated(since = "1.5.0", forRemoval = true)
    public CurseForgeAPI(final String apiKey, final HttpClient httpClient, final Gson gson, final Logger logger) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.gson = gson;
        this.logger = logger;
        this.uploadApiToken = null;
        if (!isAuthorized())
            throw new IllegalArgumentException("Invalid API Key!");
    }

    /**
     * Makes a simple request to the CurseForge API, to check if the API Key is
     * valid.
     * 
     * @returns {@code false} if the request responds with the status code
     *          {@link Constants.StatusCodes#UNAUTHORIZED},
     *          {@link Constants.StatusCodes#FORBIDDEN}, or the request encountered
     *          an exception.
     */
    public boolean isAuthorized() {
        try {
            final var statusCode = makeRequest(Requests.getGame(GameIDs.MINECRAFT)).getStatusCode();
            return statusCode != Constants.StatusCodes.UNAUTHORIZED && statusCode != Constants.StatusCodes.FORBIDDEN;
        } catch (CurseForgeException e) {
            logger.error("Could not check if the API Key is valid due to an exception.", e);
            return false;
        }
    }

    /**
     * Makes a simple request to the CurseForge Upload API, to check if the API
     * Token is valid.
     * 
     * @returns {@code false} if the request responds with the status code
     *          {@link Constants.StatusCodes#UNAUTHORIZED},
     *          {@link Constants.StatusCodes#FORBIDDEN}, or the request encountered
     *          an exception.
     */
    public boolean isAuthorizedForUpload() {
        try {
            final var statusCode = makeUploadApiRequest("minecraft", UploadApiRequests.getGameDependencies()).getStatusCode();
            return statusCode != Constants.StatusCodes.UNAUTHORIZED && statusCode != Constants.StatusCodes.FORBIDDEN && statusCode != StatusCodes.NOT_FOUND;
        } catch (CurseForgeException e) {
            logger.error("Could not check if the Upload API Token is valid due to an exception.", e);
            return false;
        }
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

    @Nullable
    public String getApiKey() {
        return apiKey;
    }

    @Nullable
    public String getUploadApiToken() {
        return uploadApiToken;
    }

    /**
     * @return the helper used for direct requests, without going through
     *         {@link Requests} first
     */
    public RequestHelper getHelper() {
        return helper;
    }

    /**
     * @return the helper used for direct async requests, without going through
     *         {@link Requests} first
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
        if (apiKey == null)
            throw new CurseForgeException("Cannot make requests with a null API key!");
        int statusCode = 0;
        try {
            final URL target = new URL(REQUEST_TARGET + genericRequest.endpoint());
            final var httpRequest = Utils.makeWithSupplier(() -> {
                var r = HttpRequest.newBuilder(URI.create(target.toString())).header("Accept", "application/json")
                    .header("x-api-key", apiKey);
                r = switch (genericRequest.method()) {
                case GET -> r.GET();
                case POST -> r.POST(BodyPublishers.ofString(genericRequest.body().toString())).header("Content-Type",
                    "application/json");
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
        if (apiKey == null)
            throw new CurseForgeException("Cannot make requests with a null API key!");
        try {
            final URL target = new URL(REQUEST_TARGET + genericRequest.endpoint());
            final var httpRequest = Utils.makeWithSupplier(() -> {
                var r = HttpRequest.newBuilder(URI.create(target.toString())).header("Accept", "application/json")
                    .header("x-api-key", apiKey);
                r = switch (genericRequest.method()) {
                case GET -> r.GET();
                case POST -> r.POST(BodyPublishers.ofString(genericRequest.body().toString())).header("Content-Type",
                    "application/json");
                case PUT -> r.PUT(BodyPublishers.ofString(genericRequest.body().toString()));
                };
                return r;
            }).build();
            return new AsyncRequest<>(() -> httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> Response
                    .ofNullableAndStatusCode(gson.fromJson(response.body(), JsonObject.class), response.statusCode())));
        } catch (Exception e) {
            throw new CurseForgeException(e);
        }
    }

    /********************************
     * 
     * Upload API
     * 
     ********************************/

    /**
     * Sends a <b>blocking</b> request to the Upload API
     * 
     * @param  <R>                 the type of the request result
     * @param  gameSlug            the slug of the game to make the request to
     * @param  request             the request to send
     * @return                     the response of the request, deserialized from a
     *                             {@link JsonObject} using
     *                             {@link UploadApiRequest#decodeResponse(WrappedJson)},
     *                             if present
     * @throws CurseForgeException
     */
    public <R> Response<R> makeUploadApiRequest(String gameSlug, UploadApiRequest<? extends R> request)
        throws CurseForgeException {
        if (uploadApiToken == null)
            throw new CurseForgeException("Cannot make requests with a null Upload API token!");
        int statusCode = 0;
        try {
            final URL target = new URL(UPLOAD_REQUEST_TARGET.formatted(gameSlug) + request.endpoint());
            final var httpRequest = Utils.makeWithSupplier(() -> {
                var r = HttpRequest.newBuilder(URI.create(target.toString())).header("Accept", "application/json")
                    .header("X-Api-Token", uploadApiToken);
                r = switch (request.method()) {
                case GET -> r.GET();
                case POST -> r.POST(request.bodyPublisher());
                case PUT -> r.PUT(request.bodyPublisher());
                };
                return r;
            }).build();
            final var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            statusCode = response.statusCode();
            if (statusCode == StatusCodes.NOT_FOUND) {
                return Response.empty(statusCode);
            }
            return Response.ofNullableAndStatusCode(gson.fromJson(response.body(), JsonElement.class), statusCode)
                .map(j -> request.responseDecoder().apply(gson, j));
        } catch (InterruptedException ine) {
            logger.error(
                "InterruptedException while awaiting CurseForge Upload API response, which returned with the status code: ",
                ine);
            Thread.currentThread().interrupt();
            return Response.empty(statusCode);
        } catch (Exception e) {
            logger.info("Status code was {}", statusCode);
            throw new CurseForgeException(e);
        }
    }

    // Async

    /**
     * Sends an <b>async</b> request to the Upload API.
     * 
     * @param  <R>                 the type of the request result
     * @param  gameSlug            the slug of the game to make the request to
     * @param  request             the request to send
     * @return                     the async request, which will be sent when
     *                             {@link AsyncRequest#queue} is called. The result
     *                             is deserialized from a {@link JsonObject} using
     *                             {@link UploadApiRequest#decodeResponse(WrappedJson)},
     *                             if present
     * @throws CurseForgeException
     */
    public <R> AsyncRequest<Response<R>> makeAsyncUploadApiRequest(String gameSlug,
        UploadApiRequest<? extends R> request) throws CurseForgeException {
        if (uploadApiToken == null)
            throw new CurseForgeException("Cannot make requests with a null Upload API token!");
        try {
            final URL target = new URL(UPLOAD_REQUEST_TARGET.formatted(gameSlug) + request.endpoint());
            final var httpRequest = Utils.makeWithSupplier(() -> {
                var r = HttpRequest.newBuilder(URI.create(target.toString())).header("Accept", "application/json")
                    .header("X-Api-Token", uploadApiToken);
                r = switch (request.method()) {
                case GET -> r.GET();
                case POST -> r.POST(request.bodyPublisher());
                case PUT -> r.PUT(request.bodyPublisher());
                };
                return r;
            }).build();
            return new AsyncRequest<>(() -> httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> Response
                    // A 404 returns the request apparently?
                    .ofNullableAndStatusCode(response.statusCode() == StatusCodes.NOT_FOUND ? null : gson.fromJson(response.body(), JsonElement.class), response.statusCode())
                    .map(j -> request.responseDecoder().apply(gson, j))));
        } catch (Exception e) {
            throw new CurseForgeException(e);
        }
    }

    /**
     * A builder class used for creating {@link CurseForgeAPI} instances.
     * 
     * @author matyrobbrt
     *
     */
    @ParametersAreNonnullByDefault
    public static final class Builder {

        private Builder() {
        }

        private String apiKey;
        private String uploadApiToken;
        private Gson gson = DEFAULT_GSON;
        private Logger logger = LoggerFactory.getLogger(CurseForgeAPI.class);
        private Supplier<HttpClient> httpClient = DEFAULT_HTTP_CLIENT_FACTORY;

        /**
         * Sets the API Key used for requests to the
         * <a href="https://docs.curseforge.com/">CurseForge API</a>.
         * 
         * @param  apiKey the API Key
         * @return        the builder instance, for chaining purposes
         */
        public Builder apiKey(@Nullable String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Sets the API Token used for requests to the <a href=
         * "https://support.curseforge.com/en/support/solutions/articles/9000197321-curseforge-upload-api">CurseForge
         * Upload API</a>.
         * 
         * @param  uploadApiToken the upload API token
         * @return                the builder instance, for chaining purposes
         */
        public Builder uploadApiToken(@Nullable String uploadApiToken) {
            this.uploadApiToken = uploadApiToken;
            return this;
        }

        /**
         * Sets the {@link com.google.gson.Gson} used for decoding responses. <br>
         * By default, this is set to
         * {@link io.github.matyrobbrt.curseforgeapi.CurseForgeAPI#DEFAULT_GSON}.
         * 
         * @param  gson the gson
         * @return      the builder instance, for chaining purposes
         */
        public Builder gson(Gson gson) {
            this.gson = Objects.requireNonNull(gson, "Cannot build a CurseForgeAPI with a null Gson.");
            return this;
        }

        /**
         * Sets the {@link com.google.gson.Gson} used for decoding responses to the
         * {@link io.github.matyrobbrt.curseforgeapi.CurseForgeAPI#DEFAULT_GSON},
         * modified by the {@code modifyingConsumer} to suit your needs.
         * 
         * @param  modifyingConsumer the consumer that will modify the default
         *                           {@link com.google.gson.GsonBuilder}. Can be
         *                           {@code null}
         * @return                   the builder instance, for chaining purposes
         */
        public Builder defaultGson(@Nullable Consumer<GsonBuilder> modifyingConsumer) {
            if (modifyingConsumer == null) {
                gson = DEFAULT_GSON;
            } else {
                final var builder = DEFAULT_GSON.newBuilder();
                modifyingConsumer.accept(builder);
                gson = builder.create();
            }
            return this;
        }

        /**
         * Sets the {@link org.slf4j.Logger} used for logging information. <br>
         * By default, this is set to
         * {@code LoggerFactory.getLogger(CurseForgeAPI.class)}
         * 
         * @param  logger the logger
         * @return        the builder instance, for chaining purposes
         */
        public Builder logger(Logger logger) {
            this.logger = Objects.requireNonNull(logger, "Cannot build a CurseForgeAPI with a null Logger.");
            return this;
        }

        /**
         * Sets the {@link java.net.http.HttpClient} used for sending HTTP requests.
         * <br>
         * By default, this is set to
         * {@link io.github.matyrobbrt.curseforgeapi.CurseForgeAPI#DEFAULT_HTTP_CLIENT_FACTORY}.
         * 
         * @param  logger the logger
         * @return        the builder instance, for chaining purposes
         */
        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = () -> Objects.requireNonNull(httpClient,
                "Cannot build a CurseForgeAPI with a null HttpClient.");
            return this;
        }

        /**
         * Builds the {@link io.github.matyrobbrt.curseforgeapi.CurseForgeAPI} based on
         * the configurations of this Builder.
         * 
         * @return                the CurseForgeAPI instance
         * @throws LoginException if either of the keys provided are not {@code null},
         *                        but invalid
         */
        public CurseForgeAPI build() throws LoginException {
            final var api = new CurseForgeAPI(apiKey, uploadApiToken, httpClient.get(), gson, logger);
            if (apiKey != null && !api.isAuthorized())  throw new LoginException("The apiKey provided is invalid.");
            if (uploadApiToken != null && !api.isAuthorizedForUpload()) {
                throw new LoginException("The uploadApiToken provided is invalid.");
            }
            return api;
        }
    }
}
