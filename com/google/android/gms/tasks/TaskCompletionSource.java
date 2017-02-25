package com.google.android.gms.tasks;

import android.support.annotation.NonNull;

public class TaskCompletionSource<TResult> {
    private final zzh<TResult> zzbNG = new zzh();

    @NonNull
    public Task<TResult> getTask() {
        return this.zzbNG;
    }

    public void setException(@NonNull Exception exception) {
        this.zzbNG.setException(exception);
    }

    public void setResult(TResult tResult) {
        this.zzbNG.setResult(tResult);
    }

    public boolean trySetException(@NonNull Exception exception) {
        return this.zzbNG.trySetException(exception);
    }

    public boolean trySetResult(TResult tResult) {
        return this.zzbNG.trySetResult(tResult);
    }
}
