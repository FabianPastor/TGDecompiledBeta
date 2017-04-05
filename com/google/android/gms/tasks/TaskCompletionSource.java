package com.google.android.gms.tasks;

import android.support.annotation.NonNull;

public class TaskCompletionSource<TResult> {
    private final zzh<TResult> zzbNC = new zzh();

    @NonNull
    public Task<TResult> getTask() {
        return this.zzbNC;
    }

    public void setException(@NonNull Exception exception) {
        this.zzbNC.setException(exception);
    }

    public void setResult(TResult tResult) {
        this.zzbNC.setResult(tResult);
    }

    public boolean trySetException(@NonNull Exception exception) {
        return this.zzbNC.trySetException(exception);
    }

    public boolean trySetResult(TResult tResult) {
        return this.zzbNC.trySetResult(tResult);
    }
}
