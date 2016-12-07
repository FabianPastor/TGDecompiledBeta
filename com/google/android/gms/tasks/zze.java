package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zze<TResult> implements zzf<TResult> {
    private final Executor aEQ;
    private OnSuccessListener<? super TResult> aMO;
    private final Object zzako = new Object();

    public zze(@NonNull Executor executor, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        this.aEQ = executor;
        this.aMO = onSuccessListener;
    }

    public void cancel() {
        synchronized (this.zzako) {
            this.aMO = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        if (task.isSuccessful()) {
            synchronized (this.zzako) {
                if (this.aMO == null) {
                    return;
                }
                this.aEQ.execute(new Runnable(this) {
                    final /* synthetic */ zze aMP;

                    public void run() {
                        synchronized (this.aMP.zzako) {
                            if (this.aMP.aMO != null) {
                                this.aMP.aMO.onSuccess(task.getResult());
                            }
                        }
                    }
                });
            }
        }
    }
}
