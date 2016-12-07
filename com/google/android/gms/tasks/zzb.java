package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzb<TResult, TContinuationResult> implements OnFailureListener, OnSuccessListener<TContinuationResult>, zzf<TResult> {
    private final Executor aBG;
    private final Continuation<TResult, Task<TContinuationResult>> aJu;
    private final zzh<TContinuationResult> aJv;

    public zzb(@NonNull Executor executor, @NonNull Continuation<TResult, Task<TContinuationResult>> continuation, @NonNull zzh<TContinuationResult> com_google_android_gms_tasks_zzh_TContinuationResult) {
        this.aBG = executor;
        this.aJu = continuation;
        this.aJv = com_google_android_gms_tasks_zzh_TContinuationResult;
    }

    public void cancel() {
        throw new UnsupportedOperationException();
    }

    public void onComplete(@NonNull final Task<TResult> task) {
        this.aBG.execute(new Runnable(this) {
            final /* synthetic */ zzb aJy;

            public void run() {
                try {
                    Task task = (Task) this.aJy.aJu.then(task);
                    if (task == null) {
                        this.aJy.onFailure(new NullPointerException("Continuation returned null"));
                        return;
                    }
                    task.addOnSuccessListener(TaskExecutors.aJI, this.aJy);
                    task.addOnFailureListener(TaskExecutors.aJI, this.aJy);
                } catch (Exception e) {
                    if (e.getCause() instanceof Exception) {
                        this.aJy.aJv.setException((Exception) e.getCause());
                    } else {
                        this.aJy.aJv.setException(e);
                    }
                } catch (Exception e2) {
                    this.aJy.aJv.setException(e2);
                }
            }
        });
    }

    public void onFailure(@NonNull Exception exception) {
        this.aJv.setException(exception);
    }

    public void onSuccess(TContinuationResult tContinuationResult) {
        this.aJv.setResult(tContinuationResult);
    }
}
