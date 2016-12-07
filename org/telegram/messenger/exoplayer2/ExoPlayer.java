package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.source.MediaSource;

public interface ExoPlayer {
    public static final int STATE_BUFFERING = 2;
    public static final int STATE_ENDED = 4;
    public static final int STATE_IDLE = 1;
    public static final int STATE_READY = 3;

    public interface EventListener {
        void onLoadingChanged(boolean z);

        void onPlayerError(ExoPlaybackException exoPlaybackException);

        void onPlayerStateChanged(boolean z, int i);

        void onPositionDiscontinuity();

        void onTimelineChanged(Timeline timeline, Object obj);
    }

    public interface ExoPlayerComponent {
        void handleMessage(int i, Object obj) throws ExoPlaybackException;
    }

    public static final class ExoPlayerMessage {
        public final Object message;
        public final int messageType;
        public final ExoPlayerComponent target;

        public ExoPlayerMessage(ExoPlayerComponent target, int messageType, Object message) {
            this.target = target;
            this.messageType = messageType;
            this.message = message;
        }
    }

    void addListener(EventListener eventListener);

    void blockingSendMessages(ExoPlayerMessage... exoPlayerMessageArr);

    int getBufferedPercentage();

    long getBufferedPosition();

    Object getCurrentManifest();

    int getCurrentPeriodIndex();

    long getCurrentPosition();

    Timeline getCurrentTimeline();

    int getCurrentWindowIndex();

    long getDuration();

    boolean getPlayWhenReady();

    int getPlaybackState();

    boolean isLoading();

    void prepare(MediaSource mediaSource);

    void prepare(MediaSource mediaSource, boolean z, boolean z2);

    void release();

    void removeListener(EventListener eventListener);

    void seekTo(int i, long j);

    void seekTo(long j);

    void seekToDefaultPosition();

    void seekToDefaultPosition(int i);

    void sendMessages(ExoPlayerMessage... exoPlayerMessageArr);

    void setPlayWhenReady(boolean z);

    void stop();
}
