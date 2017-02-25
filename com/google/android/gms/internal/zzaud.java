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
    private static final AtomicLong zzbtW = new AtomicLong(Long.MIN_VALUE);
    private ExecutorService zzbtM;
    private zzd zzbtN;
    private zzd zzbtO;
    private final PriorityBlockingQueue<FutureTask<?>> zzbtP = new PriorityBlockingQueue();
    private final BlockingQueue<FutureTask<?>> zzbtQ = new LinkedBlockingQueue();
    private final UncaughtExceptionHandler zzbtR = new zzb(this, "Thread death: Uncaught exception on worker thread");
    private final UncaughtExceptionHandler zzbtS = new zzb(this, "Thread death: Uncaught exception on network thread");
    private final Object zzbtT = new Object();
    private final Semaphore zzbtU = new Semaphore(2);
    private volatile boolean zzbtV;

    static class zza extends RuntimeException {
    }

    private final class zzb implements UncaughtExceptionHandler {
        private final String zzbtX;
        final /* synthetic */ zzaud zzbtY;

        public zzb(zzaud com_google_android_gms_internal_zzaud, String str) {
            this.zzbtY = com_google_android_gms_internal_zzaud;
            zzac.zzw(str);
            this.zzbtX = str;
        }

        public synchronized void uncaughtException(Thread thread, Throwable th) {
            this.zzbtY.zzKk().zzLX().zzj(this.zzbtX, th);
        }
    }

    private final class zzc<V> extends FutureTask<V> implements Comparable<zzc> {
        private final String zzbtX;
        final /* synthetic */ zzaud zzbtY;
        private final long zzbtZ = zzaud.zzbtW.getAndIncrement();
        private final boolean zzbua;

        zzc(zzaud com_google_android_gms_internal_zzaud, Runnable runnable, boolean z, String str) {
            this.zzbtY = com_google_android_gms_internal_zzaud;
            super(runnable, null);
            zzac.zzw(str);
            this.zzbtX = str;
            this.zzbua = z;
            if (this.zzbtZ == Long.MAX_VALUE) {
                com_google_android_gms_internal_zzaud.zzKk().zzLX().log("Tasks index overflow");
            }
        }

        zzc(zzaud com_google_android_gms_internal_zzaud, Callable<V> callable, boolean z, String str) {
            this.zzbtY = com_google_android_gms_internal_zzaud;
            super(callable);
            zzac.zzw(str);
            this.zzbtX = str;
            this.zzbua = z;
            if (this.zzbtZ == Long.MAX_VALUE) {
                com_google_android_gms_internal_zzaud.zzKk().zzLX().log("Tasks index overflow");
            }
        }

        public /* synthetic */ int compareTo(@NonNull Object obj) {
            return zzb((zzc) obj);
        }

        protected void setException(Throwable th) {
            this.zzbtY.zzKk().zzLX().zzj(this.zzbtX, th);
            if (th instanceof zza) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), th);
            }
            super.setException(th);
        }

        public int zzb(@NonNull zzc com_google_android_gms_internal_zzaud_zzc) {
            if (this.zzbua != com_google_android_gms_internal_zzaud_zzc.zzbua) {
                return this.zzbua ? -1 : 1;
            } else {
                if (this.zzbtZ < com_google_android_gms_internal_zzaud_zzc.zzbtZ) {
                    return -1;
                }
                if (this.zzbtZ > com_google_android_gms_internal_zzaud_zzc.zzbtZ) {
                    return 1;
                }
                this.zzbtY.zzKk().zzLY().zzj("Two tasks share the same index. index", Long.valueOf(this.zzbtZ));
                return 0;
            }
        }
    }

    private final class zzd extends Thread {
        final /* synthetic */ zzaud zzbtY;
        private final Object zzbub = new Object();
        private final BlockingQueue<FutureTask<?>> zzbuc;

        public zzd(zzaud com_google_android_gms_internal_zzaud, String str, BlockingQueue<FutureTask<?>> blockingQueue) {
            this.zzbtY = com_google_android_gms_internal_zzaud;
            zzac.zzw(str);
            zzac.zzw(blockingQueue);
            this.zzbuc = blockingQueue;
            setName(str);
        }

        private void zza(InterruptedException interruptedException) {
            this.zzbtY.zzKk().zzLZ().zzj(String.valueOf(getName()).concat(" was interrupted"), interruptedException);
        }

        public void run() {
            Object obj = null;
            while (obj == null) {
                try {
                    this.zzbtY.zzbtU.acquire();
                    obj = 1;
                } catch (InterruptedException e) {
                    zza(e);
                }
            }
            while (true) {
                try {
                    FutureTask futureTask = (FutureTask) this.zzbuc.poll();
                    if (futureTask != null) {
                        futureTask.run();
                    } else {
                        synchronized (this.zzbub) {
                            if (this.zzbuc.peek() == null && !this.zzbtY.zzbtV) {
                                try {
                                    this.zzbub.wait(30000);
                                } catch (InterruptedException e2) {
                                    zza(e2);
                                }
                            }
                        }
                        synchronized (this.zzbtY.zzbtT) {
                            if (this.zzbuc.peek() == null) {
                                break;
                            }
                        }
                    }
                } catch (Throwable th) {
                    synchronized (this.zzbtY.zzbtT) {
                        this.zzbtY.zzbtU.release();
                        this.zzbtY.zzbtT.notifyAll();
                        if (this == this.zzbtY.zzbtN) {
                            this.zzbtY.zzbtN = null;
                        } else if (this == this.zzbtY.zzbtO) {
                            this.zzbtY.zzbtO = null;
                        } else {
                            this.zzbtY.zzKk().zzLX().log("Current scheduler thread is neither worker nor network");
                        }
                    }
                }
            }
            synchronized (this.zzbtY.zzbtT) {
                this.zzbtY.zzbtU.release();
                this.zzbtY.zzbtT.notifyAll();
                if (this == this.zzbtY.zzbtN) {
                    this.zzbtY.zzbtN = null;
                } else if (this == this.zzbtY.zzbtO) {
                    this.zzbtY.zzbtO = null;
                } else {
                    this.zzbtY.zzKk().zzLX().log("Current scheduler thread is neither worker nor network");
                }
            }
        }

        public void zzhA() {
            synchronized (this.zzbub) {
                this.zzbub.notifyAll();
            }
        }
    }

    zzaud(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    private void zza(zzc<?> com_google_android_gms_internal_zzaud_zzc_) {
        synchronized (this.zzbtT) {
            this.zzbtP.add(com_google_android_gms_internal_zzaud_zzc_);
            if (this.zzbtN == null) {
                this.zzbtN = new zzd(this, "Measurement Worker", this.zzbtP);
                this.zzbtN.setUncaughtExceptionHandler(this.zzbtR);
                this.zzbtN.start();
            } else {
                this.zzbtN.zzhA();
            }
        }
    }

    private void zza(FutureTask<?> futureTask) {
        synchronized (this.zzbtT) {
            this.zzbtQ.add(futureTask);
            if (this.zzbtO == null) {
                this.zzbtO = new zzd(this, "Measurement Network", this.zzbtQ);
                this.zzbtO.setUncaughtExceptionHandler(this.zzbtS);
                this.zzbtO.start();
            } else {
                this.zzbtO.zzhA();
            }
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzJU() {
        super.zzJU();
    }

    public /* bridge */ /* synthetic */ void zzJV() {
        super.zzJV();
    }

    public void zzJW() {
        if (Thread.currentThread() != this.zzbtO) {
            throw new IllegalStateException("Call expected from network thread");
        }
    }

    public /* bridge */ /* synthetic */ zzatb zzJX() {
        return super.zzJX();
    }

    public /* bridge */ /* synthetic */ zzatf zzJY() {
        return super.zzJY();
    }

    public /* bridge */ /* synthetic */ zzauj zzJZ() {
        return super.zzJZ();
    }

    public /* bridge */ /* synthetic */ zzatu zzKa() {
        return super.zzKa();
    }

    public /* bridge */ /* synthetic */ zzatl zzKb() {
        return super.zzKb();
    }

    public /* bridge */ /* synthetic */ zzaul zzKc() {
        return super.zzKc();
    }

    public /* bridge */ /* synthetic */ zzauk zzKd() {
        return super.zzKd();
    }

    public /* bridge */ /* synthetic */ zzatv zzKe() {
        return super.zzKe();
    }

    public /* bridge */ /* synthetic */ zzatj zzKf() {
        return super.zzKf();
    }

    public /* bridge */ /* synthetic */ zzaut zzKg() {
        return super.zzKg();
    }

    public /* bridge */ /* synthetic */ zzauc zzKh() {
        return super.zzKh();
    }

    public /* bridge */ /* synthetic */ zzaun zzKi() {
        return super.zzKi();
    }

    public /* bridge */ /* synthetic */ zzaud zzKj() {
        return super.zzKj();
    }

    public /* bridge */ /* synthetic */ zzatx zzKk() {
        return super.zzKk();
    }

    public /* bridge */ /* synthetic */ zzaua zzKl() {
        return super.zzKl();
    }

    public /* bridge */ /* synthetic */ zzati zzKm() {
        return super.zzKm();
    }

    public boolean zzMp() {
        return Thread.currentThread() == this.zzbtN;
    }

    ExecutorService zzMq() {
        ExecutorService executorService;
        synchronized (this.zzbtT) {
            if (this.zzbtM == null) {
                this.zzbtM = new ThreadPoolExecutor(0, 1, 30, TimeUnit.SECONDS, new ArrayBlockingQueue(100));
            }
            executorService = this.zzbtM;
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
        if (Thread.currentThread() == this.zzbtN) {
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
        if (Thread.currentThread() == this.zzbtN) {
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
        if (Thread.currentThread() != this.zzbtN) {
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
