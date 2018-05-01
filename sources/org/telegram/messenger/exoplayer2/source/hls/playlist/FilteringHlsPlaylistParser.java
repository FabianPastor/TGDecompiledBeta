package org.telegram.messenger.exoplayer2.source.hls.playlist;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;

public final class FilteringHlsPlaylistParser implements Parser<HlsPlaylist> {
    private final List<String> filter;
    private final HlsPlaylistParser hlsPlaylistParser = new HlsPlaylistParser();

    public FilteringHlsPlaylistParser(List<String> list) {
        this.filter = list;
    }

    public HlsPlaylist parse(Uri uri, InputStream inputStream) throws IOException {
        uri = this.hlsPlaylistParser.parse(uri, inputStream);
        if ((uri instanceof HlsMasterPlaylist) != null) {
            uri = (HlsMasterPlaylist) uri;
        } else {
            uri = HlsMasterPlaylist.createSingleVariantMasterPlaylist(uri.baseUri);
        }
        return uri.copy(this.filter);
    }
}
