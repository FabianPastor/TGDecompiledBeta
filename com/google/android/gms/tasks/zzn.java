package com.google.android.gms.tasks;

import android.app.Activity;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbdr;
import com.google.android.gms.internal.zzbds;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

final class zzn<TResult> extends Task<TResult> {
    private final Object mLock = new Object();
    private final zzl<TResult> zzbMe = new zzl();
    private boolean zzbMf;
    private TResult zzbMg;
    private Exception zzbMh;

    static class zza extends zzbdr {
        private final List<WeakReference<zzk<?>>> mListeners = new ArrayList();

        private zza(zzbds com_google_android_gms_internal_zzbds) {
            super(com_google_android_gms_internal_zzbds);
            this.zzaEG.zza("TaskOnStopCallback", (zzbdr) this);
        }

        public static zza zzr(Activity activity) {
            zzbds zzn = zzbdr.zzn(activity);
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

    private final void zzDF() {
        zzbo.zza(this.zzbMf, (Object) "Task is not yet complete");
    }

    private final void zzDG() {
        zzbo.zza(!this.zzbMf, (Object) "Task is already complete");
    }

    private final void zzDH() {
        synchronized (this.mLock) {
            if (this.zzbMf) {
                this.zzbMe.zza((Task) this);
                return;
            }
        }
    }

    @NonNull
    public final Task<TResult> addOnCompleteListener(@NonNull Activity activity, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        zzk com_google_android_gms_tasks_zze = new zze(TaskExecutors.MAIN_THREAD, onCompleteListener);
        this.zzbMe.zza(com_google_android_gms_tasks_zze);
        zza.zzr(activity).zzb(com_google_android_gms_tasks_zze);
        zzDH();
        return this;
    }

    @NonNull
    public final Task<TResult> addOnCompleteListener(@NonNull OnCompleteListener<TResult> onCompleteListener) {
        return addOnCompleteListener(TaskExecutors.MAIN_THREAD, (OnCompleteListener) onCompleteListener);
    }

    @NonNull
    public final Task<TResult> addOnCompleteListener(@NonNull Executor executor, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        this.zzbMe.zza(new zze(executor, onCompleteListener));
        zzDH();
        return this;
    }

    @NonNull
    public final Task<TResult> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
        zzk com_google_android_gms_tasks_zzg = new zzg(TaskExecutors.MAIN_THREAD, onFailureListener);
        this.zzbMe.zza(com_google_android_gms_tasks_zzg);
        zza.zzr(activity).zzb(com_google_android_gms_tasks_zzg);
        zzDH();
        return this;
    }

    @NonNull
    public final Task<TResult> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        return addOnFailureListener(TaskExecutors.MAIN_THREAD, onFailureListener);
    }

    @NonNull
    public final Task<TResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        this.zzbMe.zza(new zzg(executor, onFailureListener));
        zzDH();
        return this;
    }

    @NonNull
    public final Task<TResult> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        zzk com_google_android_gms_tasks_zzi = new zzi(TaskExecutors.MAIN_THREAD, onSuccessListener);
        this.zzbMe.zza(com_google_android_gms_tasks_zzi);
        zza.zzr(activity).zzb(com_google_android_gms_tasks_zzi);
        zzDH();
        return this;
    }

    @NonNull
    public final Task<TResult> addOnSuccessListener(@NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        return addOnSuccessListener(TaskExecutors.MAIN_THREAD, (OnSuccessListener) onSuccessListener);
    }

    @NonNull
    public final Task<TResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        this.zzbMe.zza(new zzi(executor, onSuccessListener));
        zzDH();
        return this;
    }

    @NonNull
    public final <TContinuationResult> Task<TContinuationResult> continueWith(@NonNull Continuation<TResult, TContinuationResult> continuation) {
        return continueWith(TaskExecutors.MAIN_THREAD, continuation);
    }

    @NonNull
    public final <TContinuationResult> Task<TContinuationResult> continueWith(@NonNull Executor executor, @NonNull Continuation<TResult, TContinuationResult> continuation) {
        Task com_google_android_gms_tasks_zzn = new zzn();
        this.zzbMe.zza(new zza(executor, continuation, com_google_android_gms_tasks_zzn));
        zzDH();
        return com_google_android_gms_tasks_zzn;
    }

    @NonNull
    public final <TContinuationResult> Task<TContinuationResult> continueWithTask(@NonNull Continuation<TResult, Task<TContinuationResult>> continuation) {
        return continueWithTask(TaskExecutors.MAIN_THREAD, continuation);
    }

    @NonNull
    public final <TContinuationResult> Task<TContinuationResult> continueWithTask(@NonNull Executor executor, @NonNull Continuation<TResult, Task<TContinuationResult>> continuation) {
        Task com_google_android_gms_tasks_zzn = new zzn();
        this.zzbMe.zza(new zzc(executor, continuation, com_google_android_gms_tasks_zzn));
        zzDH();
        return com_google_android_gms_tasks_zzn;
    }

    @Nullable
    public final Exception getException() {
        Exception exception;
        synchronized (this.mLock) {
            exception = this.zzbMh;
        }
        return exception;
    }

    public final TResult getResult() {
        TResult tResult;
        synchronized (this.mLock) {
            zzDF();
            if (this.zzbMh != null) {
                throw new RuntimeExecutionException(this.zzbMh);
            }
            tResult = this.zzbMg;
        }
        return tResult;
    }

    public final <X extends Throwable> TResult getResult(@NonNull Class<X> cls) throws Throwable {
        TResult tResult;
        synchronized (this.mLock) {
            zzDF();
            if (cls.isInstance(this.zzbMh)) {
                throw ((Throwable) cls.cast(this.zzbMh));
            } else if (this.zzbMh != null) {
                throw new RuntimeExecutionException(this.zzbMh);
            } else {
                tResult = this.zzbMg;
            }
        }
        return tResult;
    }

    public final boolean isComplete() {
        boolean z;
        synchronized (this.mLock) {
            z = this.zzbMf;
        }
        return z;
    }

    public final boolean isSuccessful() {
        boolean z;
        synchronized (this.mLock) {
            z = this.zzbMf && this.zzbMh == null;
        }
        return z;
    }

    public final void setException(@NonNull Exception exception) {
        zzbo.zzb((Object) exception, (Object) "Exception must not be null");
        synchronized (this.mLock) {
            zzDG();
            this.zzbMf = true;
            this.zzbMh = exception;
        }
        this.zzbMe.zza((Task) this);
    }

    public final void setResult(TResult tResult) {
        synchronized (this.mLock) {
            zzDG();
            this.zzbMf = true;
            this.zzbMg = tResult;
        }
        this.zzbMe.zza((Task) this);
    }

    public final boolean trySetException(@NonNull Exception exception) {
        boolean z = true;
        zzbo.zzb((Object) exception, (Object) "Exception must not be null");
        synchronized (this.mLock) {
            if (this.zzbMf) {
                z = false;
            } else {
                this.zzbMf = true;
                this.zzbMh = exception;
                this.zzbMe.zza((Task) this);
            }
        }
        return z;
    }

    public final boolean trySetResult(TResult tResult) {
        boolean z = true;
        synchronized (this.mLock) {
            if (this.zzbMf) {
                z = false;
            } else {
                this.zzbMf = true;
                this.zzbMg = tResult;
                this.zzbMe.zza((Task) this);
            }
        }
        return z;
    }
}
