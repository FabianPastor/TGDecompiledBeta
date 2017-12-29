package com.google.android.gms.common.api.internal;

import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import com.google.android.gms.common.api.zze;
import java.lang.ref.WeakReference;
import java.util.NoSuchElementException;

final class zzdl implements DeathRecipient, zzdm {
    private final WeakReference<BasePendingResult<?>> zzfvl;
    private final WeakReference<zze> zzfvm;
    private final WeakReference<IBinder> zzfvn;

    private zzdl(BasePendingResult<?> basePendingResult, zze com_google_android_gms_common_api_zze, IBinder iBinder) {
        this.zzfvm = new WeakReference(com_google_android_gms_common_api_zze);
        this.zzfvl = new WeakReference(basePendingResult);
        this.zzfvn = new WeakReference(iBinder);
    }

    private final void zzajv() {
        BasePendingResult basePendingResult = (BasePendingResult) this.zzfvl.get();
        zze com_google_android_gms_common_api_zze = (zze) this.zzfvm.get();
        if (!(com_google_android_gms_common_api_zze == null || basePendingResult == null)) {
            com_google_android_gms_common_api_zze.remove(basePendingResult.zzagv().intValue());
        }
        IBinder iBinder = (IBinder) this.zzfvn.get();
        if (iBinder != null) {
            try {
                iBinder.unlinkToDeath(this, 0);
            } catch (NoSuchElementException e) {
            }
        }
    }

    public final void binderDied() {
        zzajv();
    }

    public final void zzc(BasePendingResult<?> basePendingResult) {
        zzajv();
    }
}
