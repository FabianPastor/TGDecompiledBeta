package com.google.android.gms.tasks;

import java.util.concurrent.Executor;

final class zzi<TResult> implements zzk<TResult> {
    private final Object mLock = new Object();
    private final Executor zzkev;
    private OnSuccessListener<? super TResult> zzkuh;

    public zzi(Executor executor, OnSuccessListener<? super TResult> onSuccessListener) {
        this.zzkev = executor;
        this.zzkuh = onSuccessListener;
    }

    public final void onComplete(Task<TResult> task) {
        if (task.isSuccessful()) {
            synchronized (this.mLock) {
                if (this.zzkuh == null) {
                    return;
                }
                this.zzkev.execute(new zzj(this, task));
            }
        }
    }
}
