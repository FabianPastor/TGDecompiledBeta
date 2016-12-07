package com.google.android.gms.tasks;

import android.support.annotation.NonNull;

public class TaskCompletionSource<TResult> {
    private final zzh<TResult> zzbLF = new zzh();

    @NonNull
    public Task<TResult> getTask() {
        return this.zzbLF;
    }

    public void setException(@NonNull Exception exception) {
        this.zzbLF.setException(exception);
    }

    public void setResult(TResult tResult) {
        this.zzbLF.setResult(tResult);
    }

    public boolean trySetException(@NonNull Exception exception) {
        return this.zzbLF.trySetException(exception);
    }

    public boolean trySetResult(TResult tResult) {
        return this.zzbLF.trySetResult(tResult);
    }
}
