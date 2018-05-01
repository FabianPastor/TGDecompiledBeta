package org.telegram.messenger.exoplayer2.source.hls;

import java.io.IOException;

public final class SampleQueueMappingException extends IOException {
    public SampleQueueMappingException(String mimeType) {
        super("Unable to bind a sample queue to TrackGroup with mime type " + mimeType + ".");
    }
}
