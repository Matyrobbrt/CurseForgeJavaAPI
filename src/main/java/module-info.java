module curseforgeapi {
    requires com.google.gson;
    requires methanol;
    requires org.slf4j;

    exports io.github.matyrobbrt.curseforgeapi;
    exports io.github.matyrobbrt.curseforgeapi.annotation;
    exports io.github.matyrobbrt.curseforgeapi.request;
    exports io.github.matyrobbrt.curseforgeapi.request.query;
    exports io.github.matyrobbrt.curseforgeapi.request.uploadapi;
    exports io.github.matyrobbrt.curseforgeapi.request.helper;
    exports io.github.matyrobbrt.curseforgeapi.request.async;
    exports io.github.matyrobbrt.curseforgeapi.schemas;
    exports io.github.matyrobbrt.curseforgeapi.schemas.file;
    exports io.github.matyrobbrt.curseforgeapi.schemas.game;
    exports io.github.matyrobbrt.curseforgeapi.schemas.mod;
    exports io.github.matyrobbrt.curseforgeapi.schemas.fingerprint;
    exports io.github.matyrobbrt.curseforgeapi.util;
}