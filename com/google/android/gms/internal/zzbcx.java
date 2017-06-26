package com.google.android.gms.internal;

abstract class zzbcx {
    private final zzbcv zzaDZ;

    protected zzbcx(zzbcv com_google_android_gms_internal_zzbcv) {
        this.zzaDZ = com_google_android_gms_internal_zzbcv;
    }

    public final void zzc(zzbcw com_google_android_gms_internal_zzbcw) {
        com_google_android_gms_internal_zzbcw.zzaCv.lock();
        try {
            if (com_google_android_gms_internal_zzbcw.zzaDV == this.zzaDZ) {
                zzpV();
                com_google_android_gms_internal_zzbcw.zzaCv.unlock();
            }
        } finally {
            com_google_android_gms_internal_zzbcw.zzaCv.unlock();
        }
    }

    protected abstract void zzpV();
}
