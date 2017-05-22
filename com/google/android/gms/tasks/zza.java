package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zza<TResult, TContinuationResult> implements zzf<TResult> {
    private final Executor zzbFP;
    private final Continuation<TResult, TContinuationResult> zzbNs;
    private final zzh<TContinuationResult> zzbNt;

    public zza(@NonNull Executor executor, @NonNull Continuation<TResult, TContinuationResult> continuation, @NonNull zzh<TContinuationResult> com_google_android_gms_tasks_zzh_TContinuationResult) {
        this.zzbFP = executor;
        this.zzbNs = continuation;
        this.zzbNt = com_google_android_gms_tasks_zzh_TContinuationResult;
    }

    public void cancel() {
        throw new UnsupportedOperationException();
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        this.zzbFP.execute(new Runnable(this) {
            final /* synthetic */ zza zzbNv;

            public void run() {
                try {
                    this.zzbNv.zzbNt.setResult(this.zzbNv.zzbNs.then(task));
                } catch (Exception e) {
                    if (e.getCause() instanceof Exception) {
                        this.zzbNv.zzbNt.setException((Exception) e.getCause());
                    } else {
                        this.zzbNv.zzbNt.setException(e);
                    }
                } catch (Exception e2) {
                    this.zzbNv.zzbNt.setException(e2);
                }
            }
        });
    }
}
