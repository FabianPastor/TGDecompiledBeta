package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzc<TResult> implements zzf<TResult> {
    private final Executor zzbFM;
    private OnCompleteListener<TResult> zzbNu;
    private final Object zzrJ = new Object();

    public zzc(@NonNull Executor executor, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        this.zzbFM = executor;
        this.zzbNu = onCompleteListener;
    }

    public void cancel() {
        synchronized (this.zzrJ) {
            this.zzbNu = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        synchronized (this.zzrJ) {
            if (this.zzbNu == null) {
                return;
            }
            this.zzbFM.execute(new Runnable(this) {
                final /* synthetic */ zzc zzbNv;

                public void run() {
                    synchronized (this.zzbNv.zzrJ) {
                        if (this.zzbNv.zzbNu != null) {
                            this.zzbNv.zzbNu.onComplete(task);
                        }
                    }
                }
            });
        }
    }
}
