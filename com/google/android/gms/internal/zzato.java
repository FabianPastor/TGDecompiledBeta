package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
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

public class zzato extends zzats {
    private static final AtomicLong zzbsV = new AtomicLong(Long.MIN_VALUE);
    private zzd zzbsM;
    private zzd zzbsN;
    private final PriorityBlockingQueue<FutureTask<?>> zzbsO = new PriorityBlockingQueue();
    private final BlockingQueue<FutureTask<?>> zzbsP = new LinkedBlockingQueue();
    private final UncaughtExceptionHandler zzbsQ = new zzb(this, "Thread death: Uncaught exception on worker thread");
    private final UncaughtExceptionHandler zzbsR = new zzb(this, "Thread death: Uncaught exception on network thread");
    private final Object zzbsS = new Object();
    private final Semaphore zzbsT = new Semaphore(2);
    private volatile boolean zzbsU;

    static class zza extends RuntimeException {
    }

    private final class zzb implements UncaughtExceptionHandler {
        private final String zzbsW;
        final /* synthetic */ zzato zzbsX;

        public zzb(zzato com_google_android_gms_internal_zzato, String str) {
            this.zzbsX = com_google_android_gms_internal_zzato;
            zzac.zzw(str);
            this.zzbsW = str;
        }

        public synchronized void uncaughtException(Thread thread, Throwable th) {
            this.zzbsX.zzJt().zzLa().zzj(this.zzbsW, th);
        }
    }

    private final class zzc<V> extends FutureTask<V> implements Comparable<zzc> {
        private final String zzbsW;
        final /* synthetic */ zzato zzbsX;
        private final long zzbsY = zzato.zzbsV.getAndIncrement();
        private final boolean zzbsZ;

        zzc(zzato com_google_android_gms_internal_zzato, Runnable runnable, boolean z, String str) {
            this.zzbsX = com_google_android_gms_internal_zzato;
            super(runnable, null);
            zzac.zzw(str);
            this.zzbsW = str;
            this.zzbsZ = z;
            if (this.zzbsY == Long.MAX_VALUE) {
                com_google_android_gms_internal_zzato.zzJt().zzLa().log("Tasks index overflow");
            }
        }

        zzc(zzato com_google_android_gms_internal_zzato, Callable<V> callable, boolean z, String str) {
            this.zzbsX = com_google_android_gms_internal_zzato;
            super(callable);
            zzac.zzw(str);
            this.zzbsW = str;
            this.zzbsZ = z;
            if (this.zzbsY == Long.MAX_VALUE) {
                com_google_android_gms_internal_zzato.zzJt().zzLa().log("Tasks index overflow");
            }
        }

        public /* synthetic */ int compareTo(@NonNull Object obj) {
            return zzb((zzc) obj);
        }

