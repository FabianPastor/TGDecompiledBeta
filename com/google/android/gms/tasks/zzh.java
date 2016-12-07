package com.google.android.gms.tasks;

import android.app.Activity;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzra;
import com.google.android.gms.internal.zzrb;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

final class zzh<TResult> extends Task<TResult> {
    private final zzg<TResult> aJJ = new zzg();
    private boolean aJK;
    private TResult aJL;
    private Exception aJM;
    private final Object zzakd = new Object();

    private static class zza extends zzra {
        private final List<WeakReference<zzf<?>>> mListeners = new ArrayList();

        private zza(zzrb com_google_android_gms_internal_zzrb) {
            super(com_google_android_gms_internal_zzrb);
            this.yY.zza("TaskOnStopCallback", (zzra) this);
        }

        public static zza zzv(Activity activity) {
            zzrb zzs = zzra.zzs(activity);
            zza com_google_android_gms_tasks_zzh_zza = (zza) zzs.zza("TaskOnStopCallback", zza.class);
            return com_google_android_gms_tasks_zzh_zza == null ? new zza(zzs) : com_google_android_gms_tasks_zzh_zza;
        }

        @MainThread
        public void onStop() {
            synchronized (this.mListeners) {
                for (WeakReference weakReference : this.mListeners) {
                    zzf com_google_android_gms_tasks_zzf = (zzf) weakReference.get();
                    if (com_google_android_gms_tasks_zzf != null) {
                        com_google_android_gms_tasks_zzf.cancel();
                    }
                }
                this.mListeners.clear();
            }
        }

        public <T> void zzb(zzf<T> com_google_android_gms_tasks_zzf_T) {
            synchronized (this.mListeners) {
                this.mListeners.add(new WeakReference(com_google_android_gms_tasks_zzf_T));
            }
        }
    }

    zzh() {
    }

    private void zzclh() {
        zzac.zza(this.aJK, (Object) "Task is not yet complete");
    }

    private void zzcli() {
        zzac.zza(!this.aJK, (Object) "Task is already complete");
    }

    private void zzclj() {
        synchronized (this.zzakd) {
            if (this.aJK) {
                this.aJJ.zza((Task) this);
                return;
            }
        }
    }

    @NonNull
    public Task<TResult> addOnCompleteListener(@NonNull Activity activity, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        zzf com_google_android_gms_tasks_zzc = new zzc(TaskExecutors.MAIN_THREAD, onCompleteListener);
        this.aJJ.zza(com_google_android_gms_tasks_zzc);
        zza.zzv(activity).zzb(com_google_android_gms_tasks_zzc);
        zzclj();
        return this;
    }

    @NonNull
    public Task<TResult> addOnCompleteListener(@NonNull OnCompleteListener<TResult> onCompleteListener) {
        return addOnCompleteListener(TaskExecutors.MAIN_THREAD, (OnCompleteListener) onCompleteListener);
    }

    @NonNull
    public Task<TResult> addOnCompleteListener(@NonNull Executor executor, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        this.aJJ.zza(new zzc(executor, onCompleteListener));
        zzclj();
        return this;
    }

    @NonNull
    public Task<TResult> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
        zzf com_google_android_gms_tasks_zzd = new zzd(TaskExecutors.MAIN_THREAD, onFailureListener);
        this.aJJ.zza(com_google_android_gms_tasks_zzd);
        zza.zzv(activity).zzb(com_google_android_gms_tasks_zzd);
        zzclj();
        return this;
    }

    @NonNull
    public Task<TResult> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        return addOnFailureListener(TaskExecutors.MAIN_THREAD, onFailureListener);
    }

    @NonNull
    public Task<TResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        this.aJJ.zza(new zzd(executor, onFailureListener));
        zzclj();
        return this;
    }

    @NonNull
    public Task<TResult> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        zzf com_google_android_gms_tasks_zze = new zze(TaskExecutors.MAIN_THREAD, onSuccessListener);
        this.aJJ.zza(com_google_android_gms_tasks_zze);
        zza.zzv(activity).zzb(com_google_android_gms_tasks_zze);
        zzclj();
        return this;
    }

    @NonNull
    public Task<TResult> addOnSuccessListener(@NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        return addOnSuccessListener(TaskExecutors.MAIN_THREAD, (OnSuccessListener) onSuccessListener);
    }

    @NonNull
    public Task<TResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        this.aJJ.zza(new zze(executor, onSuccessListener));
        zzclj();
        return this;
    }

    @NonNull
    public <TContinuationResult> Task<TContinuationResult> continueWith(@NonNull Continuation<TResult, TContinuationResult> continuation) {
        return continueWith(TaskExecutors.MAIN_THREAD, continuation);
    }

    @NonNull
    public <TContinuationResult> Task<TContinuationResult> continueWith(@NonNull Executor executor, @NonNull Continuation<TResult, TContinuationResult> continuation) {
        Task com_google_android_gms_tasks_zzh = new zzh();
        this.aJJ.zza(new zza(executor, continuation, com_google_android_gms_tasks_zzh));
        zzclj();
        return com_google_android_gms_tasks_zzh;
    }

    @NonNull
    public <TContinuationResult> Task<TContinuationResult> continueWithTask(@NonNull Continuation<TResult, Task<TContinuationResult>> continuation) {
        return continueWithTask(TaskExecutors.MAIN_THREAD, continuation);
    }

    @NonNull
    public <TContinuationResult> Task<TContinuationResult> continueWithTask(@NonNull Executor executor, @NonNull Continuation<TResult, Task<TContinuationResult>> continuation) {
        Task com_google_android_gms_tasks_zzh = new zzh();
        this.aJJ.zza(new zzb(executor, continuation, com_google_android_gms_tasks_zzh));
        zzclj();
        return com_google_android_gms_tasks_zzh;
    }

    @Nullable
    public Exception getException() {
        Exception exception;
        synchronized (this.zzakd) {
            exception = this.aJM;
        }
        return exception;
    }

    public TResult getResult() {
        TResult tResult;
        synchronized (this.zzakd) {
            zzclh();
            if (this.aJM != null) {
                throw new RuntimeExecutionException(this.aJM);
            }
            tResult = this.aJL;
        }
        return tResult;
    }

    public <X extends Throwable> TResult getResult(@NonNull Class<X> cls) throws Throwable {
        TResult tResult;
        synchronized (this.zzakd) {
            zzclh();
            if (cls.isInstance(this.aJM)) {
                throw ((Throwable) cls.cast(this.aJM));
            } else if (this.aJM != null) {
                throw new RuntimeExecutionException(this.aJM);
            } else {
                tResult = this.aJL;
            }
        }
        return tResult;
    }

    public boolean isComplete() {
        boolean z;
        synchronized (this.zzakd) {
            z = this.aJK;
        }
        return z;
    }

    public boolean isSuccessful() {
        boolean z;
        synchronized (this.zzakd) {
            z = this.aJK && this.aJM == null;
        }
        return z;
    }

    public void setException(@NonNull Exception exception) {
        zzac.zzb((Object) exception, (Object) "Exception must not be null");
        synchronized (this.zzakd) {
            zzcli();
            this.aJK = true;
            this.aJM = exception;
        }
        this.aJJ.zza((Task) this);
    }

    public void setResult(TResult tResult) {
        synchronized (this.zzakd) {
            zzcli();
            this.aJK = true;
            this.aJL = tResult;
        }
        this.aJJ.zza((Task) this);
    }
}
