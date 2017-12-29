package com.google.android.gms.internal;

import android.os.Process;
import com.google.android.gms.common.internal.zzbq;
import java.util.concurrent.BlockingQueue;

final class zzcil extends Thread {
    private /* synthetic */ zzcih zzjeq;
    private final Object zzjet = new Object();
    private final BlockingQueue<zzcik<?>> zzjeu;

    public zzcil(zzcih com_google_android_gms_internal_zzcih, String str, BlockingQueue<zzcik<?>> blockingQueue) {
        this.zzjeq = com_google_android_gms_internal_zzcih;
        zzbq.checkNotNull(str);
        zzbq.checkNotNull(blockingQueue);
        this.zzjeu = blockingQueue;
        setName(str);
    }

    private final void zza(InterruptedException interruptedException) {
        this.zzjeq.zzawy().zzazf().zzj(String.valueOf(getName()).concat(" was interrupted"), interruptedException);
    }

    public final void run() {
        Object obj = null;
        while (obj == null) {
            try {
                this.zzjeq.zzjem.acquire();
                obj = 1;
            } catch (InterruptedException e) {
                zza(e);
            }
        }
        int threadPriority = Process.getThreadPriority(Process.myTid());
        while (true) {
            zzcik com_google_android_gms_internal_zzcik = (zzcik) this.zzjeu.poll();
            if (com_google_android_gms_internal_zzcik != null) {
                Process.setThreadPriority(com_google_android_gms_internal_zzcik.zzjes ? threadPriority : 10);
                com_google_android_gms_internal_zzcik.run();
            } else {
                try {
                    synchronized (this.zzjet) {
                        if (this.zzjeu.peek() == null && !this.zzjeq.zzjen) {
                            try {
                                this.zzjet.wait(30000);
                            } catch (InterruptedException e2) {
                                zza(e2);
                            }
                        }
                    }
                    synchronized (this.zzjeq.zzjel) {
                        if (this.zzjeu.peek() == null) {
                            break;
                        }
                    }
                } catch (Throwable th) {
                    synchronized (this.zzjeq.zzjel) {
                        this.zzjeq.zzjem.release();
                        this.zzjeq.zzjel.notifyAll();
                        if (this == this.zzjeq.zzjef) {
                            this.zzjeq.zzjef = null;
                        } else if (this == this.zzjeq.zzjeg) {
                            this.zzjeq.zzjeg = null;
                        } else {
                            this.zzjeq.zzawy().zzazd().log("Current scheduler thread is neither worker nor network");
                        }
                    }
                }
            }
        }
        synchronized (this.zzjeq.zzjel) {
            this.zzjeq.zzjem.release();
            this.zzjeq.zzjel.notifyAll();
            if (this == this.zzjeq.zzjef) {
                this.zzjeq.zzjef = null;
            } else if (this == this.zzjeq.zzjeg) {
                this.zzjeq.zzjeg = null;
            } else {
                this.zzjeq.zzawy().zzazd().log("Current scheduler thread is neither worker nor network");
            }
        }
    }

    public final void zzrk() {
        synchronized (this.zzjet) {
            this.zzjet.notifyAll();
        }
    }
}
