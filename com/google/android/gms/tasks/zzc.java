package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzc<TResult> implements zzf<TResult> {
    private final Executor zzbFQ;
    private OnCompleteListener<TResult> zzbNy;
    private final Object zzrJ = new Object();

    public zzc(@NonNull Executor executor, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        this.zzbFQ = executor;
        this.zzbNy = onCompleteListener;
    }

    public void cancel() {
        synchronized (this.zzrJ) {
            this.zzbNy = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        synchronized (this.zzrJ) {
            if (this.zzbNy == null) {
                return;
            }
            this.zzbFQ.execute(new Runnable(this) {
                final /* synthetic */ zzc zzbNz;

                public void run() {
                    synchronized (this.zzbNz.zzrJ) {
                        if (this.zzbNz.zzbNy != null) {
                            this.zzbNz.zzbNy.onComplete(task);
                        }
                    }
                }
            });
        }
    }
}
