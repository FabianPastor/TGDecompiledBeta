package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzc<TResult> implements zzf<TResult> {
    private final Executor zzbDK;
    private OnCompleteListener<TResult> zzbLx;
    private final Object zzrN = new Object();

    public zzc(@NonNull Executor executor, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        this.zzbDK = executor;
        this.zzbLx = onCompleteListener;
    }

    public void cancel() {
        synchronized (this.zzrN) {
            this.zzbLx = null;
        }
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        synchronized (this.zzrN) {
            if (this.zzbLx == null) {
                return;
            }
            this.zzbDK.execute(new Runnable(this) {
                final /* synthetic */ zzc zzbLy;

                public void run() {
                    synchronized (this.zzbLy.zzrN) {
                        if (this.zzbLy.zzbLx != null) {
                            this.zzbLy.zzbLx.onComplete(task);
                        }
                    }
                }
            });
        }
    }
}
