package org.telegram.messenger.exoplayer2;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MediaClock;

public abstract class NoSampleRenderer implements Renderer, RendererCapabilities {
    private RendererConfiguration configuration;
    private int index;
    private int state;
    private SampleStream stream;
    private boolean streamIsFinal;

    public final int getTrackType() {
        return 5;
    }

    public final RendererCapabilities getCapabilities() {
        return this;
    }

    public final void setIndex(int index) {
        this.index = index;
    }

    public MediaClock getMediaClock() {
        return null;
    }

    public final int getState() {
        return this.state;
    }

    public final void enable(RendererConfiguration configuration, Format[] formats, SampleStream stream, long positionUs, boolean joining, long offsetUs) throws ExoPlaybackException {
        Assertions.checkState(this.state == 0);
        this.configuration = configuration;
        this.state = 1;
        onEnabled(joining);
        replaceStream(formats, stream, offsetUs);
        onPositionReset(positionUs, joining);
    }

    public final void start() throws ExoPlaybackException {
        boolean z = true;
        if (this.state != 1) {
            z = false;
        }
        Assertions.checkState(z);
        this.state = 2;
        onStarted();
    }

    public final void replaceStream(Format[] formats, SampleStream stream, long offsetUs) throws ExoPlaybackException {
        Assertions.checkState(!this.streamIsFinal);
        this.stream = stream;
        onRendererOffsetChanged(offsetUs);
    }

    public final SampleStream getStream() {
        return this.stream;
    }

    public final boolean hasReadStreamToEnd() {
        return true;
    }

    public final void setCurrentStreamFinal() {
        this.streamIsFinal = true;
    }

    public final boolean isCurrentStreamFinal() {
        return this.streamIsFinal;
    }

    public final void maybeThrowStreamError() throws IOException {
    }

    public final void resetPosition(long positionUs) throws ExoPlaybackException {
        this.streamIsFinal = false;
        onPositionReset(positionUs, false);
    }

    public final void stop() throws ExoPlaybackException {
        Assertions.checkState(this.state == 2);
        this.state = 1;
        onStopped();
    }

    public final void disable() {
        boolean z = true;
        if (this.state != 1) {
            z = false;
        }
        Assertions.checkState(z);
        this.state = 0;
        this.stream = null;
        this.streamIsFinal = false;
        onDisabled();
    }

    public boolean isReady() {
        return true;
    }

    public boolean isEnded() {
        return true;
    }

    public int supportsFormat(Format format) throws ExoPlaybackException {
        return 0;
    }

    public int supportsMixedMimeTypeAdaptation() throws ExoPlaybackException {
        return 0;
    }

    public void handleMessage(int what, Object object) throws ExoPlaybackException {
    }

    protected void onEnabled(boolean joining) throws ExoPlaybackException {
    }

    protected void onRendererOffsetChanged(long offsetUs) throws ExoPlaybackException {
    }

    protected void onPositionReset(long positionUs, boolean joining) throws ExoPlaybackException {
    }

    protected void onStarted() throws ExoPlaybackException {
    }

    protected void onStopped() throws ExoPlaybackException {
    }

    protected void onDisabled() {
    }

    protected final RendererConfiguration getConfiguration() {
        return this.configuration;
    }

    protected final int getIndex() {
        return this.index;
    }
}
