package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zza<TResult, TContinuationResult> implements zzf<TResult> {
    private final Executor zzbFQ;
    private final Continuation<TResult, TContinuationResult> zzbNt;
    private final zzh<TContinuationResult> zzbNu;

    public zza(@NonNull Executor executor, @NonNull Continuation<TResult, TContinuationResult> continuation, @NonNull zzh<TContinuationResult> com_google_android_gms_tasks_zzh_TContinuationResult) {
        this.zzbFQ = executor;
        this.zzbNt = continuation;
        this.zzbNu = com_google_android_gms_tasks_zzh_TContinuationResult;
    }

    public void cancel() {
        throw new UnsupportedOperationException();
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        this.zzbFQ.execute(new Runnable(this) {
            final /* synthetic */ zza zzbNw;

            public void run() {
                try {
                    this.zzbNw.zzbNu.setResult(this.zzbNw.zzbNt.then(task));
                } catch (Exception e) {
                    if (e.getCause() instanceof Exception) {
                        this.zzbNw.zzbNu.setException((Exception) e.getCause());
                    } else {
                        this.zzbNw.zzbNu.setException(e);
                    }
                } catch (Exception e2) {
                    this.zzbNw.zzbNu.setException(e2);
                }
            }
        });
    }
}
