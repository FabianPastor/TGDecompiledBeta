package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.PlaybackParameters;

public interface AudioSink {
    public static final long CURRENT_POSITION_NOT_SET = Long.MIN_VALUE;

    public static final class ConfigurationException extends Exception {
        public ConfigurationException(Throwable cause) {
            super(cause);
        }

        public ConfigurationException(String message) {
            super(message);
        }
    }

    public static final class InitializationException extends Exception {
        public final int audioTrackState;

        public InitializationException(int audioTrackState, int sampleRate, int channelConfig, int bufferSize) {
            super("AudioTrack init failed: " + audioTrackState + ", Config(" + sampleRate + ", " + channelConfig + ", " + bufferSize + ")");
            this.audioTrackState = audioTrackState;
        }
    }

    public interface Listener {
        void onAudioSessionId(int i);

        void onPositionDiscontinuity();

        void onUnderrun(int i, long j, long j2);
    }

    public static final class WriteException extends Exception {
        public final int errorCode;

        public WriteException(int errorCode) {
            super("AudioTrack write failed: " + errorCode);
            this.errorCode = errorCode;
        }
    }

    void configure(int i, int i2, int i3, int i4, int[] iArr, int i5, int i6) throws ConfigurationException;

    void disableTunneling();

    void enableTunnelingV21(int i);

    long getCurrentPositionUs(boolean z);

    PlaybackParameters getPlaybackParameters();

    boolean handleBuffer(ByteBuffer byteBuffer, long j) throws InitializationException, WriteException;

    void handleDiscontinuity();

    boolean hasPendingData();

    boolean isEncodingSupported(int i);

    boolean isEnded();

    void pause();

    void play();

    void playToEndOfStream() throws WriteException;

    void release();

    void reset();

    void setAudioAttributes(AudioAttributes audioAttributes);

    void setAudioSessionId(int i);

    void setListener(Listener listener);

    PlaybackParameters setPlaybackParameters(PlaybackParameters playbackParameters);

    void setVolume(float f);
}
