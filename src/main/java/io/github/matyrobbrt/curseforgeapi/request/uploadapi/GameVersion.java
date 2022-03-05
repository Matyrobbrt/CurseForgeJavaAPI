package io.github.matyrobbrt.curseforgeapi.request.uploadapi;

/**
 * https://support.curseforge.com/en/support/solutions/articles/9000197321-curseforge-upload-api
 */
public record GameVersion(int id, int gameVersionTypeID, String name, String slug) {

}
