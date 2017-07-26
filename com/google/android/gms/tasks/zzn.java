package com.google.android.gms.tasks;

import android.app.Activity;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbds;
import com.google.android.gms.internal.zzbdt;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

final class zzn<TResult> extends Task<TResult> {
    private final Object mLock = new Object();
    private final zzl<TResult> zzbMg = new zzl();
    private boolean zzbMh;
    private TResult zzbMi;
    private Exception zzbMj;

    static class zza extends zzbds {
        private final List<WeakReference<zzk<?>>> mListeners = new ArrayList();

        private zza(zzbdt com_google_android_gms_internal_zzbdt) {
            super(com_google_android_gms_internal_zzbdt);
            this.zzaEG.zza("TaskOnStopCallback", (zzbds) this);
        }

        public static zza zzr(Activity activity) {
            zzbdt zzn = zzbds.zzn(activity);
            zza com_google_android_gms_tasks_zzn_zza = (zza) zzn.zza("TaskOnStopCallback", zza.class);
            return com_google_android_gms_tasks_zzn_zza == null ? new zza(zzn) : com_google_android_gms_tasks_zzn_zza;
        }

        @MainThread
        public final void onStop() {
            synchronized (this.mListeners) {
                for (WeakReference weakReference : this.mListeners) {
                    zzk com_google_android_gms_tasks_zzk = (zzk) weakReference.get();
                    if (com_google_android_gms_tasks_zzk != null) {
                        com_google_android_gms_tasks_zzk.cancel();
                    }
                }
                this.mListeners.clear();
            }
        }

        public final <T> void zzb(zzk<T> com_google_android_gms_tasks_zzk_T) {
            synchronized (this.mListeners) {
                this.mListeners.add(new WeakReference(com_google_android_gms_tasks_zzk_T));
            }
        }
    }

    zzn() {
    }

    private final void zzDG() {
        zzbo.zza(this.zzbMh, (Object) "Task is not yet complete");
    }

    private final void zzDH() {
        zzbo.zza(!this.zzbMh, (Object) "Task is already complete");
    }

    private final void zzDI() {
        synchronized (this.mLock) {
            if (this.zzbMh) {
                this.zzbMg.zza((Task) this);
                return;
            }
        }
    }

    @NonNull
    public final Task<TResult> addOnCompleteListener(@NonNull Activity activity, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        zzk com_google_android_gms_tasks_zze = new zze(TaskExecutors.MAIN_THREAD, onCompleteListener);
        this.zzbMg.zza(com_google_android_gms_tasks_zze);
        zza.zzr(activity).zzb(com_google_android_gms_tasks_zze);
        zzDI();
        return this;
    }

    @NonNull
    public final Task<TResult> addOnCompleteListener(@NonNull OnCompleteListener<TResult> onCompleteListener) {
        return addOnCompleteListener(TaskExecutors.MAIN_THREAD, (OnCompleteListener) onCompleteListener);
    }

    @NonNull
    public final Task<TResult> addOnCompleteListener(@NonNull Executor executor, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        this.zzbMg.zza(new zze(executor, onCompleteListener));
        zzDI();
        return this;
    }

