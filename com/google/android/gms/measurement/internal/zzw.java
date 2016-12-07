package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
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
    private static final AtomicLong aqI = new AtomicLong(Long.MIN_VALUE);
    private zzd aqA;
    private final PriorityBlockingQueue<FutureTask<?>> aqB = new PriorityBlockingQueue();
    private final BlockingQueue<FutureTask<?>> aqC = new LinkedBlockingQueue();
    private final UncaughtExceptionHandler aqD = new zzb(this, "Thread death: Uncaught exception on worker thread");
    private final UncaughtExceptionHandler aqE = new zzb(this, "Thread death: Uncaught exception on network thread");
    private final Object aqF = new Object();
    private final Semaphore aqG = new Semaphore(2);
    private volatile boolean aqH;
    private zzd aqz;

    static class zza extends RuntimeException {
    }

    private final class zzb implements UncaughtExceptionHandler {
        private final String aqJ;
        final /* synthetic */ zzw aqK;

        public zzb(zzw com_google_android_gms_measurement_internal_zzw, String str) {
            this.aqK = com_google_android_gms_measurement_internal_zzw;
            zzac.zzy(str);
            this.aqJ = str;
        }

        public synchronized void uncaughtException(Thread thread, Throwable th) {
            this.aqK.zzbvg().zzbwc().zzj(this.aqJ, th);
        }
    }

    private final class zzc<V> extends FutureTask<V> implements Comparable<zzc> {
        private final String aqJ;
        final /* synthetic */ zzw aqK;
        private final long aqL = zzw.aqI.getAndIncrement();
        private final boolean aqM;

        zzc(zzw com_google_android_gms_measurement_internal_zzw, Runnable runnable, boolean z, String str) {
            this.aqK = com_google_android_gms_measurement_internal_zzw;
            super(runnable, null);
            zzac.zzy(str);
            this.aqJ = str;
            this.aqM = z;
            if (this.aqL == Long.MAX_VALUE) {
                com_google_android_gms_measurement_internal_zzw.zzbvg().zzbwc().log("Tasks index overflow");
            }
        }

        zzc(zzw com_google_android_gms_measurement_internal_zzw, Callable<V> callable, boolean z, String str) {
            this.aqK = com_google_android_gms_measurement_internal_zzw;
            super(callable);
            zzac.zzy(str);
            this.aqJ = str;
            this.aqM = z;
            if (this.aqL == Long.MAX_VALUE) {
                com_google_android_gms_measurement_internal_zzw.zzbvg().zzbwc().log("Tasks index overflow");
            }
        }

        public /* synthetic */ int compareTo(@NonNull Object obj) {
            return zzb((zzc) obj);
        }

        protected void setException(Throwable th) {
            this.aqK.zzbvg().zzbwc().zzj(this.aqJ, th);
            if (th instanceof zza) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), th);
            }
            super.setException(th);
        }

        public int zzb(@NonNull zzc com_google_android_gms_measurement_internal_zzw_zzc) {
            if (this.aqM != com_google_android_gms_measurement_internal_zzw_zzc.aqM) {
                return this.aqM ? -1 : 1;
            } else {
                if (this.aqL < com_google_android_gms_measurement_internal_zzw_zzc.aqL) {
                    return -1;
                }
                if (this.aqL > com_google_android_gms_measurement_internal_zzw_zzc.aqL) {
                    return 1;
                }
                this.aqK.zzbvg().zzbwd().zzj("Two tasks share the same index. index", Long.valueOf(this.aqL));
                return 0;
            }
        }
    }

    private final class zzd extends Thread {
        final /* synthetic */ zzw aqK;
        private final Object aqN = new Object();
        private final BlockingQueue<FutureTask<?>> aqO;

        public zzd(zzw com_google_android_gms_measurement_internal_zzw, String str, BlockingQueue<FutureTask<?>> blockingQueue) {
            this.aqK = com_google_android_gms_measurement_internal_zzw;
            zzac.zzy(str);
            zzac.zzy(blockingQueue);
            this.aqO = blockingQueue;
            setName(str);
        }

        private void zza(InterruptedException interruptedException) {
            this.aqK.zzbvg().zzbwe().zzj(String.valueOf(getName()).concat(" was interrupted"), interruptedException);
        }

        public void run() {
            Object obj = null;
            while (obj == null) {
                try {
                    this.aqK.aqG.acquire();
                    obj = 1;
                } catch (InterruptedException e) {
                    zza(e);
                }
            }
            while (true) {
                try {
                    FutureTask futureTask = (FutureTask) this.aqO.poll();
                    if (futureTask != null) {
                        futureTask.run();
                    } else {
                        synchronized (this.aqN) {
                            if (this.aqO.peek() == null && !this.aqK.aqH) {
                                try {
                                    this.aqN.wait(30000);
                                } catch (InterruptedException e2) {
                                    zza(e2);
                                }
                            }
                        }
                        synchronized (this.aqK.aqF) {
                            if (this.aqO.peek() == null) {
                                break;
                            }
                        }
                    }
                } catch (Throwable th) {
                    synchronized (this.aqK.aqF) {
                        this.aqK.aqG.release();
                        this.aqK.aqF.notifyAll();
                        if (this == this.aqK.aqz) {
                            this.aqK.aqz = null;
                        } else if (this == this.aqK.aqA) {
                            this.aqK.aqA = null;
                        } else {
                            this.aqK.zzbvg().zzbwc().log("Current scheduler thread is neither worker nor network");
                        }
                    }
                }
            }
            synchronized (this.aqK.aqF) {
                this.aqK.aqG.release();
                this.aqK.aqF.notifyAll();
                if (this == this.aqK.aqz) {
                    this.aqK.aqz = null;
                } else if (this == this.aqK.aqA) {
                    this.aqK.aqA = null;
                } else {
                    this.aqK.zzbvg().zzbwc().log("Current scheduler thread is neither worker nor network");
                }
            }
        }

        public void zzoi() {
            synchronized (this.aqN) {
                this.aqN.notifyAll();
            }
        }
    }

    zzw(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    private void zza(zzc<?> com_google_android_gms_measurement_internal_zzw_zzc_) {
        synchronized (this.aqF) {
            this.aqB.add(com_google_android_gms_measurement_internal_zzw_zzc_);
            if (this.aqz == null) {
                this.aqz = new zzd(this, "Measurement Worker", this.aqB);
                this.aqz.setUncaughtExceptionHandler(this.aqD);
                this.aqz.start();
            } else {
                this.aqz.zzoi();
            }
        }
    }

    private void zza(FutureTask<?> futureTask) {
        synchronized (this.aqF) {
            this.aqC.add(futureTask);
            if (this.aqA == null) {
                this.aqA = new zzd(this, "Measurement Network", this.aqC);
                this.aqA.setUncaughtExceptionHandler(this.aqE);
                this.aqA.start();
            } else {
                this.aqA.zzoi();
            }
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzaam() {
        super.zzaam();
    }

    public /* bridge */ /* synthetic */ zze zzaan() {
        return super.zzaan();
    }

    public void zzbuv() {
        if (Thread.currentThread() != this.aqA) {
            throw new IllegalStateException("Call expected from network thread");
        }
    }

    public /* bridge */ /* synthetic */ zzc zzbuw() {
        return super.zzbuw();
    }

    public /* bridge */ /* synthetic */ zzac zzbux() {
        return super.zzbux();
    }

    public /* bridge */ /* synthetic */ zzn zzbuy() {
        return super.zzbuy();
    }

    public /* bridge */ /* synthetic */ zzg zzbuz() {
        return super.zzbuz();
    }

    public /* bridge */ /* synthetic */ zzad zzbva() {
        return super.zzbva();
    }

    public /* bridge */ /* synthetic */ zze zzbvb() {
        return super.zzbvb();
    }

    public /* bridge */ /* synthetic */ zzal zzbvc() {
        return super.zzbvc();
    }

    public /* bridge */ /* synthetic */ zzv zzbvd() {
        return super.zzbvd();
    }

    public /* bridge */ /* synthetic */ zzaf zzbve() {
        return super.zzbve();
    }

    public /* bridge */ /* synthetic */ zzw zzbvf() {
        return super.zzbvf();
    }

    public /* bridge */ /* synthetic */ zzp zzbvg() {
        return super.zzbvg();
    }

    public /* bridge */ /* synthetic */ zzt zzbvh() {
        return super.zzbvh();
    }

    public /* bridge */ /* synthetic */ zzd zzbvi() {
        return super.zzbvi();
    }

    public <V> Future<V> zzd(Callable<V> callable) throws IllegalStateException {
        zzaax();
        zzac.zzy(callable);
        zzc com_google_android_gms_measurement_internal_zzw_zzc = new zzc(this, (Callable) callable, false, "Task exception on worker thread");
        if (Thread.currentThread() == this.aqz) {
            com_google_android_gms_measurement_internal_zzw_zzc.run();
        } else {
            zza(com_google_android_gms_measurement_internal_zzw_zzc);
        }
        return com_google_android_gms_measurement_internal_zzw_zzc;
    }

    public <V> Future<V> zze(Callable<V> callable) throws IllegalStateException {
        zzaax();
        zzac.zzy(callable);
        zzc com_google_android_gms_measurement_internal_zzw_zzc = new zzc(this, (Callable) callable, true, "Task exception on worker thread");
        if (Thread.currentThread() == this.aqz) {
            com_google_android_gms_measurement_internal_zzw_zzc.run();
        } else {
            zza(com_google_android_gms_measurement_internal_zzw_zzc);
        }
        return com_google_android_gms_measurement_internal_zzw_zzc;
    }

    public void zzm(Runnable runnable) throws IllegalStateException {
        zzaax();
        zzac.zzy(runnable);
        zza(new zzc(this, runnable, false, "Task exception on worker thread"));
    }

    public void zzn(Runnable runnable) throws IllegalStateException {
        zzaax();
        zzac.zzy(runnable);
        zza(new zzc(this, runnable, false, "Task exception on network thread"));
    }

    public void zzyl() {
        if (Thread.currentThread() != this.aqz) {
            throw new IllegalStateException("Call expected from worker thread");
        }
    }

    protected void zzym() {
    }
}
