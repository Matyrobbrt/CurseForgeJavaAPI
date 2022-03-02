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

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.matyrobbrt.curseforgeapi.CurseForgeAPI;
import io.github.matyrobbrt.curseforgeapi.request.Requests;
import io.github.matyrobbrt.curseforgeapi.request.query.FeaturedModsQuery;
import io.github.matyrobbrt.curseforgeapi.util.Constants.GameIDs;
import io.github.matyrobbrt.curseforgeapi.util.CurseForgeException;

import static io.github.matyrobbrt.curseforgeapi.request.Requests.*;
import static io.github.matyrobbrt.curseforgeapi.testing.Assertions.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Most tests made in this class use the <a href=
 * "https://www.curseforge.com/minecraft/mc-mods/eating-animation-forge">Eating
 * Animations</a> project.
 * 
 * @author matyrobbrt
 *
 */
@SuppressWarnings("static-method")
final class Testing {

    public static final String API_KEY = Dotenv.load().get("API_KEY");
    public static final CurseForgeAPI CF_API = new CurseForgeAPI(API_KEY);

    @Test
    void searchResultsShouldBeValid() throws CurseForgeException {
        final var helper = CF_API.getHelper();

        final var optionalCategories = helper.getCategories(GameIDs.MINECRAFT);
        assertThat(optionalCategories).isPresent();

        final var optCategory = optionalCategories
            .get()
            .stream()
            .filter(category -> "Armor, Tools, and Weapons".equals(category.name()))
            .findAny();
        assertThat(optCategory).isPresent();
    }

    @Test
    void tryDownloadFile() throws CurseForgeException, IOException {
        final var helper = CF_API.getHelper();

        final var optionalFiles = helper.getModFiles(570544);
        assertThat(optionalFiles).isPresent();

        final var files = optionalFiles.get();
        assertThat(files).isNotEmpty();

        final var dowPath = Path.of("test", "test.jar");
        files.get(0).download(dowPath);
        assertThat(dowPath).exists();
    }

    @Test
    void gameVersionIsValid() throws CurseForgeException {
        final var optionalTypes = CF_API.makeRequest(getGameVersionTypes(GameIDs.MINECRAFT));
        assertThat(optionalTypes).isPresent();

        final var types = optionalTypes.get();
        assertThat(types)
            .isNotEmpty()
            .anyMatch(type -> type.name().equals("Minecraft 1.18"));
    }
    
    @Test
    void changelogExists() throws CurseForgeException {
        final var changelogOptional = CF_API.makeRequest(getModFileChangelog(570544, 3657902));
        assertThat(changelogOptional).isPresent();
    }
    
    @Test
    void featuredModsExist() throws CurseForgeException {
        final var optionalFeaturedMods = CF_API.makeRequest(Requests.getFeaturedMods(
            FeaturedModsQuery.of(GameIDs.MINECRAFT)
                .excludeMod(570544)
        ));
        assertThat(optionalFeaturedMods).isPresent();
        
        final var featuredMods = optionalFeaturedMods.get();
        assertThat(featuredMods.featured()).isNotEmpty();
        assertThat(featuredMods.popular()).isNotEmpty();
        assertThat(featuredMods.recentlyUpdated()).isNotEmpty();
    }

}
