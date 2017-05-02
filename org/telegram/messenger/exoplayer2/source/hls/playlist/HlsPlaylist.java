package org.telegram.messenger.exoplayer2.source.hls.playlist;

public abstract class HlsPlaylist {
    public final String baseUri;

    protected HlsPlaylist(String baseUri) {
        this.baseUri = baseUri;
    }
}
