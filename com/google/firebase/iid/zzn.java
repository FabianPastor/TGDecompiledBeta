package com.google.firebase.iid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

final class zzn extends BroadcastReceiver {
    private /* synthetic */ zzl zzckJ;

    zzn(zzl com_google_firebase_iid_zzl) {
        this.zzckJ = com_google_firebase_iid_zzl;
    }

    public final void onReceive(Context context, Intent intent) {
        if (Log.isLoggable("InstanceID/Rpc", 3)) {
            String valueOf = String.valueOf(intent.getExtras());
            Log.d("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 44).append("Received GSF callback via dynamic receiver: ").append(valueOf).toString());
        }
        this.zzckJ.zzi(intent);
    }
}
