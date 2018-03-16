package com.google.android.gms.tasks;

import com.google.android.gms.common.internal.zzbq;
import java.util.concurrent.Executor;

final class zzn<TResult> extends Task<TResult> {
    private final Object mLock = new Object();
    private final zzl<TResult> zzkun = new zzl();
    private boolean zzkuo;
    private TResult zzkup;
    private Exception zzkuq;

    zzn() {
    }

    private final void zzbjk() {
        zzbq.zza(this.zzkuo, "Task is not yet complete");
    }

    private final void zzbjl() {
        zzbq.zza(!this.zzkuo, "Task is already complete");
    }

    private final void zzbjm() {
        synchronized (this.mLock) {
            if (this.zzkuo) {
                this.zzkun.zzb(this);
                return;
            }
        }
    }

    public final Task<TResult> addOnCompleteListener(OnCompleteListener<TResult> onCompleteListener) {
        return addOnCompleteListener(TaskExecutors.MAIN_THREAD, onCompleteListener);
    }

    public final Task<TResult> addOnCompleteListener(Executor executor, OnCompleteListener<TResult> onCompleteListener) {
        this.zzkun.zza(new zze(executor, onCompleteListener));
        zzbjm();
        return this;
    }

    public final Task<TResult> addOnFailureListener(Executor executor, OnFailureListener onFailureListener) {
        this.zzkun.zza(new zzg(executor, onFailureListener));
        zzbjm();
        return this;
    }

    public final Task<TResult> addOnSuccessListener(Executor executor, OnSuccessListener<? super TResult> onSuccessListener) {
        this.zzkun.zza(new zzi(executor, onSuccessListener));
        zzbjm();
        return this;
    }

    public final Exception getException() {
        Exception exception;
        synchronized (this.mLock) {
            exception = this.zzkuq;
        }
        return exception;
    }

    public final TResult getResult() {
        TResult tResult;
        synchronized (this.mLock) {
            zzbjk();
            if (this.zzkuq != null) {
                throw new RuntimeExecutionException(this.zzkuq);
            }
            tResult = this.zzkup;
        }
        return tResult;
    }

    public final boolean isComplete() {
        boolean z;
        synchronized (this.mLock) {
            z = this.zzkuo;
        }
        return z;
    }

    public final boolean isSuccessful() {
        boolean z;
        synchronized (this.mLock) {
            z = this.zzkuo && this.zzkuq == null;
        }
        return z;
    }

    public final void setException(Exception exception) {
        zzbq.checkNotNull(exception, "Exception must not be null");
        synchronized (this.mLock) {
            zzbjl();
            this.zzkuo = true;
            this.zzkuq = exception;
        }
        this.zzkun.zzb(this);
    }

    public final void setResult(TResult tResult) {
        synchronized (this.mLock) {
            zzbjl();
            this.zzkuo = true;
            this.zzkup = tResult;
        }
        this.zzkun.zzb(this);
    }

    public final boolean trySetException(Exception exception) {
        boolean z = true;
        zzbq.checkNotNull(exception, "Exception must not be null");
        synchronized (this.mLock) {
            if (this.zzkuo) {
                z = false;
            } else {
                this.zzkuo = true;
                this.zzkuq = exception;
                this.zzkun.zzb(this);
            }
        }
        return z;
    }

    public final boolean trySetResult(TResult tResult) {
        boolean z = true;
        synchronized (this.mLock) {
            if (this.zzkuo) {
                z = false;
            } else {
                this.zzkuo = true;
                this.zzkup = tResult;
                this.zzkun.zzb(this);
            }
        }
        return z;
    }
}
