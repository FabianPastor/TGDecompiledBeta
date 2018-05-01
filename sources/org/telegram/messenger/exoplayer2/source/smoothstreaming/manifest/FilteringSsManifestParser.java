package org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;

public final class FilteringSsManifestParser implements Parser<SsManifest> {
    private final List<TrackKey> filter;
    private final SsManifestParser ssManifestParser = new SsManifestParser();

    public FilteringSsManifestParser(List<TrackKey> filter) {
        this.filter = filter;
    }

    public SsManifest parse(Uri uri, InputStream inputStream) throws IOException {
        return this.ssManifestParser.parse(uri, inputStream).copy(this.filter);
    }
}
