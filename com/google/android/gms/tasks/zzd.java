package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzd<TResult> implements zzf<TResult> {
    private final Executor zzbDK;
    private OnFailureListener zzbLz;
    private final Object zzrN = new Object();

    public zzd(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        this.zzbDK = executor;
        this.zzbLz = onFailureListener;
    }

    public void cancel() {
        synchronized (this.zzrN) {
            this.zzbLz = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        if (!task.isSuccessful()) {
            synchronized (this.zzrN) {
                if (this.zzbLz == null) {
                    return;
                }
                this.zzbDK.execute(new Runnable(this) {
                    final /* synthetic */ zzd zzbLA;

                    public void run() {
                        synchronized (this.zzbLA.zzrN) {
                            if (this.zzbLA.zzbLz != null) {
                                this.zzbLA.zzbLz.onFailure(task.getException());
                            }
                        }
                    }
                });
            }
        }
    }
}
