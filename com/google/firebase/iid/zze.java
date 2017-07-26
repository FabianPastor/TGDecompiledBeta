package com.google.firebase.iid;

import android.content.Intent;
import android.util.Log;

final class zze implements Runnable {
    private /* synthetic */ Intent val$intent;
    private /* synthetic */ zzd zzckl;

    zze(zzd com_google_firebase_iid_zzd, Intent intent) {
        this.zzckl = com_google_firebase_iid_zzd;
        this.val$intent = intent;
    }

    public final void run() {
        String valueOf = String.valueOf(this.val$intent.getAction());
        Log.w("EnhancedIntentService", new StringBuilder(String.valueOf(valueOf).length() + 61).append("Service took too long to process intent: ").append(valueOf).append(" App may get closed.").toString());
        this.zzckl.finish();
    }
}
