package com.google.android.gms.tasks;

import android.app.Activity;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.internal.zzro;
import com.google.android.gms.internal.zzrp;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

final class zzh<TResult> extends Task<TResult> {
    private final zzg<TResult> aMU = new zzg();
    private boolean aMV;
    private TResult aMW;
    private Exception aMX;
    private final Object zzako = new Object();

    private static class zza extends zzro {
        private final List<WeakReference<zzf<?>>> mListeners = new ArrayList();

        private zza(zzrp com_google_android_gms_internal_zzrp) {
            super(com_google_android_gms_internal_zzrp);
            this.Bf.zza("TaskOnStopCallback", (zzro) this);
        }

        public static zza zzw(Activity activity) {
            zzrp zzs = zzro.zzs(activity);
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

    private void zzclg() {
        zzaa.zza(this.aMV, (Object) "Task is not yet complete");
    }

    private void zzclh() {
        zzaa.zza(!this.aMV, (Object) "Task is already complete");
    }

    private void zzcli() {
        synchronized (this.zzako) {
            if (this.aMV) {
                this.aMU.zza((Task) this);
                return;
            }
        }
    }

    @NonNull
    public Task<TResult> addOnCompleteListener(@NonNull Activity activity, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        zzf com_google_android_gms_tasks_zzc = new zzc(TaskExecutors.MAIN_THREAD, onCompleteListener);
        this.aMU.zza(com_google_android_gms_tasks_zzc);
        zza.zzw(activity).zzb(com_google_android_gms_tasks_zzc);
        zzcli();
        return this;
    }

    @NonNull
    public Task<TResult> addOnCompleteListener(@NonNull OnCompleteListener<TResult> onCompleteListener) {
        return addOnCompleteListener(TaskExecutors.MAIN_THREAD, (OnCompleteListener) onCompleteListener);
    }

    @NonNull
    public Task<TResult> addOnCompleteListener(@NonNull Executor executor, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        this.aMU.zza(new zzc(executor, onCompleteListener));
        zzcli();
        return this;
    }

    @NonNull
    public Task<TResult> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
        zzf com_google_android_gms_tasks_zzd = new zzd(TaskExecutors.MAIN_THREAD, onFailureListener);
        this.aMU.zza(com_google_android_gms_tasks_zzd);
        zza.zzw(activity).zzb(com_google_android_gms_tasks_zzd);
        zzcli();
        return this;
    }

    @NonNull
    public Task<TResult> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        return addOnFailureListener(TaskExecutors.MAIN_THREAD, onFailureListener);
    }

    @NonNull
    public Task<TResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        this.aMU.zza(new zzd(executor, onFailureListener));
        zzcli();
        return this;
    }

    @NonNull
    public Task<TResult> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        zzf com_google_android_gms_tasks_zze = new zze(TaskExecutors.MAIN_THREAD, onSuccessListener);
        this.aMU.zza(com_google_android_gms_tasks_zze);
        zza.zzw(activity).zzb(com_google_android_gms_tasks_zze);
        zzcli();
        return this;
    }

    @NonNull
    public Task<TResult> addOnSuccessListener(@NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        return addOnSuccessListener(TaskExecutors.MAIN_THREAD, (OnSuccessListener) onSuccessListener);
    }

    @NonNull
    public Task<TResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        this.aMU.zza(new zze(executor, onSuccessListener));
        zzcli();
        return this;
    }

    @NonNull
    public <TContinuationResult> Task<TContinuationResult> continueWith(@NonNull Continuation<TResult, TContinuationResult> continuation) {
        return continueWith(TaskExecutors.MAIN_THREAD, continuation);
    }

    @NonNull
    public <TContinuationResult> Task<TContinuationResult> continueWith(@NonNull Executor executor, @NonNull Continuation<TResult, TContinuationResult> continuation) {
        Task com_google_android_gms_tasks_zzh = new zzh();
        this.aMU.zza(new zza(executor, continuation, com_google_android_gms_tasks_zzh));
        zzcli();
        return com_google_android_gms_tasks_zzh;
    }

    @NonNull
    public <TContinuationResult> Task<TContinuationResult> continueWithTask(@NonNull Continuation<TResult, Task<TContinuationResult>> continuation) {
        return continueWithTask(TaskExecutors.MAIN_THREAD, continuation);
    }

    @NonNull
    public <TContinuationResult> Task<TContinuationResult> continueWithTask(@NonNull Executor executor, @NonNull Continuation<TResult, Task<TContinuationResult>> continuation) {
        Task com_google_android_gms_tasks_zzh = new zzh();
        this.aMU.zza(new zzb(executor, continuation, com_google_android_gms_tasks_zzh));
        zzcli();
        return com_google_android_gms_tasks_zzh;
    }

    @Nullable
    public Exception getException() {
        Exception exception;
        synchronized (this.zzako) {
            exception = this.aMX;
        }
        return exception;
    }

    public TResult getResult() {
        TResult tResult;
        synchronized (this.zzako) {
            zzclg();
            if (this.aMX != null) {
                throw new RuntimeExecutionException(this.aMX);
            }
            tResult = this.aMW;
        }
        return tResult;
    }

    public <X extends Throwable> TResult getResult(@NonNull Class<X> cls) throws Throwable {
        TResult tResult;
        synchronized (this.zzako) {
            zzclg();
            if (cls.isInstance(this.aMX)) {
                throw ((Throwable) cls.cast(this.aMX));
            } else if (this.aMX != null) {
                throw new RuntimeExecutionException(this.aMX);
            } else {
                tResult = this.aMW;
            }
        }
        return tResult;
    }

    public boolean isComplete() {
        boolean z;
        synchronized (this.zzako) {
            z = this.aMV;
        }
        return z;
    }

    public boolean isSuccessful() {
        boolean z;
        synchronized (this.zzako) {
            z = this.aMV && this.aMX == null;
        }
        return z;
    }

    public void setException(@NonNull Exception exception) {
        zzaa.zzb((Object) exception, (Object) "Exception must not be null");
        synchronized (this.zzako) {
            zzclh();
            this.aMV = true;
            this.aMX = exception;
        }
        this.aMU.zza((Task) this);
    }

    public void setResult(TResult tResult) {
        synchronized (this.zzako) {
            zzclh();
            this.aMV = true;
            this.aMW = tResult;
        }
        this.aMU.zza((Task) this);
    }

    public boolean trySetException(@NonNull Exception exception) {
        boolean z = true;
        zzaa.zzb((Object) exception, (Object) "Exception must not be null");
        synchronized (this.zzako) {
            if (this.aMV) {
                z = false;
            } else {
                this.aMV = true;
                this.aMX = exception;
                this.aMU.zza((Task) this);
            }
        }
        return z;
    }

    public boolean trySetResult(TResult tResult) {
        boolean z = true;
        synchronized (this.zzako) {
            if (this.aMV) {
                z = false;
            } else {
                this.aMV = true;
                this.aMW = tResult;
                this.aMU.zza((Task) this);
            }
        }
        return z;
    }
}
