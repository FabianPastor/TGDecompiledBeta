package org.telegram.messenger.exoplayer2.source.ads;

import android.view.ViewGroup;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.source.ads.AdsMediaSource.AdLoadException;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;

public interface AdsLoader {

    public interface EventListener {
        void onAdClicked();

        void onAdLoadError(AdLoadException adLoadException, DataSpec dataSpec);

        void onAdPlaybackState(AdPlaybackState adPlaybackState);

        void onAdTapped();
    }

    void attachPlayer(ExoPlayer exoPlayer, EventListener eventListener, ViewGroup viewGroup);

    void detachPlayer();

    void handlePrepareError(int i, int i2, IOException iOException);

    void release();

    void setSupportedContentTypes(int... iArr);
}
