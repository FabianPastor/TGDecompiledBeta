package org.telegram.messenger.exoplayer2.source.hls.playlist;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class HlsPlaylist {
    public static final int TYPE_MASTER = 0;
    public static final int TYPE_MEDIA = 1;
    public final String baseUri;
    public final int type;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    protected HlsPlaylist(String baseUri, int type) {
        this.baseUri = baseUri;
        this.type = type;
    }
}
