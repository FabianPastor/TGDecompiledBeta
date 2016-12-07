package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzd<TResult> implements zzf<TResult> {
    private final Executor aBG;
    private OnFailureListener aJB;
    private final Object zzakd = new Object();

    public zzd(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        this.aBG = executor;
        this.aJB = onFailureListener;
    }

    public void cancel() {
        synchronized (this.zzakd) {
            this.aJB = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        if (!task.isSuccessful()) {
            synchronized (this.zzakd) {
                if (this.aJB == null) {
                    return;
                }
                this.aBG.execute(new Runnable(this) {
                    final /* synthetic */ zzd aJC;

                    public void run() {
                        synchronized (this.aJC.zzakd) {
                            if (this.aJC.aJB != null) {
                                this.aJC.aJB.onFailure(task.getException());
                            }
                        }
                    }
                });
            }
        }
    }
}
