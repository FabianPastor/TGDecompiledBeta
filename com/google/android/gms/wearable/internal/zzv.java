package com.google.android.gms.wearable.internal;

import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;
import com.google.android.gms.wearable.CapabilityInfo;

final class zzv implements CapabilityListener {
    private CapabilityListener zzbRY;
    private String zzbRZ;

    zzv(CapabilityListener capabilityListener, String str) {
        this.zzbRY = capabilityListener;
        this.zzbRZ = str;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        zzv com_google_android_gms_wearable_internal_zzv = (zzv) obj;
        return this.zzbRY.equals(com_google_android_gms_wearable_internal_zzv.zzbRY) ? this.zzbRZ.equals(com_google_android_gms_wearable_internal_zzv.zzbRZ) : false;
    }

    public final int hashCode() {
        return (this.zzbRY.hashCode() * 31) + this.zzbRZ.hashCode();
    }

    public final void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        this.zzbRY.onCapabilityChanged(capabilityInfo);
    }
}
