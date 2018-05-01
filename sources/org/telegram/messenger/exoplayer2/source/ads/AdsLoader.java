package org.telegram.messenger.exoplayer2.source.ads;

import android.view.ViewGroup;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.ExoPlayer;

public interface AdsLoader {

    public interface EventListener {
        void onAdClicked();

        void onAdPlaybackState(AdPlaybackState adPlaybackState);

        void onAdTapped();

        void onLoadError(IOException iOException);
    }

    void attachPlayer(ExoPlayer exoPlayer, EventListener eventListener, ViewGroup viewGroup);

    void detachPlayer();

    void release();

    void setSupportedContentTypes(int... iArr);
}
