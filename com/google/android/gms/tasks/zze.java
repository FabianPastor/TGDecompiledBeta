package com.google.android.gms.tasks;

import java.util.concurrent.Executor;

final class zze<TResult> implements zzk<TResult> {
    private final Object mLock = new Object();
    private final Executor zzkev;
    private OnCompleteListener<TResult> zzkud;

    public zze(Executor executor, OnCompleteListener<TResult> onCompleteListener) {
        this.zzkev = executor;
        this.zzkud = onCompleteListener;
    }

    public final void onComplete(Task<TResult> task) {
        synchronized (this.mLock) {
            if (this.zzkud == null) {
                return;
            }
            this.zzkev.execute(new zzf(this, task));
        }
    }
}
