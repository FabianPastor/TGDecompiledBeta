package org.telegram.messenger.exoplayer2.ext.opus;

import org.telegram.messenger.exoplayer2.ExoPlayerLibraryInfo;

public final class OpusLibrary {
    public static native String opusGetVersion();

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.opus");
    }

    private OpusLibrary() {
    }

    public static String getVersion() {
        return opusGetVersion();
    }
}
