package com.google.firebase.iid;

import android.content.Intent;

final class zzc implements Runnable {
    private /* synthetic */ Intent val$intent;
    private /* synthetic */ Intent zzckg;
    private /* synthetic */ zzb zzckh;

    zzc(zzb com_google_firebase_iid_zzb, Intent intent, Intent intent2) {
        this.zzckh = com_google_firebase_iid_zzb;
        this.val$intent = intent;
        this.zzckg = intent2;
    }

    public final void run() {
        this.zzckh.handleIntent(this.val$intent);
        this.zzckh.zzm(this.zzckg);
    }
}
