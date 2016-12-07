package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zza<TResult, TContinuationResult> implements zzf<TResult> {
    private final Executor aBG;
    private final Continuation<TResult, TContinuationResult> aJu;
    private final zzh<TContinuationResult> aJv;

    public zza(@NonNull Executor executor, @NonNull Continuation<TResult, TContinuationResult> continuation, @NonNull zzh<TContinuationResult> com_google_android_gms_tasks_zzh_TContinuationResult) {
        this.aBG = executor;
        this.aJu = continuation;
        this.aJv = com_google_android_gms_tasks_zzh_TContinuationResult;
    }

    public void cancel() {
        throw new UnsupportedOperationException();
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        this.aBG.execute(new Runnable(this) {
            final /* synthetic */ zza aJx;

            public void run() {
                try {
                    this.aJx.aJv.setResult(this.aJx.aJu.then(task));
                } catch (Exception e) {
                    if (e.getCause() instanceof Exception) {
                        this.aJx.aJv.setException((Exception) e.getCause());
                    } else {
                        this.aJx.aJv.setException(e);
                    }
                } catch (Exception e2) {
                    this.aJx.aJv.setException(e2);
                }
            }
        });
    }
}
