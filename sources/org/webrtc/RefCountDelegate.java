package org.webrtc;

import java.util.concurrent.atomic.AtomicInteger;

class RefCountDelegate implements RefCounted {
    private final AtomicInteger refCount = new AtomicInteger(1);
    private final Runnable releaseCallback;

    public RefCountDelegate(Runnable releaseCallback2) {
        this.releaseCallback = releaseCallback2;
    }

    public void retain() {
        if (this.refCount.incrementAndGet() < 2) {
            throw new IllegalStateException("retain() called on an object with refcount < 1");
        }
    }

    public void release() {
        Runnable runnable;
        int updated_count = this.refCount.decrementAndGet();
        if (updated_count < 0) {
            throw new IllegalStateException("release() called on an object with refcount < 1");
        } else if (updated_count == 0 && (runnable = this.releaseCallback) != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean safeRetain() {
        int currentRefCount = this.refCount.get();
        while (currentRefCount != 0) {
            if (this.refCount.weakCompareAndSet(currentRefCount, currentRefCount + 1)) {
                return true;
            }
            currentRefCount = this.refCount.get();
        }
        return false;
    }
}
