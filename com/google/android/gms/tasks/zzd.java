package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzd<TResult> implements zzf<TResult> {
    private final Executor zzbFP;
    private OnFailureListener zzbNz;
    private final Object zzrJ = new Object();

    public zzd(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        this.zzbFP = executor;
        this.zzbNz = onFailureListener;
    }

    public void cancel() {
        synchronized (this.zzrJ) {
            this.zzbNz = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        if (!task.isSuccessful()) {
            synchronized (this.zzrJ) {
                if (this.zzbNz == null) {
                    return;
                }
                this.zzbFP.execute(new Runnable(this) {
                    final /* synthetic */ zzd zzbNA;

                    public void run() {
                        synchronized (this.zzbNA.zzrJ) {
                            if (this.zzbNA.zzbNz != null) {
                                this.zzbNA.zzbNz.onFailure(task.getException());
                            }
                        }
                    }
                });
            }
        }
    }
}
