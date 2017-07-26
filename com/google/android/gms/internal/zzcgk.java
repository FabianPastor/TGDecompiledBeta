package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;

final class zzcgk extends Thread {
    private /* synthetic */ zzcgg zzbsh;
    private final Object zzbsk = new Object();
    private final BlockingQueue<FutureTask<?>> zzbsl;

    public zzcgk(zzcgg com_google_android_gms_internal_zzcgg, String str, BlockingQueue<FutureTask<?>> blockingQueue) {
        this.zzbsh = com_google_android_gms_internal_zzcgg;
        zzbo.zzu(str);
        zzbo.zzu(blockingQueue);
        this.zzbsl = blockingQueue;
        setName(str);
    }

    private final void zza(InterruptedException interruptedException) {
        this.zzbsh.zzwF().zzyz().zzj(String.valueOf(getName()).concat(" was interrupted"), interruptedException);
    }

    public final void run() {
        Object obj = null;
        while (obj == null) {
            try {
                this.zzbsh.zzbsd.acquire();
                obj = 1;
            } catch (InterruptedException e) {
                zza(e);
            }
        }
        while (true) {
            try {
                FutureTask futureTask = (FutureTask) this.zzbsl.poll();
                if (futureTask != null) {
                    futureTask.run();
                } else {
                    synchronized (this.zzbsk) {
                        if (this.zzbsl.peek() == null && !this.zzbsh.zzbse) {
                            try {
                                this.zzbsk.wait(30000);
                            } catch (InterruptedException e2) {
                                zza(e2);
                            }
                        }
                    }
                    synchronized (this.zzbsh.zzbsc) {
                        if (this.zzbsl.peek() == null) {
                            break;
                        }
                    }
                }
            } catch (Throwable th) {
                synchronized (this.zzbsh.zzbsc) {
                    this.zzbsh.zzbsd.release();
                    this.zzbsh.zzbsc.notifyAll();
                    if (this == this.zzbsh.zzbrW) {
                        this.zzbsh.zzbrW = null;
                    } else if (this == this.zzbsh.zzbrX) {
                        this.zzbsh.zzbrX = null;
                    } else {
                        this.zzbsh.zzwF().zzyx().log("Current scheduler thread is neither worker nor network");
                    }
                }
            }
        }
        synchronized (this.zzbsh.zzbsc) {
            this.zzbsh.zzbsd.release();
            this.zzbsh.zzbsc.notifyAll();
            if (this == this.zzbsh.zzbrW) {
                this.zzbsh.zzbrW = null;
            } else if (this == this.zzbsh.zzbrX) {
                this.zzbsh.zzbrX = null;
            } else {
                this.zzbsh.zzwF().zzyx().log("Current scheduler thread is neither worker nor network");
            }
        }
    }

    public final void zzfF() {
        synchronized (this.zzbsk) {
            this.zzbsk.notifyAll();
        }
    }
}
