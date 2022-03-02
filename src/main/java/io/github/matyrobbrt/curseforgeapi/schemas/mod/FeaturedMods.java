package io.github.matyrobbrt.curseforgeapi.schemas.mod;

import java.util.List;

public record FeaturedMods(List<Mod> featured, List<Mod> popular, List<Mod> recentlyUpdated) {}