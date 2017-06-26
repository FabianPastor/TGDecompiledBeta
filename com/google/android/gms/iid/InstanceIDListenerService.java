package com.google.android.gms.iid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.iid.zzb;

public class InstanceIDListenerService extends zzb {
    static void zza(Context context, zzh com_google_android_gms_iid_zzh) {
        com_google_android_gms_iid_zzh.zzvP();
        Intent intent = new Intent("com.google.android.gms.iid.InstanceID");
        intent.putExtra("CMD", "RST");
        intent.setClassName(context, "com.google.android.gms.gcm.GcmReceiver");
        context.sendBroadcast(intent);
    }

    public void handleIntent(Intent intent) {
        if ("com.google.android.gms.iid.InstanceID".equals(intent.getAction())) {
            Bundle bundle = null;
            String stringExtra = intent.getStringExtra("subtype");
            if (stringExtra != null) {
                bundle = new Bundle();
                bundle.putString("subtype", stringExtra);
            }
            InstanceID zza = InstanceID.zza(this, bundle);
            String stringExtra2 = intent.getStringExtra("CMD");
            if (Log.isLoggable("InstanceID", 3)) {
                Log.d("InstanceID", new StringBuilder((String.valueOf(stringExtra).length() + 34) + String.valueOf(stringExtra2).length()).append("Service command. subtype:").append(stringExtra).append(" command:").append(stringExtra2).toString());
            }
            if ("gcm.googleapis.com/refresh".equals(intent.getStringExtra("from"))) {
                InstanceID.zzvM().zzdr(stringExtra);
                onTokenRefresh();
            } else if ("RST".equals(stringExtra2)) {
                zza.zzvL();
                onTokenRefresh();
            } else if ("RST_FULL".equals(stringExtra2)) {
                if (!InstanceID.zzvM().isEmpty()) {
                    InstanceID.zzvM().zzvP();
                    onTokenRefresh();
                }
            } else if ("SYNC".equals(stringExtra2)) {
                InstanceID.zzvM().zzdr(stringExtra);
                onTokenRefresh();
            } else {
                "PING".equals(stringExtra2);
            }
        }
    }

    public void onTokenRefresh() {
    }
}
