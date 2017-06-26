package com.google.android.gms.internal;

final class zzj implements Runnable {
    private final zzp zzt;
    private final zzt zzu;
    private final Runnable zzv;

    public zzj(zzh com_google_android_gms_internal_zzh, zzp com_google_android_gms_internal_zzp, zzt com_google_android_gms_internal_zzt, Runnable runnable) {
        this.zzt = com_google_android_gms_internal_zzp;
        this.zzu = com_google_android_gms_internal_zzt;
        this.zzv = runnable;
    }

    public final void run() {
        if ((this.zzu.zzaf == null ? 1 : null) != null) {
            this.zzt.zza(this.zzu.result);
        } else {
            this.zzt.zzb(this.zzu.zzaf);
        }
        if (this.zzu.zzag) {
            this.zzt.zzb("intermediate-response");
        } else {
            this.zzt.zzc("done");
        }
        if (this.zzv != null) {
            this.zzv.run();
        }
    }
}
