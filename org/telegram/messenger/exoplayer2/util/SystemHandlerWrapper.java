package org.telegram.messenger.exoplayer2.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

final class SystemHandlerWrapper implements HandlerWrapper {
    private final Handler handler;

    public SystemHandlerWrapper(Handler handler) {
        this.handler = handler;
    }

    public Looper getLooper() {
        return this.handler.getLooper();
    }

    public Message obtainMessage(int what) {
        return this.handler.obtainMessage(what);
    }

    public Message obtainMessage(int what, Object obj) {
        return this.handler.obtainMessage(what, obj);
    }

    public Message obtainMessage(int what, int arg1, int arg2) {
        return this.handler.obtainMessage(what, arg1, arg2);
    }

    public Message obtainMessage(int what, int arg1, int arg2, Object obj) {
        return this.handler.obtainMessage(what, arg1, arg2, obj);
    }

    public boolean sendEmptyMessage(int what) {
        return this.handler.sendEmptyMessage(what);
    }

    public boolean sendEmptyMessageAtTime(int what, long uptimeMs) {
        return this.handler.sendEmptyMessageAtTime(what, uptimeMs);
    }

    public void removeMessages(int what) {
        this.handler.removeMessages(what);
    }

    public void removeCallbacksAndMessages(Object token) {
        this.handler.removeCallbacksAndMessages(token);
    }

    public boolean post(Runnable runnable) {
        return this.handler.post(runnable);
    }

    public boolean postDelayed(Runnable runnable, long delayMs) {
        return this.handler.postDelayed(runnable, delayMs);
    }
}
