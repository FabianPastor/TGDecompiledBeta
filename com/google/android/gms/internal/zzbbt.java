package com.google.android.gms.internal;

import com.google.android.gms.common.api.PendingResult.zza;
import com.google.android.gms.common.api.Status;

final class zzbbt implements zza {
    private /* synthetic */ zzbbd zzaCT;
    private /* synthetic */ zzbbs zzaCU;

    zzbbt(zzbbs com_google_android_gms_internal_zzbbs, zzbbd com_google_android_gms_internal_zzbbd) {
        this.zzaCU = com_google_android_gms_internal_zzbbs;
        this.zzaCT = com_google_android_gms_internal_zzbbd;
    }

    public final void zzo(Status status) {
        this.zzaCU.zzaCR.remove(this.zzaCT);
    }
}
