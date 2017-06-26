package com.google.firebase.iid;

import android.content.Intent;

final class zzc implements Runnable {
    private /* synthetic */ Intent val$intent;
    private /* synthetic */ Intent zzckc;
    private /* synthetic */ zzb zzckd;

    zzc(zzb com_google_firebase_iid_zzb, Intent intent, Intent intent2) {
        this.zzckd = com_google_firebase_iid_zzb;
        this.val$intent = intent;
        this.zzckc = intent2;
    }

    public final void run() {
        this.zzckd.handleIntent(this.val$intent);
        this.zzckd.zzm(this.zzckc);
    }
}
