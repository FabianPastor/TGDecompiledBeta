package org.telegram.messenger.exoplayer2;

import android.os.Looper;
import org.telegram.messenger.exoplayer2.source.MediaSource;

public interface ExoPlayer extends Player {
    @Deprecated
    public static final int REPEAT_MODE_ALL = 2;
    @Deprecated
    public static final int REPEAT_MODE_OFF = 0;
    @Deprecated
    public static final int REPEAT_MODE_ONE = 1;
    @Deprecated
    public static final int STATE_BUFFERING = 2;
    @Deprecated
    public static final int STATE_ENDED = 4;
    @Deprecated
    public static final int STATE_IDLE = 1;
    @Deprecated
    public static final int STATE_READY = 3;

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

    @Deprecated
    public interface EventListener extends org.telegram.messenger.exoplayer2.Player.EventListener {
    }

    void blockingSendMessages(ExoPlayerMessage... exoPlayerMessageArr);

    Looper getPlaybackLooper();

    void prepare(MediaSource mediaSource);

    void prepare(MediaSource mediaSource, boolean z, boolean z2);

    void sendMessages(ExoPlayerMessage... exoPlayerMessageArr);
}
