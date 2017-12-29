package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class zzcih extends zzcjl {
    private static final AtomicLong zzjeo = new AtomicLong(Long.MIN_VALUE);
    private ExecutorService zzieo;
    private zzcil zzjef;
    private zzcil zzjeg;
    private final PriorityBlockingQueue<zzcik<?>> zzjeh = new PriorityBlockingQueue();
    private final BlockingQueue<zzcik<?>> zzjei = new LinkedBlockingQueue();
    private final UncaughtExceptionHandler zzjej = new zzcij(this, "Thread death: Uncaught exception on worker thread");
    private final UncaughtExceptionHandler zzjek = new zzcij(this, "Thread death: Uncaught exception on network thread");
    private final Object zzjel = new Object();
    private final Semaphore zzjem = new Semaphore(2);
    private volatile boolean zzjen;

    zzcih(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
    }

    private final void zza(zzcik<?> com_google_android_gms_internal_zzcik_) {
        synchronized (this.zzjel) {
            this.zzjeh.add(com_google_android_gms_internal_zzcik_);
            if (this.zzjef == null) {
                this.zzjef = new zzcil(this, "Measurement Worker", this.zzjeh);
                this.zzjef.setUncaughtExceptionHandler(this.zzjej);
                this.zzjef.start();
            } else {
                this.zzjef.zzrk();
            }
        }
    }

    public static boolean zzau() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final /* bridge */ /* synthetic */ void zzawi() {
        super.zzawi();
    }

    public final void zzawj() {
        if (Thread.currentThread() != this.zzjeg) {
            throw new IllegalStateException("Call expected from network thread");
        }
    }

    public final /* bridge */ /* synthetic */ zzcgd zzawk() {
        return super.zzawk();
    }

    public final /* bridge */ /* synthetic */ zzcgk zzawl() {
        return super.zzawl();
    }

    public final /* bridge */ /* synthetic */ zzcjn zzawm() {
        return super.zzawm();
    }

    public final /* bridge */ /* synthetic */ zzchh zzawn() {
        return super.zzawn();
    }

    public final /* bridge */ /* synthetic */ zzcgu zzawo() {
        return super.zzawo();
    }

    public final /* bridge */ /* synthetic */ zzckg zzawp() {
        return super.zzawp();
    }

    public final /* bridge */ /* synthetic */ zzckc zzawq() {
        return super.zzawq();
    }

    public final /* bridge */ /* synthetic */ zzchi zzawr() {
        return super.zzawr();
    }

    public final /* bridge */ /* synthetic */ zzcgo zzaws() {
        return super.zzaws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzawt() {
        return super.zzawt();
    }

    public final /* bridge */ /* synthetic */ zzclq zzawu() {
        return super.zzawu();
    }

    public final /* bridge */ /* synthetic */ zzcig zzawv() {
        return super.zzawv();
    }

    public final /* bridge */ /* synthetic */ zzclf zzaww() {
        return super.zzaww();
    }

    public final /* bridge */ /* synthetic */ zzcih zzawx() {
        return super.zzawx();
    }

    public final /* bridge */ /* synthetic */ zzchm zzawy() {
        return super.zzawy();
    }

    public final /* bridge */ /* synthetic */ zzchx zzawz() {
        return super.zzawz();
    }

    public final /* bridge */ /* synthetic */ zzcgn zzaxa() {
        return super.zzaxa();
    }

    protected final boolean zzaxz() {
        return false;
    }

    public final boolean zzazs() {
        return Thread.currentThread() == this.zzjef;
    }

    final ExecutorService zzazt() {
        ExecutorService executorService;
        synchronized (this.zzjel) {
            if (this.zzieo == null) {
                this.zzieo = new ThreadPoolExecutor(0, 1, 30, TimeUnit.SECONDS, new ArrayBlockingQueue(100));
            }
            executorService = this.zzieo;
        }
        return executorService;
    }

    public final <V> Future<V> zzc(Callable<V> callable) throws IllegalStateException {
        zzxf();
        zzbq.checkNotNull(callable);
        zzcik com_google_android_gms_internal_zzcik = new zzcik(this, (Callable) callable, false, "Task exception on worker thread");
        if (Thread.currentThread() == this.zzjef) {
            if (!this.zzjeh.isEmpty()) {
                zzawy().zzazf().log("Callable skipped the worker queue.");
            }
            com_google_android_gms_internal_zzcik.run();
        } else {
            zza(com_google_android_gms_internal_zzcik);
        }
        return com_google_android_gms_internal_zzcik;
    }

    public final <V> Future<V> zzd(Callable<V> callable) throws IllegalStateException {
        zzxf();
        zzbq.checkNotNull(callable);
        zzcik com_google_android_gms_internal_zzcik = new zzcik(this, (Callable) callable, true, "Task exception on worker thread");
        if (Thread.currentThread() == this.zzjef) {
            com_google_android_gms_internal_zzcik.run();
        } else {
            zza(com_google_android_gms_internal_zzcik);
        }
        return com_google_android_gms_internal_zzcik;
    }

    public final void zzg(Runnable runnable) throws IllegalStateException {
        zzxf();
        zzbq.checkNotNull(runnable);
        zza(new zzcik(this, runnable, false, "Task exception on worker thread"));
    }

    public final void zzh(Runnable runnable) throws IllegalStateException {
        zzxf();
        zzbq.checkNotNull(runnable);
        zzcik com_google_android_gms_internal_zzcik = new zzcik(this, runnable, false, "Task exception on network thread");
        synchronized (this.zzjel) {
            this.zzjei.add(com_google_android_gms_internal_zzcik);
            if (this.zzjeg == null) {
                this.zzjeg = new zzcil(this, "Measurement Network", this.zzjei);
                this.zzjeg.setUncaughtExceptionHandler(this.zzjek);
                this.zzjeg.start();
            } else {
                this.zzjeg.zzrk();
            }
        }
    }

    public final void zzve() {
        if (Thread.currentThread() != this.zzjef) {
            throw new IllegalStateException("Call expected from worker thread");
        }
    }

    public final /* bridge */ /* synthetic */ zzd zzws() {
        return super.zzws();
    }
}
