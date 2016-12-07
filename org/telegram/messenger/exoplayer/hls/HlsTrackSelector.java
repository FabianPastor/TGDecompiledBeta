package org.telegram.messenger.exoplayer.hls;

import java.io.IOException;

public interface HlsTrackSelector {

    public interface Output {
        void adaptiveTrack(HlsMasterPlaylist hlsMasterPlaylist, Variant[] variantArr);

        void fixedTrack(HlsMasterPlaylist hlsMasterPlaylist, Variant variant);
    }

    void selectTracks(HlsMasterPlaylist hlsMasterPlaylist, Output output) throws IOException;
}
