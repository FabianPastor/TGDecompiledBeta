package org.telegram.messenger.exoplayer2.util;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;

final class SystemClock implements Clock {
    SystemClock() {
    }

    public long elapsedRealtime() {
        return android.os.SystemClock.elapsedRealtime();
    }

    public long uptimeMillis() {
        return android.os.SystemClock.uptimeMillis();
    }

    public void sleep(long sleepTimeMs) {
        android.os.SystemClock.sleep(sleepTimeMs);
    }

    public HandlerWrapper createHandler(Looper looper, Callback callback) {
        return new SystemHandlerWrapper(new Handler(looper, callback));
    }
}
