package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.google.android.gms.common.util.zzt;
import com.google.firebase.iid.zzb.zzc;

public final class FirebaseInstanceIdInternalReceiver extends WakefulBroadcastReceiver {
    private static boolean zzbgs = false;
    private static zzc zzclp;
    private static zzc zzclq;

    static synchronized zzc zzL(Context context, String str) {
        zzc com_google_firebase_iid_zzb_zzc;
        synchronized (FirebaseInstanceIdInternalReceiver.class) {
            if ("com.google.firebase.MESSAGING_EVENT".equals(str)) {
                if (zzclq == null) {
                    zzclq = new zzc(context, str);
                }
                com_google_firebase_iid_zzb_zzc = zzclq;
            } else {
                if (zzclp == null) {
                    zzclp = new zzc(context, str);
                }
                com_google_firebase_iid_zzb_zzc = zzclp;
            }
        }
        return com_google_firebase_iid_zzb_zzc;
    }

    static boolean zzcs(Context context) {
        return zzt.zzzq() && context.getApplicationInfo().targetSdkVersion > 25;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Parcelable parcelableExtra = intent.getParcelableExtra("wrapped_intent");
            if (parcelableExtra instanceof Intent) {
                Intent intent2 = (Intent) parcelableExtra;
                if (zzcs(context)) {
                    zzL(context, intent.getAction()).zza(intent2, goAsync());
                    return;
                } else {
                    zzg.zzabW().zzb(context, intent.getAction(), intent2);
                    return;
                }
            }
            Log.e("FirebaseInstanceId", "Missing or invalid wrapped intent");
        }
    }
}
