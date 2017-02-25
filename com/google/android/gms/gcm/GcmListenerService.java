package com.google.android.gms.gcm;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import java.util.Iterator;

public abstract class GcmListenerService extends Service {
    private int zzbgm;
    private int zzbgn = 0;
    private final Object zzrJ = new Object();

    static void zzD(Bundle bundle) {
        Iterator it = bundle.keySet().iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if (str != null && str.startsWith("google.c.")) {
                it.remove();
            }
        }
    }

    private void zzGN() {
        synchronized (this.zzrJ) {
            this.zzbgn--;
            if (this.zzbgn == 0) {
                zzjA(this.zzbgm);
            }
        }
    }

    @TargetApi(11)
    private void zzl(final Intent intent) {
        int i = VERSION.SDK_INT;
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable(this) {
            final /* synthetic */ GcmListenerService zzbgo;

            public void run() {
                this.zzbgo.zzm(intent);
            }
        });
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzm(Intent intent) {
        try {
            String action = intent.getAction();
            Object obj = -1;
            switch (action.hashCode()) {
                case 366519424:
                    if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
                        obj = null;
                    }
                default:
                    switch (obj) {
                        case null:
                            zzn(intent);
                            break;
                        default:
                            action = "GcmListenerService";
                            String str = "Unknown intent action: ";
                            String valueOf = String.valueOf(intent.getAction());
                            Log.d(action, valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
                            break;
                    }
                    zzGN();
                    break;
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void zzn(Intent intent) {
        String stringExtra = intent.getStringExtra("message_type");
        if (stringExtra == null) {
            stringExtra = GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE;
        }
        Object obj = -1;
        switch (stringExtra.hashCode()) {
            case -2062414158:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_DELETED)) {
                    obj = 1;
                    break;
                }
                break;
            case 102161:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
                    obj = null;
                    break;
                }
                break;
            case 814694033:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR)) {
                    obj = 3;
                    break;
                }
                break;
            case 814800675:
                if (stringExtra.equals(GoogleCloudMessaging.MESSAGE_TYPE_SEND_EVENT)) {
                    obj = 2;
                    break;
                }
                break;
        }
        switch (obj) {
            case null:
                zzo(intent);
                return;
            case 1:
                onDeletedMessages();
                return;
            case 2:
                onMessageSent(intent.getStringExtra("google.message_id"));
                return;
            case 3:
                onSendError(zzp(intent), intent.getStringExtra("error"));
                return;
            default:
                String str = "GcmListenerService";
                String str2 = "Received message with unknown type: ";
                stringExtra = String.valueOf(stringExtra);
                Log.w(str, stringExtra.length() != 0 ? str2.concat(stringExtra) : new String(str2));
                return;
        }
    }

    private void zzo(Intent intent) {
        Bundle extras = intent.getExtras();
        extras.remove("message_type");
        extras.remove("android.support.content.wakelockid");
        if (zza.zzE(extras)) {
            if (zza.zzbu(this)) {
                zza.zzF(extras);
            } else {
                zza.zzbt(this).zzG(extras);
                return;
            }
        }
        String string = extras.getString("from");
        extras.remove("from");
        zzD(extras);
        onMessageReceived(string, extras);
    }

    private String zzp(Intent intent) {
        String stringExtra = intent.getStringExtra("google.message_id");
        return stringExtra == null ? intent.getStringExtra("message_id") : stringExtra;
    }

    public final IBinder onBind(Intent intent) {
        return null;
    }

    public void onDeletedMessages() {
    }

    public void onMessageReceived(String str, Bundle bundle) {
    }

    public void onMessageSent(String str) {
    }

    public void onSendError(String str, String str2) {
    }

    public final int onStartCommand(Intent intent, int i, int i2) {
        synchronized (this.zzrJ) {
            this.zzbgm = i2;
            this.zzbgn++;
        }
        if (intent == null) {
            zzGN();
            return 2;
        }
        zzl(intent);
        return 3;
    }

    boolean zzjA(int i) {
        return stopSelfResult(i);
    }
}
