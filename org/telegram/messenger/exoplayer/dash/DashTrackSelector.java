package org.telegram.messenger.exoplayer.dash;

import java.io.IOException;
import org.telegram.messenger.exoplayer.dash.mpd.MediaPresentationDescription;

public interface DashTrackSelector {

    public interface Output {
        void adaptiveTrack(MediaPresentationDescription mediaPresentationDescription, int i, int i2, int[] iArr);

        void fixedTrack(MediaPresentationDescription mediaPresentationDescription, int i, int i2, int i3);
    }

    void selectTracks(MediaPresentationDescription mediaPresentationDescription, int i, Output output) throws IOException;
}
