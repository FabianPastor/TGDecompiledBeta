package com.google.android.gms.internal;

import android.support.annotation.BinderThread;
import java.lang.ref.WeakReference;

final class zzbck extends zzctp {
    private final WeakReference<zzbcd> zzaDq;

    zzbck(zzbcd com_google_android_gms_internal_zzbcd) {
        this.zzaDq = new WeakReference(com_google_android_gms_internal_zzbcd);
    }

    @BinderThread
    public final void zzb(zzctx com_google_android_gms_internal_zzctx) {
        zzbcd com_google_android_gms_internal_zzbcd = (zzbcd) this.zzaDq.get();
        if (com_google_android_gms_internal_zzbcd != null) {
            com_google_android_gms_internal_zzbcd.zzaCZ.zza(new zzbcl(this, com_google_android_gms_internal_zzbcd, com_google_android_gms_internal_zzbcd, com_google_android_gms_internal_zzctx));
        }
    }
}
