package com.google.android.gms.internal;

import android.app.Dialog;

final class zzbbc extends zzbdk {
    private /* synthetic */ Dialog zzaBT;
    private /* synthetic */ zzbbb zzaBU;

    zzbbc(zzbbb com_google_android_gms_internal_zzbbb, Dialog dialog) {
        this.zzaBU = com_google_android_gms_internal_zzbbb;
        this.zzaBT = dialog;
    }

    public final void zzpA() {
        this.zzaBU.zzaBS.zzpx();
        if (this.zzaBT.isShowing()) {
            this.zzaBT.dismiss();
        }
    }
}
