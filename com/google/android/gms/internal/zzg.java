package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.net.TrafficStats;
import android.os.Build.VERSION;
import android.os.Process;
import android.os.SystemClock;
import java.util.concurrent.BlockingQueue;

public class zzg extends Thread {
    private final zzb zzi;
    private final zzn zzj;
    private volatile boolean zzk = false;
    private final BlockingQueue<zzk<?>> zzw;
    private final zzf zzx;

    public zzg(BlockingQueue<zzk<?>> blockingQueue, zzf com_google_android_gms_internal_zzf, zzb com_google_android_gms_internal_zzb, zzn com_google_android_gms_internal_zzn) {
        super("VolleyNetworkDispatcher");
        this.zzw = blockingQueue;
        this.zzx = com_google_android_gms_internal_zzf;
        this.zzi = com_google_android_gms_internal_zzb;
        this.zzj = com_google_android_gms_internal_zzn;
    }

    @TargetApi(14)
    private void zzb(zzk<?> com_google_android_gms_internal_zzk_) {
        if (VERSION.SDK_INT >= 14) {
            TrafficStats.setThreadStatsTag(com_google_android_gms_internal_zzk_.zzf());
        }
    }

    private void zzb(zzk<?> com_google_android_gms_internal_zzk_, zzr com_google_android_gms_internal_zzr) {
        this.zzj.zza((zzk) com_google_android_gms_internal_zzk_, com_google_android_gms_internal_zzk_.zzb(com_google_android_gms_internal_zzr));
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
                zzk com_google_android_gms_internal_zzk = (zzk) this.zzw.take();
                try {
                    com_google_android_gms_internal_zzk.zzc("network-queue-take");
                    zzb(com_google_android_gms_internal_zzk);
                    zzi zza = this.zzx.zza(com_google_android_gms_internal_zzk);
                    com_google_android_gms_internal_zzk.zzc("network-http-complete");
                    if (zza.zzz && com_google_android_gms_internal_zzk.zzs()) {
                        com_google_android_gms_internal_zzk.zzd("not-modified");
                    } else {
                        zzm zza2 = com_google_android_gms_internal_zzk.zza(zza);
                        com_google_android_gms_internal_zzk.zzc("network-parse-complete");
                        if (com_google_android_gms_internal_zzk.zzn() && zza2.zzae != null) {
                            this.zzi.zza(com_google_android_gms_internal_zzk.zzg(), zza2.zzae);
                            com_google_android_gms_internal_zzk.zzc("network-cache-written");
                        }
                        com_google_android_gms_internal_zzk.zzr();
                        this.zzj.zza(com_google_android_gms_internal_zzk, zza2);
                    }
                } catch (zzr e) {
                    e.zza(SystemClock.elapsedRealtime() - elapsedRealtime);
                    zzb(com_google_android_gms_internal_zzk, e);
                } catch (Throwable e2) {
                    zzs.zza(e2, "Unhandled exception %s", e2.toString());
                    zzr com_google_android_gms_internal_zzr = new zzr(e2);
                    com_google_android_gms_internal_zzr.zza(SystemClock.elapsedRealtime() - elapsedRealtime);
                    this.zzj.zza(com_google_android_gms_internal_zzk, com_google_android_gms_internal_zzr);
                }
            } catch (InterruptedException e3) {
                if (this.zzk) {
                    return;
                }
            }
        }
    }
}
