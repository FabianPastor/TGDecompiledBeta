package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzb<TResult, TContinuationResult> implements OnFailureListener, OnSuccessListener<TContinuationResult>, zzf<TResult> {
    private final Executor zzbFM;
    private final Continuation<TResult, Task<TContinuationResult>> zzbNp;
    private final zzh<TContinuationResult> zzbNq;

    public zzb(@NonNull Executor executor, @NonNull Continuation<TResult, Task<TContinuationResult>> continuation, @NonNull zzh<TContinuationResult> com_google_android_gms_tasks_zzh_TContinuationResult) {
        this.zzbFM = executor;
        this.zzbNp = continuation;
        this.zzbNq = com_google_android_gms_tasks_zzh_TContinuationResult;
    }

    public void cancel() {
        throw new UnsupportedOperationException();
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        this.zzbFM.execute(new Runnable(this) {
            final /* synthetic */ zzb zzbNt;

            public void run() {
                try {
                    Task task = (Task) this.zzbNt.zzbNp.then(task);
                    if (task == null) {
                        this.zzbNt.onFailure(new NullPointerException("Continuation returned null"));
                        return;
                    }
                    task.addOnSuccessListener(TaskExecutors.zzbND, this.zzbNt);
                    task.addOnFailureListener(TaskExecutors.zzbND, this.zzbNt);
                } catch (Exception e) {
                    if (e.getCause() instanceof Exception) {
                        this.zzbNt.zzbNq.setException((Exception) e.getCause());
                    } else {
                        this.zzbNt.zzbNq.setException(e);
                    }
                } catch (Exception e2) {
                    this.zzbNt.zzbNq.setException(e2);
                }
            }
        });
    }

    public void onFailure(@NonNull Exception exception) {
        this.zzbNq.setException(exception);
    }

    public void onSuccess(TContinuationResult tContinuationResult) {
        this.zzbNq.setResult(tContinuationResult);
    }
}
