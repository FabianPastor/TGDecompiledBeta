package com.google.android.gms.internal;

final class zzbdb implements zzbaw {
    private /* synthetic */ zzbda zzaEm;

    zzbdb(zzbda com_google_android_gms_internal_zzbda) {
        this.zzaEm = com_google_android_gms_internal_zzbda;
    }

    public final void zzac(boolean z) {
        this.zzaEm.mHandler.sendMessage(this.zzaEm.mHandler.obtainMessage(1, Boolean.valueOf(z)));
    }
}
