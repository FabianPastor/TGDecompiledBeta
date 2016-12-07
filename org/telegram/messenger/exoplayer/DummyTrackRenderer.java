package org.telegram.messenger.exoplayer;

public final class DummyTrackRenderer extends TrackRenderer {
    protected boolean doPrepare(long positionUs) throws ExoPlaybackException {
        return true;
    }

    protected int getTrackCount() {
        return 0;
    }

    protected MediaFormat getFormat(int track) {
        throw new IllegalStateException();
    }

    protected boolean isEnded() {
        throw new IllegalStateException();
    }

    protected boolean isReady() {
        throw new IllegalStateException();
    }

    protected void seekTo(long positionUs) {
        throw new IllegalStateException();
    }

    protected void doSomeWork(long positionUs, long elapsedRealtimeUs) {
        throw new IllegalStateException();
    }

    protected void maybeThrowError() {
        throw new IllegalStateException();
    }

    protected long getDurationUs() {
        throw new IllegalStateException();
    }

    protected long getBufferedPositionUs() {
        throw new IllegalStateException();
    }
}
