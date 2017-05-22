package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzc<TResult> implements zzf<TResult> {
    private final Executor zzbFP;
    private OnCompleteListener<TResult> zzbNx;
    private final Object zzrJ = new Object();

    public zzc(@NonNull Executor executor, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        this.zzbFP = executor;
        this.zzbNx = onCompleteListener;
    }

    public void cancel() {
        synchronized (this.zzrJ) {
            this.zzbNx = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        synchronized (this.zzrJ) {
            if (this.zzbNx == null) {
                return;
            }
            this.zzbFP.execute(new Runnable(this) {
                final /* synthetic */ zzc zzbNy;

                public void run() {
                    synchronized (this.zzbNy.zzrJ) {
                        if (this.zzbNy.zzbNx != null) {
                            this.zzbNy.zzbNx.onComplete(task);
                        }
                    }
                }
            });
        }
    }
}
