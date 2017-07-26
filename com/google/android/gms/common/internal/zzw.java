package com.google.android.gms.common.internal;

import android.content.Intent;
import com.google.android.gms.internal.zzbdt;

final class zzw extends zzt {
    private /* synthetic */ Intent val$intent;
    private /* synthetic */ int val$requestCode;
    private /* synthetic */ zzbdt zzaHp;

    zzw(Intent intent, zzbdt com_google_android_gms_internal_zzbdt, int i) {
        this.val$intent = intent;
        this.zzaHp = com_google_android_gms_internal_zzbdt;
        this.val$requestCode = i;
    }

    public final void zzrv() {
        if (this.val$intent != null) {
            this.zzaHp.startActivityForResult(this.val$intent, this.val$requestCode);
        }
    }
}
