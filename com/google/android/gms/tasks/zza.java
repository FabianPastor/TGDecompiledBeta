package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zza<TResult, TContinuationResult> implements zzf<TResult> {
    private final Executor zzbDK;
    private final Continuation<TResult, TContinuationResult> zzbLs;
    private final zzh<TContinuationResult> zzbLt;

    public zza(@NonNull Executor executor, @NonNull Continuation<TResult, TContinuationResult> continuation, @NonNull zzh<TContinuationResult> com_google_android_gms_tasks_zzh_TContinuationResult) {
        this.zzbDK = executor;
        this.zzbLs = continuation;
        this.zzbLt = com_google_android_gms_tasks_zzh_TContinuationResult;
    }

    public void cancel() {
        throw new UnsupportedOperationException();
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        this.zzbDK.execute(new Runnable(this) {
            final /* synthetic */ zza zzbLv;

            public void run() {
                try {
                    this.zzbLv.zzbLt.setResult(this.zzbLv.zzbLs.then(task));
                } catch (Exception e) {
                    if (e.getCause() instanceof Exception) {
                        this.zzbLv.zzbLt.setException((Exception) e.getCause());
                    } else {
                        this.zzbLv.zzbLt.setException(e);
                    }
                } catch (Exception e2) {
                    this.zzbLv.zzbLt.setException(e2);
                }
            }
        });
    }
}
