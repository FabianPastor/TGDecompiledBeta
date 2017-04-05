package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class Tasks {

    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Callable zzXy;
        final /* synthetic */ zzh zzbNI;

        AnonymousClass1(zzh com_google_android_gms_tasks_zzh, Callable callable) {
            this.zzbNI = com_google_android_gms_tasks_zzh;
            this.zzXy = callable;
        }

        public void run() {
            try {
                this.zzbNI.setResult(this.zzXy.call());
            } catch (Exception e) {
                this.zzbNI.setException(e);
            }
        }
    }

    interface zzb extends OnFailureListener, OnSuccessListener<Object> {
    }

    private static final class zza implements zzb {
        private final CountDownLatch zztj;

        private zza() {
            this.zztj = new CountDownLatch(1);
        }

        public void await() throws InterruptedException {
            this.zztj.await();
        }

        public boolean await(long j, TimeUnit timeUnit) throws InterruptedException {
            return this.zztj.await(j, timeUnit);
        }

        public void onFailure(@NonNull Exception exception) {
            this.zztj.countDown();
        }

        public void onSuccess(Object obj) {
            this.zztj.countDown();
        }
    }

    private static final class zzc implements zzb {
        private final zzh<Void> zzbNC;
        private Exception zzbNH;
        private final int zzbNJ;
        private int zzbNK;
        private int zzbNL;
        private final Object zzrJ = new Object();

        public zzc(int i, zzh<Void> com_google_android_gms_tasks_zzh_java_lang_Void) {
            this.zzbNJ = i;
            this.zzbNC = com_google_android_gms_tasks_zzh_java_lang_Void;
        }

        private void zzTJ() {
            if (this.zzbNK + this.zzbNL != this.zzbNJ) {
                return;
            }
            if (this.zzbNH == null) {
                this.zzbNC.setResult(null);
                return;
            }
            zzh com_google_android_gms_tasks_zzh = this.zzbNC;
            int i = this.zzbNL;
            com_google_android_gms_tasks_zzh.setException(new ExecutionException(i + " out of " + this.zzbNJ + " underlying tasks failed", this.zzbNH));
        }

        public void onFailure(@NonNull Exception exception) {
            synchronized (this.zzrJ) {
                this.zzbNL++;
                this.zzbNH = exception;
                zzTJ();
            }
        }

        public void onSuccess(Object obj) {
            synchronized (this.zzrJ) {
                this.zzbNK++;
                zzTJ();
            }
        }
    }

    private Tasks() {
    }

    public static <TResult> TResult await(@NonNull Task<TResult> task) throws ExecutionException, InterruptedException {
        zzac.zzye();
        zzac.zzb((Object) task, (Object) "Task must not be null");
        if (task.isComplete()) {
            return zzb(task);
        }
        Object com_google_android_gms_tasks_Tasks_zza = new zza();
        zza(task, com_google_android_gms_tasks_Tasks_zza);
        com_google_android_gms_tasks_Tasks_zza.await();
        return zzb(task);
    }

    public static <TResult> TResult await(@NonNull Task<TResult> task, long j, @NonNull TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
        zzac.zzye();
        zzac.zzb((Object) task, (Object) "Task must not be null");
        zzac.zzb((Object) timeUnit, (Object) "TimeUnit must not be null");
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
        zzac.zzb((Object) executor, (Object) "Executor must not be null");
        zzac.zzb((Object) callable, (Object) "Callback must not be null");
        Task com_google_android_gms_tasks_zzh = new zzh();
        executor.execute(new AnonymousClass1(com_google_android_gms_tasks_zzh, callable));
        return com_google_android_gms_tasks_zzh;
    }

    public static <TResult> Task<TResult> forException(@NonNull Exception exception) {
        Task com_google_android_gms_tasks_zzh = new zzh();
        com_google_android_gms_tasks_zzh.setException(exception);
        return com_google_android_gms_tasks_zzh;
    }

    public static <TResult> Task<TResult> forResult(TResult tResult) {
        Task com_google_android_gms_tasks_zzh = new zzh();
        com_google_android_gms_tasks_zzh.setResult(tResult);
        return com_google_android_gms_tasks_zzh;
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
        Task com_google_android_gms_tasks_zzh = new zzh();
        zzb com_google_android_gms_tasks_Tasks_zzc = new zzc(collection.size(), com_google_android_gms_tasks_zzh);
        for (Task task2 : collection) {
            zza(task2, com_google_android_gms_tasks_Tasks_zzc);
        }
        return com_google_android_gms_tasks_zzh;
    }

    public static Task<Void> whenAll(Task<?>... taskArr) {
        return taskArr.length == 0 ? forResult(null) : whenAll(Arrays.asList(taskArr));
    }

    private static void zza(Task<?> task, zzb com_google_android_gms_tasks_Tasks_zzb) {
        task.addOnSuccessListener(TaskExecutors.zzbND, (OnSuccessListener) com_google_android_gms_tasks_Tasks_zzb);
        task.addOnFailureListener(TaskExecutors.zzbND, (OnFailureListener) com_google_android_gms_tasks_Tasks_zzb);
    }

    private static <TResult> TResult zzb(Task<TResult> task) throws ExecutionException {
        if (task.isSuccessful()) {
            return task.getResult();
        }
        throw new ExecutionException(task.getException());
    }
}
