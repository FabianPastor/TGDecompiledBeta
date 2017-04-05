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
    private static final AtomicLong zzbtS = new AtomicLong(Long.MIN_VALUE);
    private ExecutorService zzbtI;
    private zzd zzbtJ;
    private zzd zzbtK;
    private final PriorityBlockingQueue<FutureTask<?>> zzbtL = new PriorityBlockingQueue();
    private final BlockingQueue<FutureTask<?>> zzbtM = new LinkedBlockingQueue();
    private final UncaughtExceptionHandler zzbtN = new zzb(this, "Thread death: Uncaught exception on worker thread");
    private final UncaughtExceptionHandler zzbtO = new zzb(this, "Thread death: Uncaught exception on network thread");
    private final Object zzbtP = new Object();
    private final Semaphore zzbtQ = new Semaphore(2);
    private volatile boolean zzbtR;

    static class zza extends RuntimeException {
    }

    private final class zzb implements UncaughtExceptionHandler {
        private final String zzbtT;
        final /* synthetic */ zzaud zzbtU;

        public zzb(zzaud com_google_android_gms_internal_zzaud, String str) {
            this.zzbtU = com_google_android_gms_internal_zzaud;
            zzac.zzw(str);
            this.zzbtT = str;
        }

        public synchronized void uncaughtException(Thread thread, Throwable th) {
            this.zzbtU.zzKl().zzLY().zzj(this.zzbtT, th);
        }
    }

    private final class zzc<V> extends FutureTask<V> implements Comparable<zzc> {
        private final String zzbtT;
        final /* synthetic */ zzaud zzbtU;
        private final long zzbtV = zzaud.zzbtS.getAndIncrement();
        private final boolean zzbtW;

        zzc(zzaud com_google_android_gms_internal_zzaud, Runnable runnable, boolean z, String str) {
            this.zzbtU = com_google_android_gms_internal_zzaud;
            super(runnable, null);
            zzac.zzw(str);
            this.zzbtT = str;
            this.zzbtW = z;
            if (this.zzbtV == Long.MAX_VALUE) {
                com_google_android_gms_internal_zzaud.zzKl().zzLY().log("Tasks index overflow");
            }
        }

        zzc(zzaud com_google_android_gms_internal_zzaud, Callable<V> callable, boolean z, String str) {
            this.zzbtU = com_google_android_gms_internal_zzaud;
            super(callable);
            zzac.zzw(str);
            this.zzbtT = str;
            this.zzbtW = z;
            if (this.zzbtV == Long.MAX_VALUE) {
                com_google_android_gms_internal_zzaud.zzKl().zzLY().log("Tasks index overflow");
            }
        }

        public /* synthetic */ int compareTo(@NonNull Object obj) {
            return zzb((zzc) obj);
        }

        protected void setException(Throwable th) {
            this.zzbtU.zzKl().zzLY().zzj(this.zzbtT, th);
            if (th instanceof zza) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), th);
            }
            super.setException(th);
        }

        public int zzb(@NonNull zzc com_google_android_gms_internal_zzaud_zzc) {
            if (this.zzbtW != com_google_android_gms_internal_zzaud_zzc.zzbtW) {
                return this.zzbtW ? -1 : 1;
            } else {
                if (this.zzbtV < com_google_android_gms_internal_zzaud_zzc.zzbtV) {
                    return -1;
                }
                if (this.zzbtV > com_google_android_gms_internal_zzaud_zzc.zzbtV) {
                    return 1;
                }
                this.zzbtU.zzKl().zzLZ().zzj("Two tasks share the same index. index", Long.valueOf(this.zzbtV));
                return 0;
            }
        }
    }

    private final class zzd extends Thread {
        final /* synthetic */ zzaud zzbtU;
        private final Object zzbtX = new Object();
        private final BlockingQueue<FutureTask<?>> zzbtY;

        public zzd(zzaud com_google_android_gms_internal_zzaud, String str, BlockingQueue<FutureTask<?>> blockingQueue) {
            this.zzbtU = com_google_android_gms_internal_zzaud;
            zzac.zzw(str);
            zzac.zzw(blockingQueue);
            this.zzbtY = blockingQueue;
            setName(str);
        }

        private void zza(InterruptedException interruptedException) {
            this.zzbtU.zzKl().zzMa().zzj(String.valueOf(getName()).concat(" was interrupted"), interruptedException);
        }

        public void run() {
            Object obj = null;
            while (obj == null) {
                try {
                    this.zzbtU.zzbtQ.acquire();
                    obj = 1;
                } catch (InterruptedException e) {
                    zza(e);
                }
            }
            while (true) {
                try {
                    FutureTask futureTask = (FutureTask) this.zzbtY.poll();
                    if (futureTask != null) {
                        futureTask.run();
                    } else {
                        synchronized (this.zzbtX) {
                            if (this.zzbtY.peek() == null && !this.zzbtU.zzbtR) {
                                try {
                                    this.zzbtX.wait(30000);
                                } catch (InterruptedException e2) {
                                    zza(e2);
                                }
                            }
                        }
                        synchronized (this.zzbtU.zzbtP) {
                            if (this.zzbtY.peek() == null) {
                                break;
                            }
                        }
                    }
                } catch (Throwable th) {
                    synchronized (this.zzbtU.zzbtP) {
                        this.zzbtU.zzbtQ.release();
                        this.zzbtU.zzbtP.notifyAll();
                        if (this == this.zzbtU.zzbtJ) {
                            this.zzbtU.zzbtJ = null;
                        } else if (this == this.zzbtU.zzbtK) {
                            this.zzbtU.zzbtK = null;
                        } else {
                            this.zzbtU.zzKl().zzLY().log("Current scheduler thread is neither worker nor network");
                        }
                    }
                }
            }
            synchronized (this.zzbtU.zzbtP) {
                this.zzbtU.zzbtQ.release();
                this.zzbtU.zzbtP.notifyAll();
                if (this == this.zzbtU.zzbtJ) {
                    this.zzbtU.zzbtJ = null;
                } else if (this == this.zzbtU.zzbtK) {
                    this.zzbtU.zzbtK = null;
                } else {
                    this.zzbtU.zzKl().zzLY().log("Current scheduler thread is neither worker nor network");
                }
            }
        }

        public void zzhA() {
            synchronized (this.zzbtX) {
                this.zzbtX.notifyAll();
            }
        }
    }

    zzaud(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    private void zza(zzc<?> com_google_android_gms_internal_zzaud_zzc_) {
        synchronized (this.zzbtP) {
            this.zzbtL.add(com_google_android_gms_internal_zzaud_zzc_);
            if (this.zzbtJ == null) {
                this.zzbtJ = new zzd(this, "Measurement Worker", this.zzbtL);
                this.zzbtJ.setUncaughtExceptionHandler(this.zzbtN);
                this.zzbtJ.start();
            } else {
                this.zzbtJ.zzhA();
            }
        }
    }

    private void zza(FutureTask<?> futureTask) {
        synchronized (this.zzbtP) {
            this.zzbtM.add(futureTask);
            if (this.zzbtK == null) {
                this.zzbtK = new zzd(this, "Measurement Network", this.zzbtM);
                this.zzbtK.setUncaughtExceptionHandler(this.zzbtO);
                this.zzbtK.start();
            } else {
                this.zzbtK.zzhA();
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
        if (Thread.currentThread() != this.zzbtK) {
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

    public boolean zzMq() {
        return Thread.currentThread() == this.zzbtJ;
    }

    ExecutorService zzMr() {
        ExecutorService executorService;
        synchronized (this.zzbtP) {
            if (this.zzbtI == null) {
                this.zzbtI = new ThreadPoolExecutor(0, 1, 30, TimeUnit.SECONDS, new ArrayBlockingQueue(100));
            }
            executorService = this.zzbtI;
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
        if (Thread.currentThread() == this.zzbtJ) {
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
        if (Thread.currentThread() == this.zzbtJ) {
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
        if (Thread.currentThread() != this.zzbtJ) {
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
