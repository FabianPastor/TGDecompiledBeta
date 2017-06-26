package com.google.android.gms.internal;

import android.support.annotation.BinderThread;
import java.lang.ref.WeakReference;

final class zzbcj extends zzcto {
    private final WeakReference<zzbcc> zzaDq;

    zzbcj(zzbcc com_google_android_gms_internal_zzbcc) {
        this.zzaDq = new WeakReference(com_google_android_gms_internal_zzbcc);
    }

    @BinderThread
    public final void zzb(zzctw com_google_android_gms_internal_zzctw) {
        zzbcc com_google_android_gms_internal_zzbcc = (zzbcc) this.zzaDq.get();
        if (com_google_android_gms_internal_zzbcc != null) {
            com_google_android_gms_internal_zzbcc.zzaCZ.zza(new zzbck(this, com_google_android_gms_internal_zzbcc, com_google_android_gms_internal_zzbcc, com_google_android_gms_internal_zzctw));
        }
    }
}
