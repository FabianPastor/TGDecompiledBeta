package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzd<TResult> implements zzf<TResult> {
    private final Executor zzbFM;
    private OnFailureListener zzbNw;
    private final Object zzrJ = new Object();

    public zzd(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        this.zzbFM = executor;
        this.zzbNw = onFailureListener;
    }

    public void cancel() {
        synchronized (this.zzrJ) {
            this.zzbNw = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        if (!task.isSuccessful()) {
            synchronized (this.zzrJ) {
                if (this.zzbNw == null) {
                    return;
                }
                this.zzbFM.execute(new Runnable(this) {
                    final /* synthetic */ zzd zzbNx;

                    public void run() {
                        synchronized (this.zzbNx.zzrJ) {
                            if (this.zzbNx.zzbNw != null) {
                                this.zzbNx.zzbNw.onFailure(task.getException());
                            }
                        }
                    }
                });
            }
        }
    }
}
