package com.google.firebase.iid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

final class zzab extends BroadcastReceiver {
    private zzaa zznzy;

    public zzab(zzaa com_google_firebase_iid_zzaa) {
        this.zznzy = com_google_firebase_iid_zzaa;
    }

    public final void onReceive(Context context, Intent intent) {
        if (this.zznzy != null && this.zznzy.zzcjp()) {
            if (FirebaseInstanceId.zzcix()) {
                Log.d("FirebaseInstanceId", "Connectivity changed. Starting background sync.");
            }
            FirebaseInstanceId.zzb(this.zznzy, 0);
            this.zznzy.getContext().unregisterReceiver(this);
            this.zznzy = null;
        }
    }

    public final void zzcjq() {
        if (FirebaseInstanceId.zzcix()) {
            Log.d("FirebaseInstanceId", "Connectivity change received registered");
        }
        this.zznzy.getContext().registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }
}
