package com.google.android.gms.internal;

import android.net.TrafficStats;
import android.os.Process;
import android.os.SystemClock;
import java.util.concurrent.BlockingQueue;

public final class zzl extends Thread {
    private final zzb zzi;
    private final zzw zzj;
    private volatile boolean zzk = false;
    private final BlockingQueue<zzp<?>> zzw;
    private final zzk zzx;

    public zzl(BlockingQueue<zzp<?>> blockingQueue, zzk com_google_android_gms_internal_zzk, zzb com_google_android_gms_internal_zzb, zzw com_google_android_gms_internal_zzw) {
        this.zzw = blockingQueue;
        this.zzx = com_google_android_gms_internal_zzk;
        this.zzi = com_google_android_gms_internal_zzb;
        this.zzj = com_google_android_gms_internal_zzw;
    }

    public final void quit() {
        this.zzk = true;
        interrupt();
    }

    public final void run() {
        Process.setThreadPriority(10);
        while (true) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            try {
                zzp com_google_android_gms_internal_zzp = (zzp) this.zzw.take();
                try {
                    com_google_android_gms_internal_zzp.zzb("network-queue-take");
                    TrafficStats.setThreadStatsTag(com_google_android_gms_internal_zzp.zzc());
                    zzn zza = this.zzx.zza(com_google_android_gms_internal_zzp);
                    com_google_android_gms_internal_zzp.zzb("network-http-complete");
                    if (zza.zzz && com_google_android_gms_internal_zzp.zzl()) {
                        com_google_android_gms_internal_zzp.zzc("not-modified");
                    } else {
                        zzt zza2 = com_google_android_gms_internal_zzp.zza(zza);
                        com_google_android_gms_internal_zzp.zzb("network-parse-complete");
                        if (com_google_android_gms_internal_zzp.zzh() && zza2.zzae != null) {
                            this.zzi.zza(com_google_android_gms_internal_zzp.getUrl(), zza2.zzae);
                            com_google_android_gms_internal_zzp.zzb("network-cache-written");
                        }
                        com_google_android_gms_internal_zzp.zzk();
                        this.zzj.zza(com_google_android_gms_internal_zzp, zza2);
                    }
                } catch (zzaa e) {
                    e.zza(SystemClock.elapsedRealtime() - elapsedRealtime);
                    this.zzj.zza(com_google_android_gms_internal_zzp, e);
                } catch (Throwable e2) {
                    zzab.zza(e2, "Unhandled exception %s", e2.toString());
                    zzaa com_google_android_gms_internal_zzaa = new zzaa(e2);
                    com_google_android_gms_internal_zzaa.zza(SystemClock.elapsedRealtime() - elapsedRealtime);
                    this.zzj.zza(com_google_android_gms_internal_zzp, com_google_android_gms_internal_zzaa);
                }
            } catch (InterruptedException e3) {
                if (this.zzk) {
                    return;
                }
            }
        }
    }
}
