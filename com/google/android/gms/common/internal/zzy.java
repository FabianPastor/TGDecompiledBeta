package com.google.android.gms.common.internal;

import android.content.Intent;
import com.google.android.gms.common.api.internal.zzcf;

final class zzy extends zzv {
    private /* synthetic */ Intent val$intent;
    private /* synthetic */ int val$requestCode;
    private /* synthetic */ zzcf zzfzm;

    zzy(Intent intent, zzcf com_google_android_gms_common_api_internal_zzcf, int i) {
        this.val$intent = intent;
        this.zzfzm = com_google_android_gms_common_api_internal_zzcf;
        this.val$requestCode = i;
    }

    public final void zzale() {
        if (this.val$intent != null) {
            this.zzfzm.startActivityForResult(this.val$intent, this.val$requestCode);
        }
    }
}
