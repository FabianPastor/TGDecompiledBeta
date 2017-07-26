package com.google.android.gms.internal;

import com.google.android.gms.common.api.PendingResult.zza;
import com.google.android.gms.common.api.Status;

final class zzbbu implements zza {
    private /* synthetic */ zzbbe zzaCT;
    private /* synthetic */ zzbbt zzaCU;

    zzbbu(zzbbt com_google_android_gms_internal_zzbbt, zzbbe com_google_android_gms_internal_zzbbe) {
        this.zzaCU = com_google_android_gms_internal_zzbbt;
        this.zzaCT = com_google_android_gms_internal_zzbbe;
    }

    public final void zzo(Status status) {
        this.zzaCU.zzaCR.remove(this.zzaCT);
    }
}
