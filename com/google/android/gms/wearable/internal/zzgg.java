package com.google.android.gms.wearable.internal;

import com.google.android.gms.internal.zzbdy;
import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;

final class zzgg implements zzbdy<CapabilityListener> {
    private /* synthetic */ zzaa zzbTz;

    zzgg(zzaa com_google_android_gms_wearable_internal_zzaa) {
        this.zzbTz = com_google_android_gms_wearable_internal_zzaa;
    }

    public final void zzpT() {
    }

    public final /* synthetic */ void zzq(Object obj) {
        ((CapabilityListener) obj).onCapabilityChanged(this.zzbTz);
    }
}
