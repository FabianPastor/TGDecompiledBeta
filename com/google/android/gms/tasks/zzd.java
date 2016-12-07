package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzd<TResult> implements zzf<TResult> {
    private final Executor aEQ;
    private OnFailureListener aMM;
    private final Object zzako = new Object();

    public zzd(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        this.aEQ = executor;
        this.aMM = onFailureListener;
    }

    public void cancel() {
        synchronized (this.zzako) {
            this.aMM = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        if (!task.isSuccessful()) {
            synchronized (this.zzako) {
                if (this.aMM == null) {
                    return;
                }
                this.aEQ.execute(new Runnable(this) {
                    final /* synthetic */ zzd aMN;

                    public void run() {
                        synchronized (this.aMN.zzako) {
                            if (this.aMN.aMM != null) {
                                this.aMN.aMM.onFailure(task.getException());
                            }
                        }
                    }
                });
            }
        }
    }
}
