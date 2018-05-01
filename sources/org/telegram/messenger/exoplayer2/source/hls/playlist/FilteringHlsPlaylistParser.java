package org.telegram.messenger.exoplayer2.source.hls.playlist;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;

public final class FilteringHlsPlaylistParser implements Parser<HlsPlaylist> {
    private final List<String> filter;
    private final HlsPlaylistParser hlsPlaylistParser = new HlsPlaylistParser();

    public FilteringHlsPlaylistParser(List<String> filter) {
        this.filter = filter;
    }

    public HlsPlaylist parse(Uri uri, InputStream inputStream) throws IOException {
        HlsMasterPlaylist hlsMasterPlaylist;
        HlsPlaylist hlsPlaylist = this.hlsPlaylistParser.parse(uri, inputStream);
        if (hlsPlaylist instanceof HlsMasterPlaylist) {
            hlsMasterPlaylist = (HlsMasterPlaylist) hlsPlaylist;
        } else {
            hlsMasterPlaylist = HlsMasterPlaylist.createSingleVariantMasterPlaylist(hlsPlaylist.baseUri);
        }
        return hlsMasterPlaylist.copy(this.filter);
    }
}
