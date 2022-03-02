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
