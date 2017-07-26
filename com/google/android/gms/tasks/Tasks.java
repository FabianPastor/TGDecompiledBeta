package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzbo;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class Tasks {

    interface zzb extends OnFailureListener, OnSuccessListener<Object> {
    }

    static final class zza implements zzb {
        private final CountDownLatch zztJ;

        private zza() {
            this.zztJ = new CountDownLatch(1);
        }

        public final void await() throws InterruptedException {
            this.zztJ.await();
        }

        public final boolean await(long j, TimeUnit timeUnit) throws InterruptedException {
            return this.zztJ.await(j, timeUnit);
        }

        public final void onFailure(@NonNull Exception exception) {
            this.zztJ.countDown();
        }

        public final void onSuccess(Object obj) {
            this.zztJ.countDown();
        }
    }

    static final class zzc implements zzb {
        private final Object mLock = new Object();
        private final zzn<Void> zzbMe;
        private Exception zzbMj;
        private final int zzbMl;
        private int zzbMm;
        private int zzbMn;

        public zzc(int i, zzn<Void> com_google_android_gms_tasks_zzn_java_lang_Void) {
            this.zzbMl = i;
            this.zzbMe = com_google_android_gms_tasks_zzn_java_lang_Void;
        }

        private final void zzDJ() {
            if (this.zzbMm + this.zzbMn != this.zzbMl) {
                return;
            }
            if (this.zzbMj == null) {
                this.zzbMe.setResult(null);
                return;
            }
            zzn com_google_android_gms_tasks_zzn = this.zzbMe;
            int i = this.zzbMn;
            com_google_android_gms_tasks_zzn.setException(new ExecutionException(i + " out of " + this.zzbMl + " underlying tasks failed", this.zzbMj));
        }

        public final void onFailure(@NonNull Exception exception) {
            synchronized (this.mLock) {
                this.zzbMn++;
                this.zzbMj = exception;
                zzDJ();
            }
        }

        public final void onSuccess(Object obj) {
            synchronized (this.mLock) {
                this.zzbMm++;
                zzDJ();
            }
        }
    }

    private Tasks() {
    }

    public static <TResult> TResult await(@NonNull Task<TResult> task) throws ExecutionException, InterruptedException {
        zzbo.zzcG("Must not be called on the main application thread");
        zzbo.zzb((Object) task, (Object) "Task must not be null");
        if (task.isComplete()) {
            return zzb(task);
        }
        Object com_google_android_gms_tasks_Tasks_zza = new zza();
        zza(task, com_google_android_gms_tasks_Tasks_zza);
        com_google_android_gms_tasks_Tasks_zza.await();
        return zzb(task);
    }

    public static <TResult> TResult await(@NonNull Task<TResult> task, long j, @NonNull TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
        zzbo.zzcG("Must not be called on the main application thread");
        zzbo.zzb((Object) task, (Object) "Task must not be null");
        zzbo.zzb((Object) timeUnit, (Object) "TimeUnit must not be null");
        if (task.isComplete()) {
            return zzb(task);
        }
        Object com_google_android_gms_tasks_Tasks_zza = new zza();
        zza(task, com_google_android_gms_tasks_Tasks_zza);
        if (com_google_android_gms_tasks_Tasks_zza.await(j, timeUnit)) {
            return zzb(task);
        }
        throw new TimeoutException("Timed out waiting for Task");
    }

    public static <TResult> Task<TResult> call(@NonNull Callable<TResult> callable) {
        return call(TaskExecutors.MAIN_THREAD, callable);
    }

    public static <TResult> Task<TResult> call(@NonNull Executor executor, @NonNull Callable<TResult> callable) {
        zzbo.zzb((Object) executor, (Object) "Executor must not be null");
        zzbo.zzb((Object) callable, (Object) "Callback must not be null");
        Task com_google_android_gms_tasks_zzn = new zzn();
        executor.execute(new zzo(com_google_android_gms_tasks_zzn, callable));
        return com_google_android_gms_tasks_zzn;
    }

    public static <TResult> Task<TResult> forException(@NonNull Exception exception) {
        Task com_google_android_gms_tasks_zzn = new zzn();
        com_google_android_gms_tasks_zzn.setException(exception);
        return com_google_android_gms_tasks_zzn;
    }

    public static <TResult> Task<TResult> forResult(TResult tResult) {
        Task com_google_android_gms_tasks_zzn = new zzn();
        com_google_android_gms_tasks_zzn.setResult(tResult);
        return com_google_android_gms_tasks_zzn;
    }

    public static Task<Void> whenAll(Collection<? extends Task<?>> collection) {
        if (collection.isEmpty()) {
            return forResult(null);
        }
        for (Task task : collection) {
            if (task == null) {
                throw new NullPointerException("null tasks are not accepted");
            }
        }
        Task com_google_android_gms_tasks_zzn = new zzn();
        zzb com_google_android_gms_tasks_Tasks_zzc = new zzc(collection.size(), com_google_android_gms_tasks_zzn);
        for (Task task2 : collection) {
            zza(task2, com_google_android_gms_tasks_Tasks_zzc);
        }
        return com_google_android_gms_tasks_zzn;
    }

    public static Task<Void> whenAll(Task<?>... taskArr) {
        return taskArr.length == 0 ? forResult(null) : whenAll(Arrays.asList(taskArr));
    }

    private static void zza(Task<?> task, zzb com_google_android_gms_tasks_Tasks_zzb) {
        task.addOnSuccessListener(TaskExecutors.zzbMf, (OnSuccessListener) com_google_android_gms_tasks_Tasks_zzb);
        task.addOnFailureListener(TaskExecutors.zzbMf, (OnFailureListener) com_google_android_gms_tasks_Tasks_zzb);
    }

    private static <TResult> TResult zzb(Task<TResult> task) throws ExecutionException {
        if (task.isSuccessful()) {
            return task.getResult();
        }
        throw new ExecutionException(task.getException());
    }
}
