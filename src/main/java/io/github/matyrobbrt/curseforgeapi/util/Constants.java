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

package io.github.matyrobbrt.curseforgeapi.util;

/**
 * A class holding different constant values.
 * 
 * @author matyrobbrt
 *
 */
public final class Constants {

    /**
     * The minimum CurseForge project ID.
     */
    public static final int MIN_PROJECT_ID = 10;

    /**
     * @see <a href=
     *      "https://datatracker.ietf.org/doc/html/rfc7231">https://datatracker.ietf.org/doc/html/rfc7231</a>
     *
     */
    public static final class StatusCodes {

        /**
         * The 200 (OK) status code indicates that the request has succeeded.
         * 
         * @see <a href=
         *      "https://tools.ietf.org/html/rfc7231#section-6.3.1">https://tools.ietf.org/html/rfc7231#section-6.3.1</a>
         */
        public static final int OK = 200;

        /**
         * The 400 (Bad Request) status code indicates that the server cannot or will
         * not process the request due to something that is perceived to be a client
         * error (e.g., malformed request syntax, invalid request message framing, or
         * deceptive request routing).
         * 
         * @see <a href=
         *      "https://tools.ietf.org/html/rfc7231#section-6.5.1">https://tools.ietf.org/html/rfc7231#section-6.5.1</a>
         */
        public static final int BAD_REQUEST = 400;

        /**
         * The 401 (Unauthorized) status code indicates that the request has not been
         * applied because it lacks valid authentication credentials for the target
         * resource. The server generating a 401 response MUST send a WWW-Authenticate
         * header field containing at least one challenge applicable to the target
         * resource.
         * 
         * If the request included authentication credentials, then the 401 response
         * indicates that authorization has been refused for those credentials. The user
         * agent MAY repeat the request with a new or replaced Authorization header
         * field. If the 401 response contains the same challenge as the prior response,
         * and the user agent has already attempted authentication at least once, then
         * the user agent SHOULD present the enclosed representation to the user, since
         * it usually contains relevant diagnostic information.
         * 
         * @see <a href=
         *      "https://datatracker.ietf.org/doc/html/rfc7235#section-3.1">https://datatracker.ietf.org/doc/html/rfc7235#section-3.1</a>
         */
        public static final int UNAUTHORIZED = 401;

        /**
         * The 403 (Forbidden) status code indicates that the server understood the
         * request but refuses to authorize it. A server that wishes to make public why
         * the request has been forbidden can describe that reason in the response
         * payload (if any).
         * 
         * If authentication credentials were provided in the request, the server
         * considers them insufficient to grant access. The client SHOULD NOT
         * automatically repeat the request with the same credentials. The client MAY
         * repeat the request with new or different credentials. However, a request
         * might be forbidden for reasons unrelated to the credentials.
         * 
         * An origin server that wishes to "hide" the current existence of a forbidden
         * target resource MAY instead respond with a status code of 404 (Not Found).
         * 
         * @see <a href=
         *      "https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.3">https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.3</a>
         */
        public static final int FORBIDDEN = 403;

        /**
         * The 404 (Not Found) status code indicates that the origin server did not find
         * a current representation for the target resource or is not willing to
         * disclose that one exists.
         * 
         * @see <a href=
         *      "https://tools.ietf.org/html/rfc7231#section-6.5.4">https://tools.ietf.org/html/rfc7231#section-6.5.4</a>
         */
        public static final int NOT_FOUND = 404;

        /**
         * The 500 (Internal Server Error) status code indicates that the server
         * encountered an unexpected condition that prevented it from fulfilling the
         * request.
         * 
         * @see <a href=
         *      "https://tools.ietf.org/html/rfc7231#section-6.6.1">https://tools.ietf.org/html/rfc7231#section-6.6.1</a>
         */
        public static final int INTERNAL_SERVER_ERROR = 500;
        
        public static final int API_UNAVAILABLE = 503;

        /**
         * The 504 (Gateway Timeout) status code indicates that the server,
         * while acting as a gateway or proxy, did not receive a timely response
         * from an upstream server it needed to access in order to complete the
         * request.
         * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.5">https://tools.ietf.org/html/rfc7231#section-6.6.5</a>
         */
        public static final int GATEWAY_TIMEOUT = 504;

    }

    public static final class GameIDs {

        public static final int MINECRAFT = 432;
        public static final int WILD_STAR = 454;
        public static final int THE_SECRET_WORLD = 64;
        public static final int SECRET_WORLD_LEGENDS = 445;
        public static final int RUNES_OF_MAGIC = 335;
        public static final int DARKEST_DUNGEON = 608;
    }
}
