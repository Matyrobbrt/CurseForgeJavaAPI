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
import java.util.List;
import java.util.stream.IntStream;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.matyrobbrt.curseforgeapi.CurseForgeAPI;
import io.github.matyrobbrt.curseforgeapi.request.Requests;
import io.github.matyrobbrt.curseforgeapi.request.Response;
import io.github.matyrobbrt.curseforgeapi.request.query.FeaturedModsQuery;
import io.github.matyrobbrt.curseforgeapi.request.query.ModSearchQuery;
import io.github.matyrobbrt.curseforgeapi.request.query.ModSearchQuery.SortField;
import io.github.matyrobbrt.curseforgeapi.request.uploadapi.UploadApiRequests;
import io.github.matyrobbrt.curseforgeapi.request.uploadapi.UploadQuery;
import io.github.matyrobbrt.curseforgeapi.schemas.file.FileRelationType;
import io.github.matyrobbrt.curseforgeapi.schemas.file.FileReleaseType;
import io.github.matyrobbrt.curseforgeapi.schemas.fingerprint.FingerprintsMatchesResult;
import io.github.matyrobbrt.curseforgeapi.schemas.game.Game;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.Mod;
import io.github.matyrobbrt.curseforgeapi.util.Constants;
import io.github.matyrobbrt.curseforgeapi.util.Constants.GameIDs;
import io.github.matyrobbrt.curseforgeapi.util.CurseForgeException;
import io.github.matyrobbrt.curseforgeapi.util.Pair;
import io.github.matyrobbrt.curseforgeapi.util.Utils;

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

    private static final Dotenv DOTENV = Dotenv.load();
    public static final CurseForgeAPI CF_API = Utils.rethrowSupplier(() -> CurseForgeAPI
        .builder()
        .apiKey(DOTENV.get("API_KEY", ""))
        .uploadApiToken(DOTENV.get("UPLOAD_API_TOKEN", ""))
        .build())
        .get();
    
    private static final int MOD_ID = 570544;

    @Test
    void categoryShouldExist() throws CurseForgeException {
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

        final var optionalFiles = helper.getModFiles(MOD_ID);
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
    
    @Test
    void searchResultsShouldBeValid() throws CurseForgeException {
        final var helper = CF_API.getHelper();
        
        final Response<Game> gameResponse = CF_API.makeRequest(Requests.getGame(432));
        assertThat(gameResponse).isPresent();
        final var game = gameResponse.get();

        final var categoriesResponse = helper.getCategories(game.id());
        assertThat(categoriesResponse).isPresent();
        
        final var optionalCategory = categoriesResponse.get()
            .stream()
            .filter(category -> "Armor, Tools, and Weapons".equals(category.name()))
            .findAny();
        assertThat(categoriesResponse).isPresent();
        
        final var category = optionalCategory.get();

        final var query = ModSearchQuery.of(game)
            .category(category)
            .gameVersion("1.12.2")
            .index(1)
            .pageSize(20)
            .searchFilter("Weapons")
            .sortField(SortField.LAST_UPDATED);
        assertThat(query.toString()).isNotEmpty();
        
        assertThat(helper.searchMods(query))
            .isPresent()
            .get()
            .asList()
            .hasSizeGreaterThan(15);
    }
    
    @Test
    void fileShouldBeInvalid() throws CurseForgeException {
        assertThat(CF_API.getHelper().getModFile(Integer.MIN_VALUE, Integer.MAX_VALUE)).isEmpty();
    }
    
    @Test
    void minecraftShouldExist() throws CurseForgeException {
        final Response<List<Game>> gamesResponse = CF_API.makeRequest(getGames());
        assertThat(gamesResponse).isPresent();
        
        final List<Game> games = gamesResponse.get();
        assertThat(games).anyMatch(g -> g.id() == GameIDs.MINECRAFT);
    }
    
    @Test
    void gamesHaveCategories() throws CurseForgeException {
        final Response<List<Game>> gamesResponse = CF_API.makeRequest(getGames());
        assertThat(gamesResponse).isPresent();
        
        for (final Game game : gamesResponse.get()) {
            assertThat(CF_API.getHelper().getCategories(game.id()))
                .isPresent()
                .get()
                .asList()
                .isNotEmpty();
        }
    }
    
    @Test
    void projectsCanBeFound() throws CurseForgeException {
        final var responses = IntStream.range(Constants.MIN_PROJECT_ID, Constants.MIN_PROJECT_ID + 4)
            .<Response<Mod>>mapToObj(i -> {
                try {
                    return CF_API.makeRequest(Requests.getMod(i));
                } catch (CurseForgeException e) {
                    e.printStackTrace();
                }
                return Response.empty(null);
            })
            .filter(Response::isPresent)
            .map(Response::get)
            .toList();
        
        assertThat(responses).hasSize(4);
    }
    
    @Test
    void testAsyncAnd() throws Exception {
        final var asyncHelper = CF_API.getAsyncHelper();
        final var responseOptional = asyncHelper.getMod(MOD_ID)
            .and(CF_API.makeAsyncRequest(getModDescription(MOD_ID)))
            .map(Pair::mapResponses)
            .get();
        
        assertThat(responseOptional).isPresent();
        responseOptional.get().accept((mod, description) -> {
            assertThat(mod).isNotNull();
            assertThat(description).isNotBlank();
        });
    }
    
    @Test
    void testAsyncAdd2() throws Exception {
        final var asyncHelper = CF_API.getAsyncHelper();
        final var r = asyncHelper.getMod(MOD_ID)
            .and(CF_API.makeAsyncRequest(getModDescription(MOD_ID)))
            .get();
        r.accept((r1, r2) -> {
           assertThat(r1).isPresent();
           assertThat(r2).isPresent();
        });
    }
    
    @Test
    void filesWithFingerprintExist() throws Exception {
        final var helper = CF_API.getHelper();
        
        final var filesResponse = helper.getModFiles(MOD_ID);
        assertThat(filesResponse).isPresent()
            .get()
            .asList()
            .isNotEmpty();
        
        final var files = filesResponse.get();
        assertThat(
            CF_API.makeRequest(getFingerprintMatches(
                IntStream
                    .range(0, files.size())
                    .map(i -> files.get(i).fileFingerprint()).toArray()
            ))    
        ).isNotEmpty()
        .get()
        .satisfies(new Condition<FingerprintsMatchesResult>() {
            @Override
            public boolean matches(FingerprintsMatchesResult value) {
                return value.exactMatches().size() == files.size();
            };
        });
    }
    
    @Test
    void slugSearchWorks() throws CurseForgeException {
        final var helper = CF_API.getHelper();
        
        final var modResponse = helper.getMod(MOD_ID);
        assertThat(modResponse).isNotEmpty();
        
        final var mod = modResponse.get();
        final var queryResponse = helper.searchMods(ModSearchQuery.of(mod.gameId())
            .classId(mod.classId())
            .slug(mod.slug()));
        assertThat(queryResponse).isNotEmpty();
    }
    
    @Test
    void tryUpload() throws CurseForgeException, IOException {
        final var response = CF_API.makeUploadApiRequest("minecraft", 
            UploadApiRequests.uploadFile(551821, UploadQuery
                .make("hmmm", FileReleaseType.ALPHA)
                .addRelation("mekanism", FileRelationType.REQUIRED_DEPENDENCY), Path.of("test.jar")));
        assertThat(response).isPresent();
    }

}
