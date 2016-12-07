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
    private static String zzbfQ = "gcm.googleapis.com/refresh";
    private static String zzbhj = "google.com/iid";
    private static String zzbhk = "CMD";
    MessengerCompat zzbhh = new MessengerCompat(new Handler(this, Looper.getMainLooper()) {
        final /* synthetic */ InstanceIDListenerService zzbhn;

        public void handleMessage(Message message) {
            this.zzbhn.zza(message, MessengerCompat.zzc(message));
        }
    });
    BroadcastReceiver zzbhi = new BroadcastReceiver(this) {
        final /* synthetic */ InstanceIDListenerService zzbhn;

        {
            this.zzbhn = r1;
        }

        public void onReceive(Context context, Intent intent) {
            if (Log.isLoggable("InstanceID", 3)) {
                intent.getStringExtra("registration_id");
                String valueOf = String.valueOf(intent.getExtras());
                Log.d("InstanceID", new StringBuilder(String.valueOf(valueOf).length() + 46).append("Received GSF callback using dynamic receiver: ").append(valueOf).toString());
            }
            this.zzbhn.zzn(intent);
            this.zzbhn.stop();
        }
    };
    int zzbhl;
    int zzbhm;

    static void zza(Context context, zzd com_google_android_gms_iid_zzd) {
        com_google_android_gms_iid_zzd.zzGA();
        Intent intent = new Intent("com.google.android.gms.iid.InstanceID");
        intent.putExtra(zzbhk, "RST");
        intent.setPackage(context.getPackageName());
        context.startService(intent);
    }

    private void zza(Message message, int i) {
        zzc.zzbi(this);
        getPackageManager();
        if (i == zzc.zzbhs || i == zzc.zzbhr) {
            zzn((Intent) message.obj);
            return;
        }
        int i2 = zzc.zzbhr;
        Log.w("InstanceID", "Message from unexpected caller " + i + " mine=" + i2 + " appid=" + zzc.zzbhs);
    }

    static void zzbh(Context context) {
        Intent intent = new Intent("com.google.android.gms.iid.InstanceID");
        intent.setPackage(context.getPackageName());
        intent.putExtra(zzbhk, "SYNC");
        context.startService(intent);
    }

    public IBinder onBind(Intent intent) {
        return (intent == null || !"com.google.android.gms.iid.InstanceID".equals(intent.getAction())) ? null : this.zzbhh.getBinder();
    }

    public void onCreate() {
        IntentFilter intentFilter = new IntentFilter("com.google.android.c2dm.intent.REGISTRATION");
        intentFilter.addCategory(getPackageName());
        registerReceiver(this.zzbhi, intentFilter, "com.google.android.c2dm.permission.RECEIVE", null);
    }

    public void onDestroy() {
        unregisterReceiver(this.zzbhi);
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        zzjA(i2);
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
            this.zzbhl--;
            if (this.zzbhl == 0) {
                stopSelf(this.zzbhm);
            }
            if (Log.isLoggable("InstanceID", 3)) {
                int i = this.zzbhl;
                Log.d("InstanceID", "Stop " + i + " " + this.zzbhm);
            }
        }
    }

    public void zzaB(boolean z) {
        onTokenRefresh();
    }

    void zzjA(int i) {
        synchronized (this) {
            this.zzbhl++;
            if (i > this.zzbhm) {
                this.zzbhm = i;
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
        String stringExtra2 = intent.getStringExtra(zzbhk);
        if (intent.getStringExtra("error") == null && intent.getStringExtra("registration_id") == null) {
            if (Log.isLoggable("InstanceID", 3)) {
                String valueOf = String.valueOf(intent.getExtras());
                Log.d("InstanceID", new StringBuilder(((String.valueOf(stringExtra).length() + 18) + String.valueOf(stringExtra2).length()) + String.valueOf(valueOf).length()).append("Service command ").append(stringExtra).append(" ").append(stringExtra2).append(" ").append(valueOf).toString());
            }
            if (intent.getStringExtra("unregistered") != null) {
                zzd zzGv = instance.zzGv();
                if (stringExtra == null) {
                    stringExtra = "";
                }
                zzGv.zzeO(stringExtra);
                instance.zzGw().zzv(intent);
                return;
            } else if (zzbfQ.equals(intent.getStringExtra("from"))) {
                instance.zzGv().zzeO(stringExtra);
                zzaB(false);
                return;
            } else if ("RST".equals(stringExtra2)) {
                instance.zzGu();
                zzaB(true);
                return;
            } else if ("RST_FULL".equals(stringExtra2)) {
                if (!instance.zzGv().isEmpty()) {
                    instance.zzGv().zzGA();
                    zzaB(true);
                    return;
                }
                return;
            } else if ("SYNC".equals(stringExtra2)) {
                instance.zzGv().zzeO(stringExtra);
                zzaB(false);
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
        instance.zzGw().zzv(intent);
    }
}
