package org.telegram.messenger.exoplayer2.util;

public final class SystemClock implements Clock {
    public long elapsedRealtime() {
        return android.os.SystemClock.elapsedRealtime();
    }
}
