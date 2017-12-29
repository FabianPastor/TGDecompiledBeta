package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.zzcl;
import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;

final class zzho implements zzcl<CapabilityListener> {
    private /* synthetic */ zzah zzlmc;

    zzho(zzah com_google_android_gms_wearable_internal_zzah) {
        this.zzlmc = com_google_android_gms_wearable_internal_zzah;
    }

    public final void zzahz() {
    }

    public final /* synthetic */ void zzu(Object obj) {
        ((CapabilityListener) obj).onCapabilityChanged(this.zzlmc);
    }
}
