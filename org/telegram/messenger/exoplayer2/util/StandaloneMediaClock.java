package org.telegram.messenger.exoplayer2.util;

import org.telegram.messenger.exoplayer2.C;
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

    public void resetPosition(long positionUs) {
        this.baseUs = positionUs;
        if (this.started) {
            this.baseElapsedMs = this.clock.elapsedRealtime();
        }
    }

    public long getPositionUs() {
        long positionUs = this.baseUs;
        if (!this.started) {
            return positionUs;
        }
        long elapsedSinceBaseMs = this.clock.elapsedRealtime() - this.baseElapsedMs;
        if (this.playbackParameters.speed == 1.0f) {
            return positionUs + C.msToUs(elapsedSinceBaseMs);
        }
        return positionUs + this.playbackParameters.getMediaTimeUsForPlayoutTimeMs(elapsedSinceBaseMs);
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
