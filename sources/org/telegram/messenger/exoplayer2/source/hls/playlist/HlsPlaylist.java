package org.telegram.messenger.exoplayer2.source.hls.playlist;

import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.offline.FilterableManifest;

public abstract class HlsPlaylist implements FilterableManifest<HlsPlaylist, RenditionKey> {
    public final String baseUri;
    public final List<String> tags;

    protected HlsPlaylist(String baseUri, List<String> tags) {
        this.baseUri = baseUri;
        this.tags = Collections.unmodifiableList(tags);
    }
}
