package com.google.android.gms.tasks;

import android.support.annotation.NonNull;

public class TaskCompletionSource<TResult> {
    private final zzh<TResult> aMS = new zzh();

    @NonNull
    public Task<TResult> getTask() {
        return this.aMS;
    }

    public void setException(@NonNull Exception exception) {
        this.aMS.setException(exception);
    }

    public void setResult(TResult tResult) {
        this.aMS.setResult(tResult);
    }

    public boolean trySetException(@NonNull Exception exception) {
        return this.aMS.trySetException(exception);
    }

    public boolean trySetResult(TResult tResult) {
        return this.aMS.trySetResult(tResult);
    }
}
