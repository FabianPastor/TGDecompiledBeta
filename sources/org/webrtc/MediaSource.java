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

        @CalledByNative("State")
        static State fromNativeIndex(int i) {
            return values()[i];
        }
    }

    public MediaSource(long j) {
        this.refCountDelegate = new RefCountDelegate(new MediaSource$$ExternalSyntheticLambda0(j));
        this.nativeSource = j;
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
