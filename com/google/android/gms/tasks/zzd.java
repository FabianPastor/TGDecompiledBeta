package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzd<TResult> implements zzf<TResult> {
    private final Executor zzbFQ;
    private OnFailureListener zzbNA;
    private final Object zzrJ = new Object();

    public zzd(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        this.zzbFQ = executor;
        this.zzbNA = onFailureListener;
    }

    public void cancel() {
        synchronized (this.zzrJ) {
            this.zzbNA = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        if (!task.isSuccessful()) {
            synchronized (this.zzrJ) {
                if (this.zzbNA == null) {
                    return;
                }
                this.zzbFQ.execute(new Runnable(this) {
                    final /* synthetic */ zzd zzbNB;

                    public void run() {
                        synchronized (this.zzbNB.zzrJ) {
                            if (this.zzbNB.zzbNA != null) {
                                this.zzbNB.zzbNA.onFailure(task.getException());
                            }
                        }
                    }
                });
            }
        }
    }
}
