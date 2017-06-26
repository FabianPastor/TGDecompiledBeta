package com.google.android.gms.internal;

import android.os.Process;
import java.util.concurrent.BlockingQueue;

public final class zzd extends Thread {
    private static final boolean DEBUG = zzab.DEBUG;
    private final BlockingQueue<zzp<?>> zzg;
    private final BlockingQueue<zzp<?>> zzh;
    private final zzb zzi;
    private final zzw zzj;
    private volatile boolean zzk = false;

    public zzd(BlockingQueue<zzp<?>> blockingQueue, BlockingQueue<zzp<?>> blockingQueue2, zzb com_google_android_gms_internal_zzb, zzw com_google_android_gms_internal_zzw) {
        this.zzg = blockingQueue;
        this.zzh = blockingQueue2;
        this.zzi = com_google_android_gms_internal_zzb;
        this.zzj = com_google_android_gms_internal_zzw;
    }

    public final void quit() {
        this.zzk = true;
        interrupt();
    }

    public final void run() {
        if (DEBUG) {
            zzab.zza("start new dispatcher", new Object[0]);
        }
        Process.setThreadPriority(10);
        this.zzi.initialize();
        while (true) {
            try {
                zzp com_google_android_gms_internal_zzp = (zzp) this.zzg.take();
                com_google_android_gms_internal_zzp.zzb("cache-queue-take");
                zzc zza = this.zzi.zza(com_google_android_gms_internal_zzp.getUrl());
                if (zza == null) {
                    com_google_android_gms_internal_zzp.zzb("cache-miss");
                    this.zzh.put(com_google_android_gms_internal_zzp);
                } else {
                    if ((zza.zzd < System.currentTimeMillis() ? 1 : 0) != 0) {
                        com_google_android_gms_internal_zzp.zzb("cache-hit-expired");
                        com_google_android_gms_internal_zzp.zza(zza);
                        this.zzh.put(com_google_android_gms_internal_zzp);
                    } else {
                        com_google_android_gms_internal_zzp.zzb("cache-hit");
                        zzt zza2 = com_google_android_gms_internal_zzp.zza(new zzn(zza.data, zza.zzf));
                        com_google_android_gms_internal_zzp.zzb("cache-hit-parsed");
                        if ((zza.zze < System.currentTimeMillis() ? 1 : 0) == 0) {
                            this.zzj.zza(com_google_android_gms_internal_zzp, zza2);
                        } else {
                            com_google_android_gms_internal_zzp.zzb("cache-hit-refresh-needed");
                            com_google_android_gms_internal_zzp.zza(zza);
                            zza2.zzag = true;
                            this.zzj.zza(com_google_android_gms_internal_zzp, zza2, new zze(this, com_google_android_gms_internal_zzp));
                        }
                    }
                }
            } catch (InterruptedException e) {
                if (this.zzk) {
                    return;
                }
            }
        }
    }
}
