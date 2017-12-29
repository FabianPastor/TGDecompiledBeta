package com.google.android.gms.tasks;

import java.util.concurrent.Executor;

final class zzg<TResult> implements zzk<TResult> {
    private final Object mLock = new Object();
    private final Executor zzkev;
    private OnFailureListener zzkuf;

    public zzg(Executor executor, OnFailureListener onFailureListener) {
        this.zzkev = executor;
        this.zzkuf = onFailureListener;
    }

    public final void onComplete(Task<TResult> task) {
        if (!task.isSuccessful()) {
            synchronized (this.mLock) {
                if (this.zzkuf == null) {
                    return;
                }
                this.zzkev.execute(new zzh(this, task));
            }
        }
    }
}
