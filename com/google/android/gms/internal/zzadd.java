package com.google.android.gms.internal;

import android.os.Process;

class zzadd implements Runnable {
    private final int mPriority;
    private final Runnable zzw;

    public zzadd(Runnable runnable, int i) {
        this.zzw = runnable;
        this.mPriority = i;
    }

    public void run() {
        Process.setThreadPriority(this.mPriority);
        this.zzw.run();
    }
}
