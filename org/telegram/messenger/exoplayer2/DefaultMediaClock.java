package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.util.Clock;
import org.telegram.messenger.exoplayer2.util.MediaClock;
import org.telegram.messenger.exoplayer2.util.StandaloneMediaClock;

final class DefaultMediaClock implements MediaClock {
    private final PlaybackParameterListener listener;
    private MediaClock rendererClock;
    private Renderer rendererClockSource;
    private final StandaloneMediaClock standaloneMediaClock;

    public interface PlaybackParameterListener {
        void onPlaybackParametersChanged(PlaybackParameters playbackParameters);
    }

    public DefaultMediaClock(PlaybackParameterListener listener, Clock clock) {
        this.listener = listener;
        this.standaloneMediaClock = new StandaloneMediaClock(clock);
    }

    public void start() {
        this.standaloneMediaClock.start();
    }

    public void stop() {
        this.standaloneMediaClock.stop();
    }

    public void resetPosition(long positionUs) {
        this.standaloneMediaClock.resetPosition(positionUs);
    }

    public void onRendererEnabled(Renderer renderer) throws ExoPlaybackException {
        MediaClock rendererMediaClock = renderer.getMediaClock();
        if (rendererMediaClock != null && rendererMediaClock != this.rendererClock) {
            if (this.rendererClock != null) {
                throw ExoPlaybackException.createForUnexpected(new IllegalStateException("Multiple renderer media clocks enabled."));
            }
            this.rendererClock = rendererMediaClock;
            this.rendererClockSource = renderer;
            this.rendererClock.setPlaybackParameters(this.standaloneMediaClock.getPlaybackParameters());
            ensureSynced();
        }
    }

    public void onRendererDisabled(Renderer renderer) {
        if (renderer == this.rendererClockSource) {
            this.rendererClock = null;
            this.rendererClockSource = null;
        }
    }

    public long syncAndGetPositionUs() {
        if (!isUsingRendererClock()) {
            return this.standaloneMediaClock.getPositionUs();
        }
        ensureSynced();
        return this.rendererClock.getPositionUs();
    }

    public long getPositionUs() {
        if (isUsingRendererClock()) {
            return this.rendererClock.getPositionUs();
        }
        return this.standaloneMediaClock.getPositionUs();
    }

    public PlaybackParameters setPlaybackParameters(PlaybackParameters playbackParameters) {
        if (this.rendererClock != null) {
            playbackParameters = this.rendererClock.setPlaybackParameters(playbackParameters);
        }
        this.standaloneMediaClock.setPlaybackParameters(playbackParameters);
        this.listener.onPlaybackParametersChanged(playbackParameters);
        return playbackParameters;
    }

    public PlaybackParameters getPlaybackParameters() {
        if (this.rendererClock != null) {
            return this.rendererClock.getPlaybackParameters();
        }
        return this.standaloneMediaClock.getPlaybackParameters();
    }

    private void ensureSynced() {
        this.standaloneMediaClock.resetPosition(this.rendererClock.getPositionUs());
        PlaybackParameters playbackParameters = this.rendererClock.getPlaybackParameters();
        if (!playbackParameters.equals(this.standaloneMediaClock.getPlaybackParameters())) {
            this.standaloneMediaClock.setPlaybackParameters(playbackParameters);
            this.listener.onPlaybackParametersChanged(playbackParameters);
        }
    }

    private boolean isUsingRendererClock() {
        return (this.rendererClockSource == null || this.rendererClockSource.isEnded() || (!this.rendererClockSource.isReady() && this.rendererClockSource.hasReadStreamToEnd())) ? false : true;
    }
}
