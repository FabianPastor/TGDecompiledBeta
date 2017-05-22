package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
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

public class zzaud extends zzauh {
    private static final AtomicLong zzbtU = new AtomicLong(Long.MIN_VALUE);
    private ExecutorService zzbtK;
    private zzd zzbtL;
    private zzd zzbtM;
    private final PriorityBlockingQueue<FutureTask<?>> zzbtN = new PriorityBlockingQueue();
    private final BlockingQueue<FutureTask<?>> zzbtO = new LinkedBlockingQueue();
    private final UncaughtExceptionHandler zzbtP = new zzb(this, "Thread death: Uncaught exception on worker thread");
    private final UncaughtExceptionHandler zzbtQ = new zzb(this, "Thread death: Uncaught exception on network thread");
    private final Object zzbtR = new Object();
    private final Semaphore zzbtS = new Semaphore(2);
    private volatile boolean zzbtT;

    static class zza extends RuntimeException {
    }

    private final class zzb implements UncaughtExceptionHandler {
        private final String zzbtV;
        final /* synthetic */ zzaud zzbtW;

        public zzb(zzaud com_google_android_gms_internal_zzaud, String str) {
            this.zzbtW = com_google_android_gms_internal_zzaud;
            zzac.zzw(str);
            this.zzbtV = str;
        }

        public synchronized void uncaughtException(Thread thread, Throwable th) {
            this.zzbtW.zzKl().zzLZ().zzj(this.zzbtV, th);
        }
    }

    private final class zzc<V> extends FutureTask<V> implements Comparable<zzc> {
        private final String zzbtV;
        final /* synthetic */ zzaud zzbtW;
        private final long zzbtX = zzaud.zzbtU.getAndIncrement();
        private final boolean zzbtY;

        zzc(zzaud com_google_android_gms_internal_zzaud, Runnable runnable, boolean z, String str) {
            this.zzbtW = com_google_android_gms_internal_zzaud;
            super(runnable, null);
            zzac.zzw(str);
            this.zzbtV = str;
            this.zzbtY = z;
            if (this.zzbtX == Long.MAX_VALUE) {
                com_google_android_gms_internal_zzaud.zzKl().zzLZ().log("Tasks index overflow");
            }
        }

        zzc(zzaud com_google_android_gms_internal_zzaud, Callable<V> callable, boolean z, String str) {
            this.zzbtW = com_google_android_gms_internal_zzaud;
            super(callable);
            zzac.zzw(str);
            this.zzbtV = str;
            this.zzbtY = z;
            if (this.zzbtX == Long.MAX_VALUE) {
                com_google_android_gms_internal_zzaud.zzKl().zzLZ().log("Tasks index overflow");
            }
        }

        public /* synthetic */ int compareTo(@NonNull Object obj) {
            return zzb((zzc) obj);
        }

