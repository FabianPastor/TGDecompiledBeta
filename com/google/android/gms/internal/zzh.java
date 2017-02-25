package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.net.TrafficStats;
import android.os.Build.VERSION;
import android.os.Process;
import android.os.SystemClock;
import java.util.concurrent.BlockingQueue;

public class zzh extends Thread {
    private final zzb zzi;
    private final zzo zzj;
    private volatile boolean zzk = false;
    private final BlockingQueue<zzl<?>> zzx;
    private final zzg zzy;

    public zzh(BlockingQueue<zzl<?>> blockingQueue, zzg com_google_android_gms_internal_zzg, zzb com_google_android_gms_internal_zzb, zzo com_google_android_gms_internal_zzo) {
        this.zzx = blockingQueue;
        this.zzy = com_google_android_gms_internal_zzg;
        this.zzi = com_google_android_gms_internal_zzb;
        this.zzj = com_google_android_gms_internal_zzo;
    }

    @TargetApi(14)
    private void zzb(zzl<?> com_google_android_gms_internal_zzl_) {
        int i = VERSION.SDK_INT;
        TrafficStats.setThreadStatsTag(com_google_android_gms_internal_zzl_.zzf());
    }

    private void zzb(zzl<?> com_google_android_gms_internal_zzl_, zzs com_google_android_gms_internal_zzs) {
        this.zzj.zza((zzl) com_google_android_gms_internal_zzl_, com_google_android_gms_internal_zzl_.zzb(com_google_android_gms_internal_zzs));
    }

    public void quit() {
        this.zzk = true;
        interrupt();
    }

    public void run() {
        Process.setThreadPriority(10);
        while (true) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            try {
                zzl com_google_android_gms_internal_zzl = (zzl) this.zzx.take();
                try {
                    com_google_android_gms_internal_zzl.zzc("network-queue-take");
                    zzb(com_google_android_gms_internal_zzl);
                    zzj zza = this.zzy.zza(com_google_android_gms_internal_zzl);
                    com_google_android_gms_internal_zzl.zzc("network-http-complete");
                    if (zza.zzA && com_google_android_gms_internal_zzl.zzs()) {
                        com_google_android_gms_internal_zzl.zzd("not-modified");
                    } else {
                        zzn zza2 = com_google_android_gms_internal_zzl.zza(zza);
                        com_google_android_gms_internal_zzl.zzc("network-parse-complete");
                        if (com_google_android_gms_internal_zzl.zzn() && zza2.zzaf != null) {
                            this.zzi.zza(com_google_android_gms_internal_zzl.zzg(), zza2.zzaf);
                            com_google_android_gms_internal_zzl.zzc("network-cache-written");
                        }
                        com_google_android_gms_internal_zzl.zzr();
                        this.zzj.zza(com_google_android_gms_internal_zzl, zza2);
                    }
                } catch (zzs e) {
                    e.zza(SystemClock.elapsedRealtime() - elapsedRealtime);
                    zzb(com_google_android_gms_internal_zzl, e);
                } catch (Throwable e2) {
                    zzt.zza(e2, "Unhandled exception %s", e2.toString());
                    zzs com_google_android_gms_internal_zzs = new zzs(e2);
                    com_google_android_gms_internal_zzs.zza(SystemClock.elapsedRealtime() - elapsedRealtime);
                    this.zzj.zza(com_google_android_gms_internal_zzl, com_google_android_gms_internal_zzs);
                }
            } catch (InterruptedException e3) {
                if (this.zzk) {
                    return;
                }
            }
        }
    }
}
