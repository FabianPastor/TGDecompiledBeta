package com.google.android.gms.common.api.internal;

import android.app.Dialog;

final class zzr extends zzby {
    private /* synthetic */ Dialog zzfor;
    private /* synthetic */ zzq zzfos;

    zzr(zzq com_google_android_gms_common_api_internal_zzq, Dialog dialog) {
        this.zzfos = com_google_android_gms_common_api_internal_zzq;
        this.zzfor = dialog;
    }

    public final void zzahg() {
        this.zzfos.zzfoq.zzahd();
        if (this.zzfor.isShowing()) {
            this.zzfor.dismiss();
        }
    }
}
