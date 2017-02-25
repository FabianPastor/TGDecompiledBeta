package com.google.android.gms.internal;

import android.os.Process;
import com.google.android.gms.internal.zzb.zza;
import java.util.concurrent.BlockingQueue;

public class zzc extends Thread {
    private static final boolean DEBUG = zzt.DEBUG;
    private final BlockingQueue<zzl<?>> zzg;
    private final BlockingQueue<zzl<?>> zzh;
    private final zzb zzi;
    private final zzo zzj;
    private volatile boolean zzk = false;

    public zzc(BlockingQueue<zzl<?>> blockingQueue, BlockingQueue<zzl<?>> blockingQueue2, zzb com_google_android_gms_internal_zzb, zzo com_google_android_gms_internal_zzo) {
        this.zzg = blockingQueue;
        this.zzh = blockingQueue2;
        this.zzi = com_google_android_gms_internal_zzb;
        this.zzj = com_google_android_gms_internal_zzo;
    }

    public void quit() {
        this.zzk = true;
        interrupt();
    }

    public void run() {
        if (DEBUG) {
            zzt.zza("start new dispatcher", new Object[0]);
        }
        Process.setThreadPriority(10);
        this.zzi.initialize();
        while (true) {
            try {
                final zzl com_google_android_gms_internal_zzl = (zzl) this.zzg.take();
                com_google_android_gms_internal_zzl.zzc("cache-queue-take");
                zza zza = this.zzi.zza(com_google_android_gms_internal_zzl.zzg());
                if (zza == null) {
                    com_google_android_gms_internal_zzl.zzc("cache-miss");
                    this.zzh.put(com_google_android_gms_internal_zzl);
                } else if (zza.zza()) {
                    com_google_android_gms_internal_zzl.zzc("cache-hit-expired");
                    com_google_android_gms_internal_zzl.zza(zza);
                    this.zzh.put(com_google_android_gms_internal_zzl);
                } else {
                    com_google_android_gms_internal_zzl.zzc("cache-hit");
                    zzn zza2 = com_google_android_gms_internal_zzl.zza(new zzj(zza.data, zza.zzf));
                    com_google_android_gms_internal_zzl.zzc("cache-hit-parsed");
                    if (zza.zzb()) {
                        com_google_android_gms_internal_zzl.zzc("cache-hit-refresh-needed");
                        com_google_android_gms_internal_zzl.zza(zza);
                        zza2.zzah = true;
                        this.zzj.zza(com_google_android_gms_internal_zzl, zza2, new Runnable(this) {
                            final /* synthetic */ zzc zzm;

                            public void run() {
                                try {
                                    this.zzm.zzh.put(com_google_android_gms_internal_zzl);
                                } catch (InterruptedException e) {
                                }
                            }
                        });
                    } else {
                        this.zzj.zza(com_google_android_gms_internal_zzl, zza2);
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
