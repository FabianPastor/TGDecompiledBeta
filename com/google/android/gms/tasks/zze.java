package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zze<TResult> implements zzf<TResult> {
    private final Executor zzbFP;
    private OnSuccessListener<? super TResult> zzbNB;
    private final Object zzrJ = new Object();

    public zze(@NonNull Executor executor, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        this.zzbFP = executor;
        this.zzbNB = onSuccessListener;
    }

    public void cancel() {
        synchronized (this.zzrJ) {
            this.zzbNB = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        if (task.isSuccessful()) {
            synchronized (this.zzrJ) {
                if (this.zzbNB == null) {
                    return;
                }
                this.zzbFP.execute(new Runnable(this) {
                    final /* synthetic */ zze zzbNC;

                    public void run() {
                        synchronized (this.zzbNC.zzrJ) {
                            if (this.zzbNC.zzbNB != null) {
                                this.zzbNC.zzbNB.onSuccess(task.getResult());
                            }
                        }
                    }
                });
            }
        }
    }
}
