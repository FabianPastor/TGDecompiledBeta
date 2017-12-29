package com.google.android.gms.iid;

import android.content.Intent;

final class zzc implements Runnable {
    private /* synthetic */ Intent val$intent;
    private /* synthetic */ Intent zzies;
    private /* synthetic */ zzb zziet;

    zzc(zzb com_google_android_gms_iid_zzb, Intent intent, Intent intent2) {
        this.zziet = com_google_android_gms_iid_zzb;
        this.val$intent = intent;
        this.zzies = intent2;
    }

    public final void run() {
        this.zziet.handleIntent(this.val$intent);
        this.zziet.zzh(this.zzies);
    }
}
