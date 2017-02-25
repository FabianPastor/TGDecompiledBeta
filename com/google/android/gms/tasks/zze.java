package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zze<TResult> implements zzf<TResult> {
    private final Executor zzbFQ;
    private OnSuccessListener<? super TResult> zzbNC;
    private final Object zzrJ = new Object();

    public zze(@NonNull Executor executor, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        this.zzbFQ = executor;
        this.zzbNC = onSuccessListener;
    }

    public void cancel() {
        synchronized (this.zzrJ) {
            this.zzbNC = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        if (task.isSuccessful()) {
            synchronized (this.zzrJ) {
                if (this.zzbNC == null) {
                    return;
                }
                this.zzbFQ.execute(new Runnable(this) {
                    final /* synthetic */ zze zzbND;

                    public void run() {
                        synchronized (this.zzbND.zzrJ) {
                            if (this.zzbND.zzbNC != null) {
                                this.zzbND.zzbNC.onSuccess(task.getResult());
                            }
                        }
                    }
                });
            }
        }
    }
}
