package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzc<TResult> implements zzf<TResult> {
    private final Executor aBG;
    private OnCompleteListener<TResult> aJz;
    private final Object zzakd = new Object();

    public zzc(@NonNull Executor executor, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        this.aBG = executor;
        this.aJz = onCompleteListener;
    }

    public void cancel() {
        synchronized (this.zzakd) {
            this.aJz = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        synchronized (this.zzakd) {
            if (this.aJz == null) {
                return;
            }
            this.aBG.execute(new Runnable(this) {
                final /* synthetic */ zzc aJA;

                public void run() {
                    synchronized (this.aJA.zzakd) {
                        if (this.aJA.aJz != null) {
                            this.aJA.aJz.onComplete(task);
                        }
                    }
                }
            });
        }
    }
}
