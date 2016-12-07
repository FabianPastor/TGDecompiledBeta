package org.telegram.messenger.exoplayer.hls;

import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.chunk.FormatWrapper;

public final class Variant implements FormatWrapper {
    public final Format format;
    public final String url;

    public Variant(String url, Format format) {
        this.url = url;
        this.format = format;
    }

    public Format getFormat() {
        return this.format;
    }
}
