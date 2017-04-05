package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.util.zzt;
import com.google.firebase.iid.zzb.zzb;

public final class FirebaseInstanceIdReceiver extends WakefulBroadcastReceiver {
    private static boolean zzbgs = false;
    private zzb zzcll;
    private zzb zzclm;

    private zzb zzK(Context context, String str) {
        if ("com.google.android.c2dm.intent.RECEIVE".equals(str)) {
            if (this.zzclm == null) {
                this.zzclm = new zzb(context, str);
            }
            return this.zzclm;
        }
        if (this.zzcll == null) {
            this.zzcll = new zzb(context, str);
        }
        return this.zzcll;
    }

    public void onReceive(Context context, Intent intent) {
        intent.setComponent(null);
        intent.setPackage(context.getPackageName());
        if (VERSION.SDK_INT <= 18) {
            intent.removeCategory(context.getPackageName());
        }
        String stringExtra = intent.getStringExtra("gcm.rawData64");
        if (stringExtra != null) {
            intent.putExtra("rawData", Base64.decode(stringExtra, 0));
            intent.removeExtra("gcm.rawData64");
        }
        stringExtra = intent.getStringExtra("from");
        if ("google.com/iid".equals(stringExtra) || "gcm.googleapis.com/refresh".equals(stringExtra)) {
            stringExtra = "com.google.firebase.INSTANCE_ID_EVENT";
        } else if ("com.google.android.c2dm.intent.RECEIVE".equals(intent.getAction())) {
            stringExtra = "com.google.firebase.MESSAGING_EVENT";
        } else {
            Log.d("FirebaseInstanceId", "Unexpected intent");
            stringExtra = null;
        }
        int i = -1;
        if (stringExtra != null) {
            i = zza(context, stringExtra, intent);
        }
        if (isOrderedBroadcast()) {
            setResultCode(i);
        }
    }

    public int zza(Context context, String str, Intent intent) {
        if (!zzt.zzzq()) {
            return zzg.zzabU().zzb(context, str, intent);
        }
        if (isOrderedBroadcast()) {
            setResultCode(-1);
        }
        zzK(context, str).zzb(intent, goAsync());
        return -1;
    }
}
