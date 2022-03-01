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

package io.github.matyrobbrt.curseforgeapi.testing;

import org.junit.jupiter.api.Test;

import static io.github.matyrobbrt.curseforgeapi.testing.TestUtils.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.matyrobbrt.curseforgeapi.CurseForgeAPI;
import io.github.matyrobbrt.curseforgeapi.request.Arguments;
import io.github.matyrobbrt.curseforgeapi.request.Requests;
import io.github.matyrobbrt.curseforgeapi.util.Constants.GameIDs;
import io.github.matyrobbrt.curseforgeapi.util.CurseForgeException;

final class Testing {

    public static final String API_KEY = Dotenv.load().get("API_KEY");
    public static final CurseForgeAPI CF_API = new CurseForgeAPI(API_KEY);

    @Test
    void searchResultsShouldBeValid() throws CurseForgeException {
        final var optionalCategories = CF_API
            .makeRequest(Requests.getCategories(Arguments.of("gameId", GameIDs.MINECRAFT)));
        assertThat(optionalCategories).isPresent();
        final var optCategory = optionalCategories.get().stream()
            .filter(category -> "Armor, Tools, and Weapons".equals(category != null ? category.name() : null))
            .findAny();
        assertThat(optCategory).isPresent();
    }

    public static void main(String[] args) throws CurseForgeException {
    	CF_API.makeRequest(Requests.getCategories(Arguments.of("gameId", GameIDs.MINECRAFT)))
        .ifPresent(categories -> {
            System.out.println(categories);
        });
    }

}
