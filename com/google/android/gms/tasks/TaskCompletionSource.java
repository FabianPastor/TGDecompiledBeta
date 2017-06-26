package com.google.android.gms.tasks;

import android.support.annotation.NonNull;

public class TaskCompletionSource<TResult> {
    private final zzn<TResult> zzbMc = new zzn();

    @NonNull
    public Task<TResult> getTask() {
        return this.zzbMc;
    }

    public void setException(@NonNull Exception exception) {
        this.zzbMc.setException(exception);
    }

    public void setResult(TResult tResult) {
        this.zzbMc.setResult(tResult);
    }

    public boolean trySetException(@NonNull Exception exception) {
        return this.zzbMc.trySetException(exception);
    }

    public boolean trySetResult(TResult tResult) {
        return this.zzbMc.trySetResult(tResult);
    }
}
