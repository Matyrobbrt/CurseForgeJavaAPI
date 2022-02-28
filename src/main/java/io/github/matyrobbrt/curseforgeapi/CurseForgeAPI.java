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
import java.net.http.HttpResponse;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import io.github.matyrobbrt.curseforgeapi.annotation.Nonnull;
import io.github.matyrobbrt.curseforgeapi.annotation.ParametersAreNonnullByDefault;
import io.github.matyrobbrt.curseforgeapi.request.GenericRequest;
import io.github.matyrobbrt.curseforgeapi.request.Method;
import io.github.matyrobbrt.curseforgeapi.request.Request;
import io.github.matyrobbrt.curseforgeapi.util.CurseForgeException;
import io.github.matyrobbrt.curseforgeapi.util.Utils;
import io.github.matyrobbrt.curseforgeapi.util.WrappedJson;

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
    private int lastStatusCode;

    public CurseForgeAPI(final String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        this.gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public CurseForgeAPI(final String apiKey, final HttpClient httpClient, final Gson gson, final Logger logger) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.gson = gson;
        this.logger = logger;
    }

    /**
     * Sends a request to the API
     * 
     * @param  <R>                 the type of the request result
     * @param  request             the request to send
     * @return                     the result of the request, deserialized from a
     *                             {@link JsonObject} using
     *                             {@link Request#decodeResponse(WrappedJson)}.
     * @throws CurseForgeException
     */
    public <R> R makeRequest(Request<? extends R> request) throws CurseForgeException {
        final var resJson = makeGenericRequest(request);
        if (resJson == null) { return null; }
        return request.decodeResponse(new WrappedJson(resJson));
    }

    /**
     * Sends a generic request to the API.
     * 
     * @param  genericRequest      the request to send
     * @return                     the result of the request
     * @throws CurseForgeException
     */
    @Nonnull
    public JsonObject makeGenericRequest(GenericRequest genericRequest) throws CurseForgeException {
        try {
            final URL target = new URL(REQUEST_TARGET + genericRequest.endpoint());
            final var httpRequest = Utils.<HttpRequest.Builder>makeWithSupplier(() -> {
                var r = HttpRequest.newBuilder(URI.create(target.toString())).header("Accept", "application/json")
                    .header("x-api-key", apiKey);
                if (genericRequest.method() == Method.GET) {
                    r = r.GET();
                }
                return r;
            }).build();
            final var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            lastStatusCode = response.statusCode();
            return gson.fromJson(response.body(), JsonObject.class);
        } catch (InterruptedException ine) {
            logger.info("The last status code was: " + lastStatusCode);
            logger.error("InterruptedException while awaiting CurseForge response: ", ine);
            Thread.currentThread().interrupt();
            return new JsonObject();
        } catch (Exception e) {
            logger.info("The last status code was: " + lastStatusCode);
            throw new CurseForgeException(e);
        }
    }

}
