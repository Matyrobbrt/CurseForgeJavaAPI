package io.github.matyrobbrt.curseforgeapi.schemas.fingerprint;

import java.util.List;

import io.github.matyrobbrt.curseforgeapi.annotation.CurseForgeSchema;
import io.github.matyrobbrt.curseforgeapi.schemas.file.File;

@CurseForgeSchema("https://docs.curseforge.com/#tocS_FingerprintFuzzyMatch")
public record FingerprintFuzzyMatch(int id, File file, List<File> latestFiles, List<Integer> fingerprints) {}
