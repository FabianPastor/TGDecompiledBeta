package com.google.android.gms.iid;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class InstanceIDListenerService extends Service {
    static String ACTION = "action";
    private static String zzbgu = "gcm.googleapis.com/refresh";
    private static String zzbhP = "google.com/iid";
    private static String zzbhQ = "CMD";
    MessengerCompat zzbhN = new MessengerCompat(new Handler(this, Looper.getMainLooper()) {
        final /* synthetic */ InstanceIDListenerService zzbhT;

        public void handleMessage(Message message) {
            this.zzbhT.zza(message, MessengerCompat.zzc(message));
        }
    });
    BroadcastReceiver zzbhO = new BroadcastReceiver(this) {
        final /* synthetic */ InstanceIDListenerService zzbhT;

        {
            this.zzbhT = r1;
        }

        public void onReceive(Context context, Intent intent) {
            if (Log.isLoggable("InstanceID", 3)) {
                intent.getStringExtra("registration_id");
                String valueOf = String.valueOf(intent.getExtras());
                Log.d("InstanceID", new StringBuilder(String.valueOf(valueOf).length() + 46).append("Received GSF callback using dynamic receiver: ").append(valueOf).toString());
            }
            this.zzbhT.zzn(intent);
            this.zzbhT.stop();
        }
    };
    int zzbhR;
    int zzbhS;

    static void zza(Context context, zzd com_google_android_gms_iid_zzd) {
        com_google_android_gms_iid_zzd.zzHn();
        Intent intent = new Intent("com.google.android.gms.iid.InstanceID");
        intent.putExtra(zzbhQ, "RST");
        intent.setPackage(context.getPackageName());
        context.startService(intent);
    }

    private void zza(Message message, int i) {
        zzc.zzbA(this);
        getPackageManager();
        if (i == zzc.zzbhY || i == zzc.zzbhX) {
            zzn((Intent) message.obj);
            return;
        }
        int i2 = zzc.zzbhX;
        Log.w("InstanceID", "Message from unexpected caller " + i + " mine=" + i2 + " appid=" + zzc.zzbhY);
    }

    static void zzbz(Context context) {
        Intent intent = new Intent("com.google.android.gms.iid.InstanceID");
        intent.setPackage(context.getPackageName());
        intent.putExtra(zzbhQ, "SYNC");
        context.startService(intent);
    }

    public IBinder onBind(Intent intent) {
        return (intent == null || !"com.google.android.gms.iid.InstanceID".equals(intent.getAction())) ? null : this.zzbhN.getBinder();
    }

    public void onCreate() {
        IntentFilter intentFilter = new IntentFilter("com.google.android.c2dm.intent.REGISTRATION");
        intentFilter.addCategory(getPackageName());
        registerReceiver(this.zzbhO, intentFilter, "com.google.android.c2dm.permission.RECEIVE", null);
    }

    public void onDestroy() {
        unregisterReceiver(this.zzbhO);
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        zzjJ(i2);
        if (intent == null) {
            stop();
            return 2;
        }
        try {
            if ("com.google.android.gms.iid.InstanceID".equals(intent.getAction())) {
                if (VERSION.SDK_INT <= 18) {
                    Intent intent2 = (Intent) intent.getParcelableExtra("GSF");
                    if (intent2 != null) {
                        startService(intent2);
                        return 1;
                    }
                }
                zzn(intent);
            }
            stop();
            if (intent.getStringExtra("from") != null) {
                WakefulBroadcastReceiver.completeWakefulIntent(intent);
            }
            return 2;
        } finally {
            stop();
        }
    }

    public void onTokenRefresh() {
    }

    void stop() {
        synchronized (this) {
            this.zzbhR--;
            if (this.zzbhR == 0) {
                stopSelf(this.zzbhS);
            }
            if (Log.isLoggable("InstanceID", 3)) {
                int i = this.zzbhR;
                Log.d("InstanceID", "Stop " + i + " " + this.zzbhS);
            }
        }
    }

    public void zzaG(boolean z) {
        onTokenRefresh();
    }

    void zzjJ(int i) {
        synchronized (this) {
            this.zzbhR++;
            if (i > this.zzbhS) {
                this.zzbhS = i;
            }
        }
    }

    public void zzn(Intent intent) {
        InstanceID instance;
        String stringExtra = intent.getStringExtra("subtype");
        if (stringExtra == null) {
            instance = InstanceID.getInstance(this);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("subtype", stringExtra);
            instance = InstanceID.zza(this, bundle);
        }
        String stringExtra2 = intent.getStringExtra(zzbhQ);
        if (intent.getStringExtra("error") == null && intent.getStringExtra("registration_id") == null) {
            if (Log.isLoggable("InstanceID", 3)) {
                String valueOf = String.valueOf(intent.getExtras());
                Log.d("InstanceID", new StringBuilder(((String.valueOf(stringExtra).length() + 18) + String.valueOf(stringExtra2).length()) + String.valueOf(valueOf).length()).append("Service command ").append(stringExtra).append(" ").append(stringExtra2).append(" ").append(valueOf).toString());
            }
            if (intent.getStringExtra("unregistered") != null) {
                zzd zzHi = instance.zzHi();
                if (stringExtra == null) {
                    stringExtra = "";
                }
                zzHi.zzeK(stringExtra);
                instance.zzHj().zzv(intent);
                return;
            } else if (zzbgu.equals(intent.getStringExtra("from"))) {
                instance.zzHi().zzeK(stringExtra);
                zzaG(false);
                return;
            } else if ("RST".equals(stringExtra2)) {
                instance.zzHh();
                zzaG(true);
                return;
            } else if ("RST_FULL".equals(stringExtra2)) {
                if (!instance.zzHi().isEmpty()) {
                    instance.zzHi().zzHn();
                    zzaG(true);
                    return;
                }
                return;
            } else if ("SYNC".equals(stringExtra2)) {
                instance.zzHi().zzeK(stringExtra);
                zzaG(false);
                return;
            } else {
                "PING".equals(stringExtra2);
                return;
            }
        }
        if (Log.isLoggable("InstanceID", 3)) {
            stringExtra2 = "InstanceID";
            String str = "Register result in service ";
            stringExtra = String.valueOf(stringExtra);
            Log.d(stringExtra2, stringExtra.length() != 0 ? str.concat(stringExtra) : new String(str));
        }
        instance.zzHj().zzv(intent);
    }
}
