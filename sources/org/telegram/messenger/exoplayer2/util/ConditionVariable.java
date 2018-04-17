package org.telegram.messenger.exoplayer2.util;

import android.os.SystemClock;

public final class ConditionVariable {
    private boolean isOpen;

    public synchronized boolean open() {
        if (this.isOpen) {
            return false;
        }
        this.isOpen = true;
        notifyAll();
        return true;
    }

    public synchronized boolean close() {
        boolean wasOpen;
        wasOpen = this.isOpen;
        this.isOpen = false;
        return wasOpen;
    }

    public synchronized void block() throws InterruptedException {
        while (!this.isOpen) {
            wait();
        }
    }

    public synchronized boolean block(long timeout) throws InterruptedException {
        long now = SystemClock.elapsedRealtime();
        long end = now + timeout;
        while (!this.isOpen && now < end) {
            wait(end - now);
            now = SystemClock.elapsedRealtime();
        }
        return this.isOpen;
    }
}
