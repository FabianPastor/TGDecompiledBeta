package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzb<TResult, TContinuationResult> implements OnFailureListener, OnSuccessListener<TContinuationResult>, zzf<TResult> {
    private final Executor zzbFQ;
    private final Continuation<TResult, Task<TContinuationResult>> zzbNt;
    private final zzh<TContinuationResult> zzbNu;

    public zzb(@NonNull Executor executor, @NonNull Continuation<TResult, Task<TContinuationResult>> continuation, @NonNull zzh<TContinuationResult> com_google_android_gms_tasks_zzh_TContinuationResult) {
        this.zzbFQ = executor;
        this.zzbNt = continuation;
        this.zzbNu = com_google_android_gms_tasks_zzh_TContinuationResult;
    }

    public void cancel() {
        throw new UnsupportedOperationException();
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        this.zzbFQ.execute(new Runnable(this) {
            final /* synthetic */ zzb zzbNx;

            public void run() {
                try {
                    Task task = (Task) this.zzbNx.zzbNt.then(task);
                    if (task == null) {
                        this.zzbNx.onFailure(new NullPointerException("Continuation returned null"));
                        return;
                    }
                    task.addOnSuccessListener(TaskExecutors.zzbNH, this.zzbNx);
                    task.addOnFailureListener(TaskExecutors.zzbNH, this.zzbNx);
                } catch (Exception e) {
                    if (e.getCause() instanceof Exception) {
                        this.zzbNx.zzbNu.setException((Exception) e.getCause());
                    } else {
                        this.zzbNx.zzbNu.setException(e);
                    }
                } catch (Exception e2) {
                    this.zzbNx.zzbNu.setException(e2);
                }
            }
        });
    }

    public void onFailure(@NonNull Exception exception) {
        this.zzbNu.setException(exception);
    }

    public void onSuccess(TContinuationResult tContinuationResult) {
        this.zzbNu.setResult(tContinuationResult);
    }
}
