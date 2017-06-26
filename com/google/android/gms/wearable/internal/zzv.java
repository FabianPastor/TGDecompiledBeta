package com.google.android.gms.wearable.internal;

import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;
import com.google.android.gms.wearable.CapabilityInfo;

final class zzv implements CapabilityListener {
    private CapabilityListener zzbRW;
    private String zzbRX;

    zzv(CapabilityListener capabilityListener, String str) {
        this.zzbRW = capabilityListener;
        this.zzbRX = str;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        zzv com_google_android_gms_wearable_internal_zzv = (zzv) obj;
        return this.zzbRW.equals(com_google_android_gms_wearable_internal_zzv.zzbRW) ? this.zzbRX.equals(com_google_android_gms_wearable_internal_zzv.zzbRX) : false;
    }

    public final int hashCode() {
        return (this.zzbRW.hashCode() * 31) + this.zzbRX.hashCode();
    }

    public final void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        this.zzbRW.onCapabilityChanged(capabilityInfo);
    }
}
