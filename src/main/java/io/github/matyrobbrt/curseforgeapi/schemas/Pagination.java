package io.github.matyrobbrt.curseforgeapi.schemas;

import io.github.matyrobbrt.curseforgeapi.annotation.CurseForgeSchema;
import io.github.matyrobbrt.curseforgeapi.annotation.Nullable;

@CurseForgeSchema("https://docs.curseforge.com/#tocS_Pagination")
public record Pagination(int index, int pageSize, int resultCount, @Nullable Integer totalCount) {

}
