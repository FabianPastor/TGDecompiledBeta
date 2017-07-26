package com.google.android.gms.internal;

abstract class zzbcy {
    private final zzbcw zzaDZ;

    protected zzbcy(zzbcw com_google_android_gms_internal_zzbcw) {
        this.zzaDZ = com_google_android_gms_internal_zzbcw;
    }

    public final void zzc(zzbcx com_google_android_gms_internal_zzbcx) {
        com_google_android_gms_internal_zzbcx.zzaCv.lock();
        try {
            if (com_google_android_gms_internal_zzbcx.zzaDV == this.zzaDZ) {
                zzpV();
                com_google_android_gms_internal_zzbcx.zzaCv.unlock();
            }
        } finally {
            com_google_android_gms_internal_zzbcx.zzaCv.unlock();
        }
    }

    protected abstract void zzpV();
}
