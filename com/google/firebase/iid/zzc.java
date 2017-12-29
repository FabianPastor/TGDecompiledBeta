package com.google.firebase.iid;

import android.content.Intent;

final class zzc implements Runnable {
    private /* synthetic */ Intent val$intent;
    private /* synthetic */ Intent zzies;
    private /* synthetic */ zzb zznyj;

    zzc(zzb com_google_firebase_iid_zzb, Intent intent, Intent intent2) {
        this.zznyj = com_google_firebase_iid_zzb;
        this.val$intent = intent;
        this.zzies = intent2;
    }

    public final void run() {
        this.zznyj.handleIntent(this.val$intent);
        this.zznyj.zzh(this.zzies);
    }
}
