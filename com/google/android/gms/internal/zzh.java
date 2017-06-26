package com.google.android.gms.internal;

import android.os.Handler;
import java.util.concurrent.Executor;

public final class zzh implements zzw {
    private final Executor zzr;

    public zzh(Handler handler) {
        this.zzr = new zzi(this, handler);
    }

    public final void zza(zzp<?> com_google_android_gms_internal_zzp_, zzaa com_google_android_gms_internal_zzaa) {
        com_google_android_gms_internal_zzp_.zzb("post-error");
        this.zzr.execute(new zzj(this, com_google_android_gms_internal_zzp_, zzt.zzc(com_google_android_gms_internal_zzaa), null));
    }

    public final void zza(zzp<?> com_google_android_gms_internal_zzp_, zzt<?> com_google_android_gms_internal_zzt_) {
        zza(com_google_android_gms_internal_zzp_, com_google_android_gms_internal_zzt_, null);
    }

    public final void zza(zzp<?> com_google_android_gms_internal_zzp_, zzt<?> com_google_android_gms_internal_zzt_, Runnable runnable) {
        com_google_android_gms_internal_zzp_.zzk();
        com_google_android_gms_internal_zzp_.zzb("post-response");
        this.zzr.execute(new zzj(this, com_google_android_gms_internal_zzp_, com_google_android_gms_internal_zzt_, runnable));
    }
}
