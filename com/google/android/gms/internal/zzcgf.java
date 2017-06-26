package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class zzcgf extends zzchi {
    private static final AtomicLong zzbsf = new AtomicLong(Long.MIN_VALUE);
    private ExecutorService zzbrV;
    private zzcgj zzbrW;
    private zzcgj zzbrX;
    private final PriorityBlockingQueue<FutureTask<?>> zzbrY = new PriorityBlockingQueue();
    private final BlockingQueue<FutureTask<?>> zzbrZ = new LinkedBlockingQueue();
    private final UncaughtExceptionHandler zzbsa = new zzcgh(this, "Thread death: Uncaught exception on worker thread");
    private final UncaughtExceptionHandler zzbsb = new zzcgh(this, "Thread death: Uncaught exception on network thread");
    private final Object zzbsc = new Object();
    private final Semaphore zzbsd = new Semaphore(2);
    private volatile boolean zzbse;

    zzcgf(zzcgk com_google_android_gms_internal_zzcgk) {
        super(com_google_android_gms_internal_zzcgk);
    }

    public static boolean zzS() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private final void zza(zzcgi<?> com_google_android_gms_internal_zzcgi_) {
        synchronized (this.zzbsc) {
            this.zzbrY.add(com_google_android_gms_internal_zzcgi_);
            if (this.zzbrW == null) {
                this.zzbrW = new zzcgj(this, "Measurement Worker", this.zzbrY);
                this.zzbrW.setUncaughtExceptionHandler(this.zzbsa);
                this.zzbrW.start();
            } else {
                this.zzbrW.zzfF();
            }
        }
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final <V> Future<V> zze(Callable<V> callable) throws IllegalStateException {
        zzkD();
        zzbo.zzu(callable);
        zzcgi com_google_android_gms_internal_zzcgi = new zzcgi(this, (Callable) callable, false, "Task exception on worker thread");
        if (Thread.currentThread() == this.zzbrW) {
            if (!this.zzbrY.isEmpty()) {
                super.zzwF().zzyz().log("Callable skipped the worker queue.");
            }
            com_google_android_gms_internal_zzcgi.run();
        } else {
            zza(com_google_android_gms_internal_zzcgi);
        }
        return com_google_android_gms_internal_zzcgi;
    }

    public final <V> Future<V> zzf(Callable<V> callable) throws IllegalStateException {
        zzkD();
        zzbo.zzu(callable);
        zzcgi com_google_android_gms_internal_zzcgi = new zzcgi(this, (Callable) callable, true, "Task exception on worker thread");
        if (Thread.currentThread() == this.zzbrW) {
            com_google_android_gms_internal_zzcgi.run();
        } else {
            zza(com_google_android_gms_internal_zzcgi);
        }
        return com_google_android_gms_internal_zzcgi;
    }

    public final void zzj(Runnable runnable) throws IllegalStateException {
        zzkD();
        zzbo.zzu(runnable);
        zza(new zzcgi(this, runnable, false, "Task exception on worker thread"));
    }

    public final void zzjC() {
        if (Thread.currentThread() != this.zzbrW) {
            throw new IllegalStateException("Call expected from worker thread");
        }
    }

    protected final void zzjD() {
    }

    public final void zzk(Runnable runnable) throws IllegalStateException {
        zzkD();
        zzbo.zzu(runnable);
        zzcgi com_google_android_gms_internal_zzcgi = new zzcgi(this, runnable, false, "Task exception on network thread");
        synchronized (this.zzbsc) {
            this.zzbrZ.add(com_google_android_gms_internal_zzcgi);
            if (this.zzbrX == null) {
                this.zzbrX = new zzcgj(this, "Measurement Network", this.zzbrZ);
                this.zzbrX.setUncaughtExceptionHandler(this.zzbsb);
                this.zzbrX.start();
            } else {
                this.zzbrX.zzfF();
            }
        }
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    public final /* bridge */ /* synthetic */ zzcfi zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjk zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcge zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzciz zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfk zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfv zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcel zzwH() {
        return super.zzwH();
    }

    public final /* bridge */ /* synthetic */ void zzwo() {
        super.zzwo();
    }

    public final /* bridge */ /* synthetic */ void zzwp() {
        super.zzwp();
    }

    public final void zzwq() {
        if (Thread.currentThread() != this.zzbrX) {
            throw new IllegalStateException("Call expected from network thread");
        }
    }

    public final /* bridge */ /* synthetic */ zzceb zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcei zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcff zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzces zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcic zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchy zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwz() {
        return super.zzwz();
    }

    public final boolean zzyM() {
        return Thread.currentThread() == this.zzbrW;
    }

    final ExecutorService zzyN() {
        ExecutorService executorService;
        synchronized (this.zzbsc) {
            if (this.zzbrV == null) {
                this.zzbrV = new ThreadPoolExecutor(0, 1, 30, TimeUnit.SECONDS, new ArrayBlockingQueue(100));
            }
            executorService = this.zzbrV;
        }
        return executorService;
    }
}
