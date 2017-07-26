package com.google.firebase.iid;

import android.util.Log;

final class zzg implements Runnable {
    private /* synthetic */ zzd zzckn;
    private /* synthetic */ zzf zzcko;

    zzg(zzf com_google_firebase_iid_zzf, zzd com_google_firebase_iid_zzd) {
        this.zzcko = com_google_firebase_iid_zzf;
        this.zzckn = com_google_firebase_iid_zzd;
    }

    public final void run() {
        if (Log.isLoggable("EnhancedIntentService", 3)) {
            Log.d("EnhancedIntentService", "bg processing of the intent starting now");
        }
        this.zzcko.zzckm.handleIntent(this.zzckn.intent);
        this.zzckn.finish();
    }
}
