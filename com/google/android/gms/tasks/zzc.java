package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzc<TResult> implements zzf<TResult> {
    private final Executor aEQ;
    private OnCompleteListener<TResult> aMK;
    private final Object zzako = new Object();

    public zzc(@NonNull Executor executor, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        this.aEQ = executor;
        this.aMK = onCompleteListener;
    }

    public void cancel() {
        synchronized (this.zzako) {
            this.aMK = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        synchronized (this.zzako) {
            if (this.aMK == null) {
                return;
            }
            this.aEQ.execute(new Runnable(this) {
                final /* synthetic */ zzc aML;

                public void run() {
                    synchronized (this.aML.zzako) {
                        if (this.aML.aMK != null) {
                            this.aML.aMK.onComplete(task);
                        }
                    }
                }
            });
        }
    }
}
