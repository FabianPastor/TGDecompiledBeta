package org.webrtc;

public class MediaSource {
    private long nativeSource;
    private final RefCountDelegate refCountDelegate;

    private static native State nativeGetState(long j);

    public enum State {
        INITIALIZING,
        LIVE,
        ENDED,
        MUTED;

        static State fromNativeIndex(int nativeIndex) {
            return values()[nativeIndex];
        }
    }

    public MediaSource(long nativeSource2) {
        this.refCountDelegate = new RefCountDelegate(new MediaSource$$ExternalSyntheticLambda0(nativeSource2));
        this.nativeSource = nativeSource2;
    }

    public State state() {
        checkMediaSourceExists();
        return nativeGetState(this.nativeSource);
    }

    public void dispose() {
        checkMediaSourceExists();
        this.refCountDelegate.release();
        this.nativeSource = 0;
    }

    /* access modifiers changed from: protected */
    public long getNativeMediaSource() {
        checkMediaSourceExists();
        return this.nativeSource;
    }

    /* access modifiers changed from: package-private */
    public void runWithReference(Runnable runnable) {
        if (this.refCountDelegate.safeRetain()) {
            try {
                runnable.run();
            } finally {
                this.refCountDelegate.release();
            }
        }
    }

    private void checkMediaSourceExists() {
        if (this.nativeSource == 0) {
            throw new IllegalStateException("MediaSource has been disposed.");
        }
    }
}
