package org.telegram.messenger.exoplayer.smoothstreaming;

import java.io.IOException;

public interface SmoothStreamingTrackSelector {

    public interface Output {
        void adaptiveTrack(SmoothStreamingManifest smoothStreamingManifest, int i, int[] iArr);

        void fixedTrack(SmoothStreamingManifest smoothStreamingManifest, int i, int i2);
    }

    void selectTracks(SmoothStreamingManifest smoothStreamingManifest, Output output) throws IOException;
}
