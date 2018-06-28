package org.telegram.messenger.exoplayer2.offline;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;

public final class FilteringManifestParser<T extends FilterableManifest<T, K>, K> implements Parser<T> {
    private final Parser<T> parser;
    private final List<K> trackKeys;

    public FilteringManifestParser(Parser<T> parser, List<K> trackKeys) {
        this.parser = parser;
        this.trackKeys = trackKeys;
    }

    public T parse(Uri uri, InputStream inputStream) throws IOException {
        T manifest = (FilterableManifest) this.parser.parse(uri, inputStream);
        return (this.trackKeys == null || this.trackKeys.isEmpty()) ? manifest : (FilterableManifest) manifest.copy(this.trackKeys);
    }
}
