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

import io.github.matyrobbrt.curseforgeapi.schemas.file.File;
import io.github.matyrobbrt.curseforgeapi.schemas.mod.ModLoaderType;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Category exists")
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
    @DisplayName("File can be downloaded")
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
    @DisplayName("Game version is valid")
    void gameVersionIsValid() throws CurseForgeException {
        final var optionalTypes = CF_API.makeRequest(getGameVersionTypes(GameIDs.MINECRAFT));
        assertThat(optionalTypes).isPresent();

        final var types = optionalTypes.get();
        assertThat(types)
            .isNotEmpty()
            .anyMatch(type -> type.name().equals("Minecraft 1.18"));
    }
    
    @Test
    @DisplayName("Changelog exists")
    void changelogExists() throws CurseForgeException {
        final var changelogOptional = CF_API.makeRequest(getModFileChangelog(570544, 3657902));
        assertThat(changelogOptional).isPresent();
    }
    
    @Test
    @DisplayName("Featured mods exist")
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
    @DisplayName("Search Results are valid")
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

        final var cosmeticCategory = categoriesResponse.get()
            .stream()
            .filter(c -> c.name().equals("Cosmetic"))
            .findFirst()
            .get();

        final var multipleLoadersQuery = ModSearchQuery.of(game)
            .category(cosmeticCategory)
            .gameVersion("1.20.1")
            .index(1)
            .pageSize(20)
            .searchFilter("Presence Footsteps")
            .modLoaderTypes(List.of(ModLoaderType.FORGE, ModLoaderType.FABRIC))
            .sortField(SortField.TOTAL_DOWNLOADS);
        assertThat(multipleLoadersQuery.toString()).isNotEmpty();

        final var multipleLoadersResults = helper.searchMods(multipleLoadersQuery);
        assertThat(multipleLoadersResults)
            .isPresent()
            .get()
            .asList()
            .hasSizeGreaterThan(1);

        final var hasFabric = multipleLoadersResults.get().stream().anyMatch(mod -> mod.id() == 334259);
        final var hasForge = multipleLoadersResults.get().stream().anyMatch(mod -> mod.id() == 433068);
        assertThat(hasFabric && hasForge).isTrue();
    }
    
    @Test
    @DisplayName("File is invalid")
    void fileShouldBeInvalid() throws CurseForgeException {
        assertThat(CF_API.getHelper().getModFile(Integer.MIN_VALUE, Integer.MAX_VALUE)).isEmpty();
    }
    
    @Test
    @DisplayName("Minecraft exists")
    void minecraftShouldExist() throws CurseForgeException {
        final Response<List<Game>> gamesResponse = CF_API.makeRequest(getGames());
        assertThat(gamesResponse).isPresent();
        
        final List<Game> games = gamesResponse.get();
        assertThat(games).anyMatch(g -> g.id() == GameIDs.MINECRAFT);
    }
    
    @Test
    @DisplayName("Games have categories")
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
    @DisplayName("Projects can be found")
    void projectsCanBeFound() throws CurseForgeException {
        final var responses = IntStream.range(Constants.MIN_PROJECT_ID, Constants.MIN_PROJECT_ID + 4)
            .mapToObj(i -> {
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
    @DisplayName("Async and")
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
    @DisplayName("Async add 2")
    void testAsyncAnd2() throws Exception {
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
    @DisplayName("Files with fingerprints exist")
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
                    files.stream().mapToInt(File::fileFingerprint).toArray()
            ))
        ).isNotEmpty()
        .get()
        .satisfies(new Condition<>() {
            @Override
            public boolean matches(FingerprintsMatchesResult value) {
                return value.exactMatches().size() == files.size();
            }
        });
    }
    
    @Test
    @DisplayName("Slug search")
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
    @DisplayName("DateReleased is same as release date of latest file")
    void dateReleasedSameAsLatestFile() throws CurseForgeException {
        final var helper = CF_API.getHelper();

        final var modResponse = helper.getMod(MOD_ID);
        assertThat(modResponse).isNotEmpty();

        final var latestFile = helper.getModFile(MOD_ID, modResponse.get().latestFilesIndexes().get(0).fileId());
        assertThat(latestFile).isNotEmpty()
                .hasValueSatisfying(new Condition<>() {
                    @Override
                    public boolean matches(File value) {
                        return value.getFileDateAsInstant().equals(modResponse.get().getDateReleasedAsInstant());
                    }
                });
    }
    
    // Test shouldn't be executed every time.
    // @Test
    void tryUpload() throws CurseForgeException, IOException {
        final var response = CF_API.makeUploadApiRequest("minecraft", 
            UploadApiRequests.uploadFile(551821, UploadQuery
                .make("hmmm", FileReleaseType.ALPHA)
                .addRelation("mekanism", FileRelationType.REQUIRED_DEPENDENCY), Path.of("test.jar")));
        assertThat(response).isPresent();
    }

}