        protected void setException(Throwable th) {
            this.zzbsX.zzJt().zzLa().zzj(this.zzbsW, th);
            if (th instanceof zza) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), th);
            }
            super.setException(th);
        }

        public int zzb(@NonNull zzc com_google_android_gms_internal_zzato_zzc) {
            if (this.zzbsZ != com_google_android_gms_internal_zzato_zzc.zzbsZ) {
                return this.zzbsZ ? -1 : 1;
            } else {
                if (this.zzbsY < com_google_android_gms_internal_zzato_zzc.zzbsY) {
                    return -1;
                }
                if (this.zzbsY > com_google_android_gms_internal_zzato_zzc.zzbsY) {
                    return 1;
                }
                this.zzbsX.zzJt().zzLb().zzj("Two tasks share the same index. index", Long.valueOf(this.zzbsY));
                return 0;
            }
        }
    }

    private final class zzd extends Thread {
        final /* synthetic */ zzato zzbsX;
        private final Object zzbta = new Object();
        private final BlockingQueue<FutureTask<?>> zzbtb;

        public zzd(zzato com_google_android_gms_internal_zzato, String str, BlockingQueue<FutureTask<?>> blockingQueue) {
            this.zzbsX = com_google_android_gms_internal_zzato;
            zzac.zzw(str);
            zzac.zzw(blockingQueue);
            this.zzbtb = blockingQueue;
            setName(str);
        }

        private void zza(InterruptedException interruptedException) {
            this.zzbsX.zzJt().zzLc().zzj(String.valueOf(getName()).concat(" was interrupted"), interruptedException);
        }

        public void run() {
            Object obj = null;
            while (obj == null) {
                try {
                    this.zzbsX.zzbsT.acquire();
                    obj = 1;
                } catch (InterruptedException e) {
                    zza(e);
                }
            }
            while (true) {
                try {
                    FutureTask futureTask = (FutureTask) this.zzbtb.poll();
                    if (futureTask != null) {
                        futureTask.run();
                    } else {
                        synchronized (this.zzbta) {
                            if (this.zzbtb.peek() == null && !this.zzbsX.zzbsU) {
                                try {
                                    this.zzbta.wait(30000);
                                } catch (InterruptedException e2) {
                                    zza(e2);
                                }
                            }
                        }
                        synchronized (this.zzbsX.zzbsS) {
                            if (this.zzbtb.peek() == null) {
                                break;
                            }
                        }
                    }
                } catch (Throwable th) {
                    synchronized (this.zzbsX.zzbsS) {
                        this.zzbsX.zzbsT.release();
                        this.zzbsX.zzbsS.notifyAll();
                        if (this == this.zzbsX.zzbsM) {
                            this.zzbsX.zzbsM = null;
                        } else if (this == this.zzbsX.zzbsN) {
                            this.zzbsX.zzbsN = null;
                        } else {
                            this.zzbsX.zzJt().zzLa().log("Current scheduler thread is neither worker nor network");
                        }
                    }
                }
            }
            synchronized (this.zzbsX.zzbsS) {
                this.zzbsX.zzbsT.release();
                this.zzbsX.zzbsS.notifyAll();
                if (this == this.zzbsX.zzbsM) {
                    this.zzbsX.zzbsM = null;
                } else if (this == this.zzbsX.zzbsN) {
                    this.zzbsX.zzbsN = null;
                } else {
                    this.zzbsX.zzJt().zzLa().log("Current scheduler thread is neither worker nor network");
                }
            }
        }

        public void zzhf() {
            synchronized (this.zzbta) {
                this.zzbta.notifyAll();
            }
        }
    }

    zzato(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
    }

    private void zza(zzc<?> com_google_android_gms_internal_zzato_zzc_) {
        synchronized (this.zzbsS) {
            this.zzbsO.add(com_google_android_gms_internal_zzato_zzc_);
            if (this.zzbsM == null) {
                this.zzbsM = new zzd(this, "Measurement Worker", this.zzbsO);
                this.zzbsM.setUncaughtExceptionHandler(this.zzbsQ);
                this.zzbsM.start();
            } else {
                this.zzbsM.zzhf();
            }
        }
    }

    private void zza(FutureTask<?> futureTask) {
        synchronized (this.zzbsS) {
            this.zzbsP.add(futureTask);
            if (this.zzbsN == null) {
                this.zzbsN = new zzd(this, "Measurement Network", this.zzbsP);
                this.zzbsN.setUncaughtExceptionHandler(this.zzbsR);
                this.zzbsN.start();
            } else {
                this.zzbsN.zzhf();
            }
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzJd() {
        super.zzJd();
    }

    public /* bridge */ /* synthetic */ void zzJe() {
        super.zzJe();
    }

    public void zzJf() {
        if (Thread.currentThread() != this.zzbsN) {
            throw new IllegalStateException("Call expected from network thread");
        }
    }

    public /* bridge */ /* synthetic */ zzaso zzJg() {
        return super.zzJg();
    }

    public /* bridge */ /* synthetic */ zzass zzJh() {
        return super.zzJh();
    }

    public /* bridge */ /* synthetic */ zzatu zzJi() {
        return super.zzJi();
    }

    public /* bridge */ /* synthetic */ zzatf zzJj() {
        return super.zzJj();
    }

    public /* bridge */ /* synthetic */ zzasw zzJk() {
        return super.zzJk();
    }

    public /* bridge */ /* synthetic */ zzatw zzJl() {
        return super.zzJl();
    }

    public /* bridge */ /* synthetic */ zzatv zzJm() {
        return super.zzJm();
    }

    public /* bridge */ /* synthetic */ zzatg zzJn() {
        return super.zzJn();
    }

    public /* bridge */ /* synthetic */ zzasu zzJo() {
        return super.zzJo();
    }

    public /* bridge */ /* synthetic */ zzaue zzJp() {
        return super.zzJp();
    }

    public /* bridge */ /* synthetic */ zzatn zzJq() {
        return super.zzJq();
    }

    public /* bridge */ /* synthetic */ zzaty zzJr() {
        return super.zzJr();
    }

    public /* bridge */ /* synthetic */ zzato zzJs() {
        return super.zzJs();
    }

    public /* bridge */ /* synthetic */ zzati zzJt() {
        return super.zzJt();
    }

    public /* bridge */ /* synthetic */ zzatl zzJu() {
        return super.zzJu();
    }

    public /* bridge */ /* synthetic */ zzast zzJv() {
        return super.zzJv();
    }

    public boolean zzLr() {
        return Thread.currentThread() == this.zzbsM;
    }

    public boolean zzbd() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public <V> Future<V> zzd(Callable<V> callable) throws IllegalStateException {
        zznA();
        zzac.zzw(callable);
        zzc com_google_android_gms_internal_zzato_zzc = new zzc(this, (Callable) callable, false, "Task exception on worker thread");
        if (Thread.currentThread() == this.zzbsM) {
            com_google_android_gms_internal_zzato_zzc.run();
        } else {
            zza(com_google_android_gms_internal_zzato_zzc);
        }
        return com_google_android_gms_internal_zzato_zzc;
    }

    public <V> Future<V> zze(Callable<V> callable) throws IllegalStateException {
        zznA();
        zzac.zzw(callable);
        zzc com_google_android_gms_internal_zzato_zzc = new zzc(this, (Callable) callable, true, "Task exception on worker thread");
        if (Thread.currentThread() == this.zzbsM) {
            com_google_android_gms_internal_zzato_zzc.run();
        } else {
            zza(com_google_android_gms_internal_zzato_zzc);
        }
        return com_google_android_gms_internal_zzato_zzc;
    }

    public void zzm(Runnable runnable) throws IllegalStateException {
        zznA();
        zzac.zzw(runnable);
        zza(new zzc(this, runnable, false, "Task exception on worker thread"));
    }

    public void zzmq() {
        if (Thread.currentThread() != this.zzbsM) {
            throw new IllegalStateException("Call expected from worker thread");
        }
    }

    protected void zzmr() {
    }

    public void zzn(Runnable runnable) throws IllegalStateException {
        zznA();
        zzac.zzw(runnable);
        zza(new zzc(this, runnable, false, "Task exception on network thread"));
    }

    public /* bridge */ /* synthetic */ zze zznq() {
        return super.zznq();
    }
}
