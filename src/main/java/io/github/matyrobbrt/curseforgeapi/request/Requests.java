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

package io.github.matyrobbrt.curseforgeapi.request;

import java.util.List;

import io.github.matyrobbrt.curseforgeapi.annotation.AcceptsArgs;
import io.github.matyrobbrt.curseforgeapi.annotation.Arg;
import io.github.matyrobbrt.curseforgeapi.annotation.Arg.Type;
import io.github.matyrobbrt.curseforgeapi.schemas.Category;
import io.github.matyrobbrt.curseforgeapi.schemas.Game;

/**
 * A utility class that contains methods for creating common requests. Please
 * see <a href="https://docs.curseforge.com/">the CurseForge documentation</a>
 * for more information.
 * 
 * @author matyrobbrt
 *
 */
public final class Requests {

    public static Request<List<Category>> getCategories(int gameId) {
        return new Request<>("/v1/categories?gameId=%s".formatted(gameId), Method.GET,
            j -> j.getListJsonObject("data", Category::new));
    }

    public static Request<List<Category>> getCategories(int gameId, int classId) {
        return new Request<>("/v1/categories?gameId=%s?classId=%s".formatted(gameId, classId), Method.GET,
            j -> j.getListJsonObject("data", Category::new));
    }

    @AcceptsArgs({
        @Arg(name = "index", description = "A zero based index of the first item to include in the response.", type = Type.INTEGER),
        @Arg(name = "pageSize", description = "The number of items to include in the response", type = Type.INTEGER)
    })
    public static Request<List<Game>> getGames(Arguments args) {
        return new Request<>(format("/v1/games", args), Method.GET, j -> j.getListJsonObject("data", Game::new));
    }

    public static String format(String str, Arguments args) {
        return str + args.build();
    }

}
