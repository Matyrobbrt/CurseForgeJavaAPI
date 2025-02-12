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

package io.github.matyrobbrt.curseforgeapi.request.query;

import io.github.matyrobbrt.curseforgeapi.request.Arguments;

public class PaginationQuery implements Query {

    /**
     * Makes a {@link PaginationQuery}
     * 
     * @param  index a zero based index of the first item to include in the response
     * @return       the query
     */
    public static PaginationQuery of(int index) {
        return new PaginationQuery().index(index);
    }

    /**
     * Makes a {@link PaginationQuery}
     * 
     * @param  index    a zero based index of the first item to include in the
     *                  response
     * @param  pageSize the number of items to include in the response
     * @return          the query
     */
    public static PaginationQuery of(int index, int pageSize) {
        return new PaginationQuery().index(index).pageSize(pageSize);
    }

    private PaginationQuery() {
    }

    Integer index;
    Integer pageSize;

    /**
     * @param  index a zero based index of the first item to include in the response
     * @return       the query instance
     */
    public PaginationQuery index(int index) {
        this.index = index;
        return this;
    }

    /**
     * @param  pageSize the number of items to include in the response
     * @return          the query instance
     */
    public PaginationQuery pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public Arguments toArgs() {
        return Arguments.EMPTY.put("index", index).put("pageSize", pageSize);
    }

}