    @NonNull
    public final Task<TResult> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
        zzk com_google_android_gms_tasks_zzg = new zzg(TaskExecutors.MAIN_THREAD, onFailureListener);
        this.zzbMg.zza(com_google_android_gms_tasks_zzg);
        zza.zzr(activity).zzb(com_google_android_gms_tasks_zzg);
        zzDI();
        return this;
    }

    @NonNull
    public final Task<TResult> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        return addOnFailureListener(TaskExecutors.MAIN_THREAD, onFailureListener);
    }

    @NonNull
    public final Task<TResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        this.zzbMg.zza(new zzg(executor, onFailureListener));
        zzDI();
        return this;
    }

    @NonNull
    public final Task<TResult> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        zzk com_google_android_gms_tasks_zzi = new zzi(TaskExecutors.MAIN_THREAD, onSuccessListener);
        this.zzbMg.zza(com_google_android_gms_tasks_zzi);
        zza.zzr(activity).zzb(com_google_android_gms_tasks_zzi);
        zzDI();
        return this;
    }

    @NonNull
    public final Task<TResult> addOnSuccessListener(@NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        return addOnSuccessListener(TaskExecutors.MAIN_THREAD, (OnSuccessListener) onSuccessListener);
    }

    @NonNull
    public final Task<TResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        this.zzbMg.zza(new zzi(executor, onSuccessListener));
        zzDI();
        return this;
    }

    @NonNull
    public final <TContinuationResult> Task<TContinuationResult> continueWith(@NonNull Continuation<TResult, TContinuationResult> continuation) {
        return continueWith(TaskExecutors.MAIN_THREAD, continuation);
    }

    @NonNull
    public final <TContinuationResult> Task<TContinuationResult> continueWith(@NonNull Executor executor, @NonNull Continuation<TResult, TContinuationResult> continuation) {
        Task com_google_android_gms_tasks_zzn = new zzn();
        this.zzbMg.zza(new zza(executor, continuation, com_google_android_gms_tasks_zzn));
        zzDI();
        return com_google_android_gms_tasks_zzn;
    }

    @NonNull
    public final <TContinuationResult> Task<TContinuationResult> continueWithTask(@NonNull Continuation<TResult, Task<TContinuationResult>> continuation) {
        return continueWithTask(TaskExecutors.MAIN_THREAD, continuation);
    }

    @NonNull
    public final <TContinuationResult> Task<TContinuationResult> continueWithTask(@NonNull Executor executor, @NonNull Continuation<TResult, Task<TContinuationResult>> continuation) {
        Task com_google_android_gms_tasks_zzn = new zzn();
        this.zzbMg.zza(new zzc(executor, continuation, com_google_android_gms_tasks_zzn));
        zzDI();
        return com_google_android_gms_tasks_zzn;
    }

    @Nullable
    public final Exception getException() {
        Exception exception;
        synchronized (this.mLock) {
            exception = this.zzbMj;
        }
        return exception;
    }

    public final TResult getResult() {
        TResult tResult;
        synchronized (this.mLock) {
            zzDG();
            if (this.zzbMj != null) {
                throw new RuntimeExecutionException(this.zzbMj);
            }
            tResult = this.zzbMi;
        }
        return tResult;
    }

    public final <X extends Throwable> TResult getResult(@NonNull Class<X> cls) throws Throwable {
        TResult tResult;
        synchronized (this.mLock) {
            zzDG();
            if (cls.isInstance(this.zzbMj)) {
                throw ((Throwable) cls.cast(this.zzbMj));
            } else if (this.zzbMj != null) {
                throw new RuntimeExecutionException(this.zzbMj);
            } else {
                tResult = this.zzbMi;
            }
        }
        return tResult;
    }

    public final boolean isComplete() {
        boolean z;
        synchronized (this.mLock) {
            z = this.zzbMh;
        }
        return z;
    }

    public final boolean isSuccessful() {
        boolean z;
        synchronized (this.mLock) {
            z = this.zzbMh && this.zzbMj == null;
        }
        return z;
    }

    public final void setException(@NonNull Exception exception) {
        zzbo.zzb((Object) exception, (Object) "Exception must not be null");
        synchronized (this.mLock) {
            zzDH();
            this.zzbMh = true;
            this.zzbMj = exception;
        }
        this.zzbMg.zza((Task) this);
    }

    public final void setResult(TResult tResult) {
        synchronized (this.mLock) {
            zzDH();
            this.zzbMh = true;
            this.zzbMi = tResult;
        }
        this.zzbMg.zza((Task) this);
    }

    public final boolean trySetException(@NonNull Exception exception) {
        boolean z = true;
        zzbo.zzb((Object) exception, (Object) "Exception must not be null");
        synchronized (this.mLock) {
            if (this.zzbMh) {
                z = false;
            } else {
                this.zzbMh = true;
                this.zzbMj = exception;
                this.zzbMg.zza((Task) this);
            }
        }
        return z;
    }

    public final boolean trySetResult(TResult tResult) {
        boolean z = true;
        synchronized (this.mLock) {
            if (this.zzbMh) {
                z = false;
            } else {
                this.zzbMh = true;
                this.zzbMi = tResult;
                this.zzbMg.zza((Task) this);
            }
        }
        return z;
    }
}
