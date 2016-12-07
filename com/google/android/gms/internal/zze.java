package com.google.android.gms.internal;

import android.os.Handler;
import java.util.concurrent.Executor;

public class zze implements zzn {
    private final Executor zzr;

    private class zza implements Runnable {
        private final zzk zzt;
        private final zzm zzu;
        private final Runnable zzv;

        public zza(zze com_google_android_gms_internal_zze, zzk com_google_android_gms_internal_zzk, zzm com_google_android_gms_internal_zzm, Runnable runnable) {
            this.zzt = com_google_android_gms_internal_zzk;
            this.zzu = com_google_android_gms_internal_zzm;
            this.zzv = runnable;
        }

        public void run() {
            if (this.zzu.isSuccess()) {
                this.zzt.zza(this.zzu.result);
            } else {
                this.zzt.zzc(this.zzu.zzaf);
            }
            if (this.zzu.zzag) {
                this.zzt.zzc("intermediate-response");
            } else {
                this.zzt.zzd("done");
            }
            if (this.zzv != null) {
                this.zzv.run();
            }
        }
    }

    public zze(final Handler handler) {
        this.zzr = new Executor(this) {
            public void execute(Runnable runnable) {
                handler.post(runnable);
            }
        };
    }

    public void zza(zzk<?> com_google_android_gms_internal_zzk_, zzm<?> com_google_android_gms_internal_zzm_) {
        zza(com_google_android_gms_internal_zzk_, com_google_android_gms_internal_zzm_, null);
    }

    public void zza(zzk<?> com_google_android_gms_internal_zzk_, zzm<?> com_google_android_gms_internal_zzm_, Runnable runnable) {
        com_google_android_gms_internal_zzk_.zzr();
        com_google_android_gms_internal_zzk_.zzc("post-response");
        this.zzr.execute(new zza(this, com_google_android_gms_internal_zzk_, com_google_android_gms_internal_zzm_, runnable));
    }

    public void zza(zzk<?> com_google_android_gms_internal_zzk_, zzr com_google_android_gms_internal_zzr) {
        com_google_android_gms_internal_zzk_.zzc("post-error");
        this.zzr.execute(new zza(this, com_google_android_gms_internal_zzk_, zzm.zzd(com_google_android_gms_internal_zzr), null));
    }
}
