package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zze;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

public class zzw extends zzaa {
    private static final AtomicLong atP = new AtomicLong(Long.MIN_VALUE);
    private zzd atG;
    private zzd atH;
    private final PriorityBlockingQueue<FutureTask<?>> atI = new PriorityBlockingQueue();
    private final BlockingQueue<FutureTask<?>> atJ = new LinkedBlockingQueue();
    private final UncaughtExceptionHandler atK = new zzb(this, "Thread death: Uncaught exception on worker thread");
    private final UncaughtExceptionHandler atL = new zzb(this, "Thread death: Uncaught exception on network thread");
    private final Object atM = new Object();
    private final Semaphore atN = new Semaphore(2);
    private volatile boolean atO;

    static class zza extends RuntimeException {
    }

    private final class zzb implements UncaughtExceptionHandler {
        private final String atQ;
        final /* synthetic */ zzw atR;

        public zzb(zzw com_google_android_gms_measurement_internal_zzw, String str) {
            this.atR = com_google_android_gms_measurement_internal_zzw;
            zzaa.zzy(str);
            this.atQ = str;
        }

        public synchronized void uncaughtException(Thread thread, Throwable th) {
            this.atR.zzbwb().zzbwy().zzj(this.atQ, th);
        }
    }

    private final class zzc<V> extends FutureTask<V> implements Comparable<zzc> {
        private final String atQ;
        final /* synthetic */ zzw atR;
        private final long atS = zzw.atP.getAndIncrement();
        private final boolean atT;

        zzc(zzw com_google_android_gms_measurement_internal_zzw, Runnable runnable, boolean z, String str) {
            this.atR = com_google_android_gms_measurement_internal_zzw;
            super(runnable, null);
            zzaa.zzy(str);
            this.atQ = str;
            this.atT = z;
            if (this.atS == Long.MAX_VALUE) {
                com_google_android_gms_measurement_internal_zzw.zzbwb().zzbwy().log("Tasks index overflow");
            }
        }

        zzc(zzw com_google_android_gms_measurement_internal_zzw, Callable<V> callable, boolean z, String str) {
            this.atR = com_google_android_gms_measurement_internal_zzw;
            super(callable);
            zzaa.zzy(str);
            this.atQ = str;
            this.atT = z;
            if (this.atS == Long.MAX_VALUE) {
                com_google_android_gms_measurement_internal_zzw.zzbwb().zzbwy().log("Tasks index overflow");
            }
        }

        public /* synthetic */ int compareTo(@NonNull Object obj) {
            return zzb((zzc) obj);
        }

