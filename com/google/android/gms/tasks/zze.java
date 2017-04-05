package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zze<TResult> implements zzf<TResult> {
    private final Executor zzbFM;
    private OnSuccessListener<? super TResult> zzbNy;
    private final Object zzrJ = new Object();

    public zze(@NonNull Executor executor, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        this.zzbFM = executor;
        this.zzbNy = onSuccessListener;
    }

    public void cancel() {
        synchronized (this.zzrJ) {
            this.zzbNy = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        if (task.isSuccessful()) {
            synchronized (this.zzrJ) {
                if (this.zzbNy == null) {
                    return;
                }
                this.zzbFM.execute(new Runnable(this) {
                    final /* synthetic */ zze zzbNz;

                    public void run() {
                        synchronized (this.zzbNz.zzrJ) {
                            if (this.zzbNz.zzbNy != null) {
                                this.zzbNz.zzbNy.onSuccess(task.getResult());
                            }
                        }
                    }
                });
            }
        }
    }
}
