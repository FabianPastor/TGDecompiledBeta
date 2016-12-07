package com.google.android.gms.tasks;

import android.support.annotation.NonNull;

public class TaskCompletionSource<TResult> {
    private final zzh<TResult> aJH = new zzh();

    @NonNull
    public Task<TResult> getTask() {
        return this.aJH;
    }

    public void setException(@NonNull Exception exception) {
        this.aJH.setException(exception);
    }

    public void setResult(TResult tResult) {
        this.aJH.setResult(tResult);
    }
}
