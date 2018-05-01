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
        boolean z;
        z = this.isOpen;
        this.isOpen = false;
        return z;
    }

    public synchronized void block() throws InterruptedException {
        while (!this.isOpen) {
            wait();
        }
    }

    public synchronized boolean block(long j) throws InterruptedException {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j2 = elapsedRealtime + j;
        while (this.isOpen == null && elapsedRealtime < j2) {
            wait(j2 - elapsedRealtime);
            elapsedRealtime = SystemClock.elapsedRealtime();
        }
        return this.isOpen;
    }
}
