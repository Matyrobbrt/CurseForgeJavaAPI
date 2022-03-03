package io.github.matyrobbrt.curseforgeapi.schemas.fingerprint;

import java.util.List;

import io.github.matyrobbrt.curseforgeapi.annotation.CurseForgeSchema;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;

@CurseForgeSchema("https://docs.curseforge.com/#tocS_FingerprintsMatchesResult")
public record FingerprintsMatchesResult(boolean isCacheBuilt, List<FingerprintMatch> exactMatches,
    List<Integer> exactFingerprints, List<FingerprintMatch> partialMatches, Object partialMatchFingerprints,
    List<Integer> additionalProperties, List<Integer> installedFingerprints, @Nullable List<Integer> unmatchedFingerprints) {

}