package com.google.android.gms.tasks;

import android.support.annotation.NonNull;

public class TaskCompletionSource<TResult> {
    private final zzh<TResult> zzbNF = new zzh();

    @NonNull
    public Task<TResult> getTask() {
        return this.zzbNF;
    }

    public void setException(@NonNull Exception exception) {
        this.zzbNF.setException(exception);
    }

    public void setResult(TResult tResult) {
        this.zzbNF.setResult(tResult);
    }

    public boolean trySetException(@NonNull Exception exception) {
        return this.zzbNF.trySetException(exception);
    }

    public boolean trySetResult(TResult tResult) {
        return this.zzbNF.trySetResult(tResult);
    }
}
