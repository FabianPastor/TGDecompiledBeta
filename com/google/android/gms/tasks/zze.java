package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zze<TResult> implements zzf<TResult> {
    private final Executor aBG;
    private OnSuccessListener<? super TResult> aJD;
    private final Object zzakd = new Object();

    public zze(@NonNull Executor executor, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        this.aBG = executor;
        this.aJD = onSuccessListener;
    }

    public void cancel() {
        synchronized (this.zzakd) {
            this.aJD = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        if (task.isSuccessful()) {
            synchronized (this.zzakd) {
                if (this.aJD == null) {
                    return;
                }
                this.aBG.execute(new Runnable(this) {
                    final /* synthetic */ zze aJE;

                    public void run() {
                        synchronized (this.aJE.zzakd) {
                            if (this.aJE.aJD != null) {
                                this.aJE.aJD.onSuccess(task.getResult());
                            }
                        }
                    }
                });
            }
        }
    }
}
