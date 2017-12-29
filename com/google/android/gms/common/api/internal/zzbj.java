package com.google.android.gms.common.api.internal;

abstract class zzbj {
    private final zzbh zzfsv;

    protected zzbj(zzbh com_google_android_gms_common_api_internal_zzbh) {
        this.zzfsv = com_google_android_gms_common_api_internal_zzbh;
    }

    protected abstract void zzaib();

    public final void zzc(zzbi com_google_android_gms_common_api_internal_zzbi) {
        com_google_android_gms_common_api_internal_zzbi.zzfps.lock();
        try {
            if (com_google_android_gms_common_api_internal_zzbi.zzfsr == this.zzfsv) {
                zzaib();
                com_google_android_gms_common_api_internal_zzbi.zzfps.unlock();
            }
        } finally {
            com_google_android_gms_common_api_internal_zzbi.zzfps.unlock();
        }
    }
}
