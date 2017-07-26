package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

final class zzi<TResult> implements zzk<TResult> {
    private final Object mLock = new Object();
    private final Executor zzbEo;
    private OnSuccessListener<? super TResult> zzbMa;

    public zzi(@NonNull Executor executor, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        this.zzbEo = executor;
        this.zzbMa = onSuccessListener;
    }

    public final void cancel() {
        synchronized (this.mLock) {
            this.zzbMa = null;
        }
    }

    public final void onComplete(@NonNull Task<TResult> task) {
        if (task.isSuccessful()) {
            synchronized (this.mLock) {
                if (this.zzbMa == null) {
                    return;
                }
                this.zzbEo.execute(new zzj(this, task));
            }
        }
    }
}
