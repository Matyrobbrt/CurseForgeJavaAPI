package io.github.matyrobbrt.curseforgeapi.schemas.fingerprint;

import io.github.matyrobbrt.curseforgeapi.annotation.CurseForgeSchema;
import io.github.matyrobbrt.curseforgeapi.schemas.file.File;

@CurseForgeSchema("https://docs.curseforge.com/#tocS_FingerprintMatch")
public record FingerprintMatch(int id, File file, java.util.List<File> latestFiles) {}