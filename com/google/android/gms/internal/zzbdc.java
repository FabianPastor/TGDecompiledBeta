package com.google.android.gms.internal;

final class zzbdc implements zzbax {
    private /* synthetic */ zzbdb zzaEm;

    zzbdc(zzbdb com_google_android_gms_internal_zzbdb) {
        this.zzaEm = com_google_android_gms_internal_zzbdb;
    }

    public final void zzac(boolean z) {
        this.zzaEm.mHandler.sendMessage(this.zzaEm.mHandler.obtainMessage(1, Boolean.valueOf(z)));
    }
}
