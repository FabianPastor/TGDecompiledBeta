package com.google.android.gms.tasks;

import android.support.annotation.NonNull;

public class TaskCompletionSource<TResult> {
    private final zzn<TResult> zzbMe = new zzn();

    @NonNull
    public Task<TResult> getTask() {
        return this.zzbMe;
    }

    public void setException(@NonNull Exception exception) {
        this.zzbMe.setException(exception);
    }

    public void setResult(TResult tResult) {
        this.zzbMe.setResult(tResult);
    }

    public boolean trySetException(@NonNull Exception exception) {
        return this.zzbMe.trySetException(exception);
    }

    public boolean trySetResult(TResult tResult) {
        return this.zzbMe.trySetResult(tResult);
    }
}
