package org.telegram.messenger.exoplayer2.source.hls;

import java.io.IOException;

public final class SampleQueueMappingException extends IOException {
    public SampleQueueMappingException(String mimeType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unable to bind a sample queue to TrackGroup with mime type ");
        stringBuilder.append(mimeType);
        stringBuilder.append(".");
        super(stringBuilder.toString());
    }
}
