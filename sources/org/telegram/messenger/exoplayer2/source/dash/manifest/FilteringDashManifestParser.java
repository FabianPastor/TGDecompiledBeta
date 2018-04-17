package org.telegram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;

public final class FilteringDashManifestParser implements Parser<DashManifest> {
    private final DashManifestParser dashManifestParser = new DashManifestParser();
    private final ArrayList<RepresentationKey> filter;

    public FilteringDashManifestParser(ArrayList<RepresentationKey> filter) {
        this.filter = filter;
    }

    public DashManifest parse(Uri uri, InputStream inputStream) throws IOException {
        return this.dashManifestParser.parse(uri, inputStream).copy(this.filter);
    }
}