        protected void setException(Throwable th) {
            this.atR.zzbwb().zzbwy().zzj(this.atQ, th);
            if (th instanceof zza) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), th);
            }
            super.setException(th);
        }

        public int zzb(@NonNull zzc com_google_android_gms_measurement_internal_zzw_zzc) {
            if (this.atT != com_google_android_gms_measurement_internal_zzw_zzc.atT) {
                return this.atT ? -1 : 1;
            } else {
                if (this.atS < com_google_android_gms_measurement_internal_zzw_zzc.atS) {
                    return -1;
                }
                if (this.atS > com_google_android_gms_measurement_internal_zzw_zzc.atS) {
                    return 1;
                }
                this.atR.zzbwb().zzbwz().zzj("Two tasks share the same index. index", Long.valueOf(this.atS));
                return 0;
            }
        }
    }

    private final class zzd extends Thread {
        final /* synthetic */ zzw atR;
        private final Object atU = new Object();
        private final BlockingQueue<FutureTask<?>> atV;

        public zzd(zzw com_google_android_gms_measurement_internal_zzw, String str, BlockingQueue<FutureTask<?>> blockingQueue) {
            this.atR = com_google_android_gms_measurement_internal_zzw;
            zzaa.zzy(str);
            zzaa.zzy(blockingQueue);
            this.atV = blockingQueue;
            setName(str);
        }

        private void zza(InterruptedException interruptedException) {
            this.atR.zzbwb().zzbxa().zzj(String.valueOf(getName()).concat(" was interrupted"), interruptedException);
        }

        public void run() {
            Object obj = null;
            while (obj == null) {
                try {
                    this.atR.atN.acquire();
                    obj = 1;
                } catch (InterruptedException e) {
                    zza(e);
                }
            }
            while (true) {
                try {
                    FutureTask futureTask = (FutureTask) this.atV.poll();
                    if (futureTask != null) {
                        futureTask.run();
                    } else {
                        synchronized (this.atU) {
                            if (this.atV.peek() == null && !this.atR.atO) {
                                try {
                                    this.atU.wait(30000);
                                } catch (InterruptedException e2) {
                                    zza(e2);
                                }
                            }
                        }
                        synchronized (this.atR.atM) {
                            if (this.atV.peek() == null) {
                                break;
                            }
                        }
                    }
                } catch (Throwable th) {
                    synchronized (this.atR.atM) {
                        this.atR.atN.release();
                        this.atR.atM.notifyAll();
                        if (this == this.atR.atG) {
                            this.atR.atG = null;
                        } else if (this == this.atR.atH) {
                            this.atR.atH = null;
                        } else {
                            this.atR.zzbwb().zzbwy().log("Current scheduler thread is neither worker nor network");
                        }
                    }
                }
            }
            synchronized (this.atR.atM) {
                this.atR.atN.release();
                this.atR.atM.notifyAll();
                if (this == this.atR.atG) {
                    this.atR.atG = null;
                } else if (this == this.atR.atH) {
                    this.atR.atH = null;
                } else {
                    this.atR.zzbwb().zzbwy().log("Current scheduler thread is neither worker nor network");
                }
            }
        }

        public void zzpi() {
            synchronized (this.atU) {
                this.atU.notifyAll();
            }
        }
    }

    zzw(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    private void zza(zzc<?> com_google_android_gms_measurement_internal_zzw_zzc_) {
        synchronized (this.atM) {
            this.atI.add(com_google_android_gms_measurement_internal_zzw_zzc_);
            if (this.atG == null) {
                this.atG = new zzd(this, "Measurement Worker", this.atI);
                this.atG.setUncaughtExceptionHandler(this.atK);
                this.atG.start();
            } else {
                this.atG.zzpi();
            }
        }
    }

    private void zza(FutureTask<?> futureTask) {
        synchronized (this.atM) {
            this.atJ.add(futureTask);
            if (this.atH == null) {
                this.atH = new zzd(this, "Measurement Network", this.atJ);
                this.atH.setUncaughtExceptionHandler(this.atL);
                this.atH.start();
            } else {
                this.atH.zzpi();
            }
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzaby() {
        super.zzaby();
    }

    public /* bridge */ /* synthetic */ zze zzabz() {
        return super.zzabz();
    }

    public void zzbvo() {
        if (Thread.currentThread() != this.atH) {
            throw new IllegalStateException("Call expected from network thread");
        }
    }

    public /* bridge */ /* synthetic */ zzc zzbvp() {
        return super.zzbvp();
    }

    public /* bridge */ /* synthetic */ zzac zzbvq() {
        return super.zzbvq();
    }

    public /* bridge */ /* synthetic */ zzn zzbvr() {
        return super.zzbvr();
    }

    public /* bridge */ /* synthetic */ zzg zzbvs() {
        return super.zzbvs();
    }

    public /* bridge */ /* synthetic */ zzae zzbvt() {
        return super.zzbvt();
    }

    public /* bridge */ /* synthetic */ zzad zzbvu() {
        return super.zzbvu();
    }

    public /* bridge */ /* synthetic */ zzo zzbvv() {
        return super.zzbvv();
    }

    public /* bridge */ /* synthetic */ zze zzbvw() {
        return super.zzbvw();
    }

    public /* bridge */ /* synthetic */ zzal zzbvx() {
        return super.zzbvx();
    }

    public /* bridge */ /* synthetic */ zzv zzbvy() {
        return super.zzbvy();
    }

    public /* bridge */ /* synthetic */ zzag zzbvz() {
        return super.zzbvz();
    }

    public /* bridge */ /* synthetic */ zzw zzbwa() {
        return super.zzbwa();
    }

    public /* bridge */ /* synthetic */ zzq zzbwb() {
        return super.zzbwb();
    }

    public /* bridge */ /* synthetic */ zzt zzbwc() {
        return super.zzbwc();
    }

    public /* bridge */ /* synthetic */ zzd zzbwd() {
        return super.zzbwd();
    }

    public <V> Future<V> zzd(Callable<V> callable) throws IllegalStateException {
        zzacj();
        zzaa.zzy(callable);
        zzc com_google_android_gms_measurement_internal_zzw_zzc = new zzc(this, (Callable) callable, false, "Task exception on worker thread");
        if (Thread.currentThread() == this.atG) {
            com_google_android_gms_measurement_internal_zzw_zzc.run();
        } else {
            zza(com_google_android_gms_measurement_internal_zzw_zzc);
        }
        return com_google_android_gms_measurement_internal_zzw_zzc;
    }

    public boolean zzdg() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public <V> Future<V> zze(Callable<V> callable) throws IllegalStateException {
        zzacj();
        zzaa.zzy(callable);
        zzc com_google_android_gms_measurement_internal_zzw_zzc = new zzc(this, (Callable) callable, true, "Task exception on worker thread");
        if (Thread.currentThread() == this.atG) {
            com_google_android_gms_measurement_internal_zzw_zzc.run();
        } else {
            zza(com_google_android_gms_measurement_internal_zzw_zzc);
        }
        return com_google_android_gms_measurement_internal_zzw_zzc;
    }

    public void zzm(Runnable runnable) throws IllegalStateException {
        zzacj();
        zzaa.zzy(runnable);
        zza(new zzc(this, runnable, false, "Task exception on worker thread"));
    }

    public void zzn(Runnable runnable) throws IllegalStateException {
        zzacj();
        zzaa.zzy(runnable);
        zza(new zzc(this, runnable, false, "Task exception on network thread"));
    }

    public void zzzx() {
        if (Thread.currentThread() != this.atG) {
            throw new IllegalStateException("Call expected from worker thread");
        }
    }

    protected void zzzy() {
    }
}
