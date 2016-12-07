package org.telegram.messenger.exoplayer.extractor;

import org.telegram.messenger.exoplayer.drm.DrmInitData;

public interface ExtractorOutput {
    void drmInitData(DrmInitData drmInitData);

    void endTracks();

    void seekMap(SeekMap seekMap);

    TrackOutput track(int i);
}
