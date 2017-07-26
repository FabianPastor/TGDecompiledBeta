package com.google.android.gms.wearable.internal;

import com.google.android.gms.internal.zzbdz;
import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;

final class zzgg implements zzbdz<CapabilityListener> {
    private /* synthetic */ zzaa zzbTB;

    zzgg(zzaa com_google_android_gms_wearable_internal_zzaa) {
        this.zzbTB = com_google_android_gms_wearable_internal_zzaa;
    }

    public final void zzpT() {
    }

    public final /* synthetic */ void zzq(Object obj) {
        ((CapabilityListener) obj).onCapabilityChanged(this.zzbTB);
    }
}