        protected void setException(Throwable th) {
            this.zzbtW.zzKl().zzLZ().zzj(this.zzbtV, th);
            if (th instanceof zza) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), th);
            }
            super.setException(th);
        }

        public int zzb(@NonNull zzc com_google_android_gms_internal_zzaud_zzc) {
            if (this.zzbtY != com_google_android_gms_internal_zzaud_zzc.zzbtY) {
                return this.zzbtY ? -1 : 1;
            } else {
                if (this.zzbtX < com_google_android_gms_internal_zzaud_zzc.zzbtX) {
                    return -1;
                }
                if (this.zzbtX > com_google_android_gms_internal_zzaud_zzc.zzbtX) {
                    return 1;
                }
                this.zzbtW.zzKl().zzMa().zzj("Two tasks share the same index. index", Long.valueOf(this.zzbtX));
                return 0;
            }
        }
    }

    private final class zzd extends Thread {
        final /* synthetic */ zzaud zzbtW;
        private final Object zzbtZ = new Object();
        private final BlockingQueue<FutureTask<?>> zzbua;

        public zzd(zzaud com_google_android_gms_internal_zzaud, String str, BlockingQueue<FutureTask<?>> blockingQueue) {
            this.zzbtW = com_google_android_gms_internal_zzaud;
            zzac.zzw(str);
            zzac.zzw(blockingQueue);
            this.zzbua = blockingQueue;
            setName(str);
        }

        private void zza(InterruptedException interruptedException) {
            this.zzbtW.zzKl().zzMb().zzj(String.valueOf(getName()).concat(" was interrupted"), interruptedException);
        }

        public void run() {
            Object obj = null;
            while (obj == null) {
                try {
                    this.zzbtW.zzbtS.acquire();
                    obj = 1;
                } catch (InterruptedException e) {
                    zza(e);
                }
            }
            while (true) {
                try {
                    FutureTask futureTask = (FutureTask) this.zzbua.poll();
                    if (futureTask != null) {
                        futureTask.run();
                    } else {
                        synchronized (this.zzbtZ) {
                            if (this.zzbua.peek() == null && !this.zzbtW.zzbtT) {
                                try {
                                    this.zzbtZ.wait(30000);
                                } catch (InterruptedException e2) {
                                    zza(e2);
                                }
                            }
                        }
                        synchronized (this.zzbtW.zzbtR) {
                            if (this.zzbua.peek() == null) {
                                break;
                            }
                        }
                    }
                } catch (Throwable th) {
                    synchronized (this.zzbtW.zzbtR) {
                        this.zzbtW.zzbtS.release();
                        this.zzbtW.zzbtR.notifyAll();
                        if (this == this.zzbtW.zzbtL) {
                            this.zzbtW.zzbtL = null;
                        } else if (this == this.zzbtW.zzbtM) {
                            this.zzbtW.zzbtM = null;
                        } else {
                            this.zzbtW.zzKl().zzLZ().log("Current scheduler thread is neither worker nor network");
                        }
                    }
                }
            }
            synchronized (this.zzbtW.zzbtR) {
                this.zzbtW.zzbtS.release();
                this.zzbtW.zzbtR.notifyAll();
                if (this == this.zzbtW.zzbtL) {
                    this.zzbtW.zzbtL = null;
                } else if (this == this.zzbtW.zzbtM) {
                    this.zzbtW.zzbtM = null;
                } else {
                    this.zzbtW.zzKl().zzLZ().log("Current scheduler thread is neither worker nor network");
                }
            }
        }

        public void zzhA() {
            synchronized (this.zzbtZ) {
                this.zzbtZ.notifyAll();
            }
        }
    }

    zzaud(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    private void zza(zzc<?> com_google_android_gms_internal_zzaud_zzc_) {
        synchronized (this.zzbtR) {
            this.zzbtN.add(com_google_android_gms_internal_zzaud_zzc_);
            if (this.zzbtL == null) {
                this.zzbtL = new zzd(this, "Measurement Worker", this.zzbtN);
                this.zzbtL.setUncaughtExceptionHandler(this.zzbtP);
                this.zzbtL.start();
            } else {
                this.zzbtL.zzhA();
            }
        }
    }

    private void zza(FutureTask<?> futureTask) {
        synchronized (this.zzbtR) {
            this.zzbtO.add(futureTask);
            if (this.zzbtM == null) {
                this.zzbtM = new zzd(this, "Measurement Network", this.zzbtO);
                this.zzbtM.setUncaughtExceptionHandler(this.zzbtQ);
                this.zzbtM.start();
            } else {
                this.zzbtM.zzhA();
            }
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzJV() {
        super.zzJV();
    }

    public /* bridge */ /* synthetic */ void zzJW() {
        super.zzJW();
    }

    public void zzJX() {
        if (Thread.currentThread() != this.zzbtM) {
            throw new IllegalStateException("Call expected from network thread");
        }
    }

    public /* bridge */ /* synthetic */ zzatb zzJY() {
        return super.zzJY();
    }

    public /* bridge */ /* synthetic */ zzatf zzJZ() {
        return super.zzJZ();
    }

    public /* bridge */ /* synthetic */ zzauj zzKa() {
        return super.zzKa();
    }

    public /* bridge */ /* synthetic */ zzatu zzKb() {
        return super.zzKb();
    }

    public /* bridge */ /* synthetic */ zzatl zzKc() {
        return super.zzKc();
    }

    public /* bridge */ /* synthetic */ zzaul zzKd() {
        return super.zzKd();
    }

    public /* bridge */ /* synthetic */ zzauk zzKe() {
        return super.zzKe();
    }

    public /* bridge */ /* synthetic */ zzatv zzKf() {
        return super.zzKf();
    }

    public /* bridge */ /* synthetic */ zzatj zzKg() {
        return super.zzKg();
    }

    public /* bridge */ /* synthetic */ zzaut zzKh() {
        return super.zzKh();
    }

    public /* bridge */ /* synthetic */ zzauc zzKi() {
        return super.zzKi();
    }

    public /* bridge */ /* synthetic */ zzaun zzKj() {
        return super.zzKj();
    }

    public /* bridge */ /* synthetic */ zzaud zzKk() {
        return super.zzKk();
    }

    public /* bridge */ /* synthetic */ zzatx zzKl() {
        return super.zzKl();
    }

    public /* bridge */ /* synthetic */ zzaua zzKm() {
        return super.zzKm();
    }

    public /* bridge */ /* synthetic */ zzati zzKn() {
        return super.zzKn();
    }

    public boolean zzMr() {
        return Thread.currentThread() == this.zzbtL;
    }

    ExecutorService zzMs() {
        ExecutorService executorService;
        synchronized (this.zzbtR) {
            if (this.zzbtK == null) {
                this.zzbtK = new ThreadPoolExecutor(0, 1, 30, TimeUnit.SECONDS, new ArrayBlockingQueue(100));
            }
            executorService = this.zzbtK;
        }
        return executorService;
    }

    public boolean zzbc() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public <V> Future<V> zzd(Callable<V> callable) throws IllegalStateException {
        zzob();
        zzac.zzw(callable);
        zzc com_google_android_gms_internal_zzaud_zzc = new zzc(this, (Callable) callable, false, "Task exception on worker thread");
        if (Thread.currentThread() == this.zzbtL) {
            if (!this.zzbtN.isEmpty()) {
                zzKl().zzMb().log("Callable skipped the worker queue.");
            }
            com_google_android_gms_internal_zzaud_zzc.run();
        } else {
            zza(com_google_android_gms_internal_zzaud_zzc);
        }
        return com_google_android_gms_internal_zzaud_zzc;
    }

    public <V> Future<V> zze(Callable<V> callable) throws IllegalStateException {
        zzob();
        zzac.zzw(callable);
        zzc com_google_android_gms_internal_zzaud_zzc = new zzc(this, (Callable) callable, true, "Task exception on worker thread");
        if (Thread.currentThread() == this.zzbtL) {
            com_google_android_gms_internal_zzaud_zzc.run();
        } else {
            zza(com_google_android_gms_internal_zzaud_zzc);
        }
        return com_google_android_gms_internal_zzaud_zzc;
    }

    public void zzm(Runnable runnable) throws IllegalStateException {
        zzob();
        zzac.zzw(runnable);
        zza(new zzc(this, runnable, false, "Task exception on worker thread"));
    }

    public void zzmR() {
        if (Thread.currentThread() != this.zzbtL) {
            throw new IllegalStateException("Call expected from worker thread");
        }
    }

    protected void zzmS() {
    }

    public void zzn(Runnable runnable) throws IllegalStateException {
        zzob();
        zzac.zzw(runnable);
        zza(new zzc(this, runnable, false, "Task exception on network thread"));
    }

    public /* bridge */ /* synthetic */ zze zznR() {
        return super.zznR();
    }
}
