package com.google.android.gms.common.internal;

import android.content.Intent;
import com.google.android.gms.internal.zzbds;

final class zzw extends zzt {
    private /* synthetic */ Intent val$intent;
    private /* synthetic */ int val$requestCode;
    private /* synthetic */ zzbds zzaHp;

    zzw(Intent intent, zzbds com_google_android_gms_internal_zzbds, int i) {
        this.val$intent = intent;
        this.zzaHp = com_google_android_gms_internal_zzbds;
        this.val$requestCode = i;
    }

    public final void zzrv() {
        if (this.val$intent != null) {
            this.zzaHp.startActivityForResult(this.val$intent, this.val$requestCode);
        }
    }
}
