package org.telegram.messenger.exoplayer2.util;

import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.PlaybackParameters;

public final class StandaloneMediaClock implements MediaClock {
    private long baseElapsedMs;
    private long baseUs;
    private final Clock clock;
    private PlaybackParameters playbackParameters = PlaybackParameters.DEFAULT;
    private boolean started;

    public StandaloneMediaClock(Clock clock) {
        this.clock = clock;
    }

    public void start() {
        if (!this.started) {
            this.baseElapsedMs = this.clock.elapsedRealtime();
            this.started = true;
        }
    }

    public void stop() {
        if (this.started) {
            resetPosition(getPositionUs());
            this.started = false;
        }
    }

    public void resetPosition(long j) {
        this.baseUs = j;
        if (this.started != null) {
            this.baseElapsedMs = this.clock.elapsedRealtime();
        }
    }

    public long getPositionUs() {
        long j = this.baseUs;
        if (!this.started) {
            return j;
        }
        long elapsedRealtime = this.clock.elapsedRealtime() - this.baseElapsedMs;
        if (this.playbackParameters.speed == 1.0f) {
            return j + C0542C.msToUs(elapsedRealtime);
        }
        return j + this.playbackParameters.getMediaTimeUsForPlayoutTimeMs(elapsedRealtime);
    }

    public PlaybackParameters setPlaybackParameters(PlaybackParameters playbackParameters) {
        if (this.started) {
            resetPosition(getPositionUs());
        }
        this.playbackParameters = playbackParameters;
        return playbackParameters;
    }

    public PlaybackParameters getPlaybackParameters() {
        return this.playbackParameters;
    }
}
