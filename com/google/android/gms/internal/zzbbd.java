package com.google.android.gms.internal;

import android.app.Dialog;

final class zzbbd extends zzbdl {
    private /* synthetic */ Dialog zzaBT;
    private /* synthetic */ zzbbc zzaBU;

    zzbbd(zzbbc com_google_android_gms_internal_zzbbc, Dialog dialog) {
        this.zzaBU = com_google_android_gms_internal_zzbbc;
        this.zzaBT = dialog;
    }

    public final void zzpA() {
        this.zzaBU.zzaBS.zzpx();
        if (this.zzaBT.isShowing()) {
            this.zzaBT.dismiss();
        }
    }
}
