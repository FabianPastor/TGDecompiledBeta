package com.google.firebase.iid;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import android.util.Log;
import java.io.IOException;

public class FirebaseInstanceIdService extends zzb {
    @VisibleForTesting
    static final Object zzclr = new Object();
    @VisibleForTesting
    static boolean zzcls = false;
    private boolean zzclt = false;

    private static class zza extends BroadcastReceiver {
        @Nullable
        static BroadcastReceiver receiver;
        final int zzclu;

        zza(int i) {
            this.zzclu = i;
        }

        static synchronized void zzl(Context context, int i) {
            synchronized (zza.class) {
                if (receiver == null) {
                    receiver = new zza(i);
                    context.getApplicationContext().registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                }
            }
        }

        public void onReceive(Context context, Intent intent) {
            synchronized (zza.class) {
                if (receiver != this) {
                } else if (FirebaseInstanceIdService.zzcu(context)) {
                    if (Log.isLoggable("FirebaseInstanceId", 3)) {
                        Log.d("FirebaseInstanceId", "connectivity changed. starting background sync.");
                    }
                    context.getApplicationContext().unregisterReceiver(this);
                    receiver = null;
                    zzg.zzabW().zzg(context, FirebaseInstanceIdService.zzqF(this.zzclu));
                }
            }
        }
    }

    private String zzG(Intent intent) {
        String stringExtra = intent.getStringExtra("subtype");
        return stringExtra == null ? "" : stringExtra;
    }

    private void zzU(Bundle bundle) {
        String zzbA = zzf.zzbA(this);
        if (zzbA == null) {
            Log.w("FirebaseInstanceId", "Unable to respond to ping due to missing target package");
            return;
        }
        Intent intent = new Intent("com.google.android.gcm.intent.SEND");
        intent.setPackage(zzbA);
        intent.putExtras(bundle);
        zzf.zzf(this, intent);
        intent.putExtra("google.to", "google.com/iid");
        intent.putExtra("google.message_id", zzf.zzHn());
        sendOrderedBroadcast(intent, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
    }

    private int zza(Intent intent, boolean z) {
        int intExtra = intent == null ? 10 : intent.getIntExtra("next_retry_delay_in_seconds", 0);
        return (intExtra >= 10 || z) ? intExtra >= 10 ? intExtra > 28800 ? 28800 : intExtra : 10 : 30;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void zza(Context context, FirebaseInstanceId firebaseInstanceId) {
        synchronized (zzclr) {
            if (zzcls) {
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zza(Intent intent, boolean z, boolean z2) {
        synchronized (zzclr) {
            zzcls = false;
        }
        if (zzf.zzbA(this) != null) {
            FirebaseInstanceId instance = FirebaseInstanceId.getInstance();
            zza zzabP = instance.zzabP();
            if (zzabP == null || zzabP.zzjB(zzd.zzbhN)) {
                try {
                    String zzabQ = instance.zzabQ();
                    if (zzabQ != null) {
                        if (this.zzclt) {
                            Log.d("FirebaseInstanceId", "get master token succeeded");
                        }
                        zza((Context) this, instance);
                        if (z2 || zzabP == null || !(zzabP == null || zzabQ.equals(zzabP.zzbxW))) {
                            onTokenRefresh();
                            return;
                        }
                        return;
                    }
                    zzd(intent, "returned token is null");
                    return;
                } catch (IOException e) {
                    zzd(intent, e.getMessage());
                    return;
                } catch (Throwable e2) {
                    Log.e("FirebaseInstanceId", "Unable to get master token", e2);
                    return;
                }
            }
            zze zzabR = instance.zzabR();
            for (String zzabU = zzabR.zzabU(); zzabU != null; zzabU = zzabR.zzabU()) {
                String[] split = zzabU.split("!");
                if (split.length == 2) {
                    String str = split[0];
                    String str2 = split[1];
                    int i = -1;
                    try {
                        switch (str.hashCode()) {
                            case 83:
                                if (str.equals("S")) {
                                    i = 0;
                                }
                            case 85:
                                if (str.equals("U")) {
                                    i = 1;
                                }
                                switch (i) {
                                    case 0:
                                        FirebaseInstanceId.getInstance().zzju(str2);
                                        if (!this.zzclt) {
                                            Log.d("FirebaseInstanceId", "subscribe operation succeeded");
                                            break;
                                        }
                                        break;
                                    case 1:
                                        FirebaseInstanceId.getInstance().zzjv(str2);
                                        if (!this.zzclt) {
                                            Log.d("FirebaseInstanceId", "unsubscribe operation succeeded");
                                            break;
                                        }
                                        break;
                                    default:
                                        continue;
                                }
                        }
                        switch (i) {
                            case 0:
                                FirebaseInstanceId.getInstance().zzju(str2);
                                if (!this.zzclt) {
                                    break;
                                }
                                Log.d("FirebaseInstanceId", "subscribe operation succeeded");
                                break;
                            case 1:
                                FirebaseInstanceId.getInstance().zzjv(str2);
                                if (!this.zzclt) {
                                    break;
                                }
                                Log.d("FirebaseInstanceId", "unsubscribe operation succeeded");
                                break;
                            default:
                                continue;
                        }
                    } catch (IOException e3) {
                        zzd(intent, e3.getMessage());
                        return;
                    }
                }
                zzabR.zzjx(zzabU);
            }
            Log.d("FirebaseInstanceId", "topic sync succeeded");
        }
    }

    static void zzct(Context context) {
        if (zzf.zzbA(context) != null) {
            synchronized (zzclr) {
                if (!zzcls) {
                    zzg.zzabW().zzg(context, zzqF(0));
                    zzcls = true;
                }
            }
        }
    }

    private static boolean zzcu(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void zzd(Intent intent, String str) {
        boolean zzcu = zzcu(this);
        int zza = zza(intent, zzcu);
        Log.d("FirebaseInstanceId", new StringBuilder(String.valueOf(str).length() + 47).append("background sync failed: ").append(str).append(", retry in ").append(zza).append("s").toString());
        synchronized (zzclr) {
            zzqG(zza);
            zzcls = true;
        }
        if (!zzcu) {
            if (this.zzclt) {
                Log.d("FirebaseInstanceId", "device not connected. Connectivity change received registered");
            }
            zza.zzl(this, zza);
        }
    }

    private zzd zzjw(String str) {
        if (str == null) {
            return zzd.zzb(this, null);
        }
        Bundle bundle = new Bundle();
        bundle.putString("subtype", str);
        return zzd.zzb(this, bundle);
    }

    private static Intent zzqF(int i) {
        Intent intent = new Intent("ACTION_TOKEN_REFRESH_RETRY");
        intent.putExtra("next_retry_delay_in_seconds", i);
        return intent;
    }

    private void zzqG(int i) {
        ((AlarmManager) getSystemService("alarm")).set(3, SystemClock.elapsedRealtime() + ((long) (i * 1000)), zzg.zza(this, 0, zzqF(i * 2), 134217728));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleIntent(Intent intent) {
        boolean z;
        String action = intent.getAction();
        if (action == null) {
            action = "";
        }
        switch (action.hashCode()) {
            case -1737547627:
                if (action.equals("ACTION_TOKEN_REFRESH_RETRY")) {
                    z = false;
                    break;
                }
            default:
                z = true;
                break;
        }
        switch (z) {
            case false:
                zza(intent, false, false);
                return;
            default:
                zzF(intent);
                return;
        }
    }

    @WorkerThread
    public void onTokenRefresh() {
    }

    protected Intent zzD(Intent intent) {
        return zzg.zzabW().zzabX();
    }

    public boolean zzE(Intent intent) {
        this.zzclt = Log.isLoggable("FirebaseInstanceId", 3);
        if (intent.getStringExtra("error") == null && intent.getStringExtra("registration_id") == null) {
            return false;
        }
        String zzG = zzG(intent);
        if (this.zzclt) {
            String str = "FirebaseInstanceId";
            String str2 = "Register result in service ";
            String valueOf = String.valueOf(zzG);
            Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
        zzjw(zzG).zzabT().zzs(intent);
        return true;
    }

    public void zzF(Intent intent) {
        String zzG = zzG(intent);
        zzd zzjw = zzjw(zzG);
        String stringExtra = intent.getStringExtra("CMD");
        if (this.zzclt) {
            String valueOf = String.valueOf(intent.getExtras());
            Log.d("FirebaseInstanceId", new StringBuilder(((String.valueOf(zzG).length() + 18) + String.valueOf(stringExtra).length()) + String.valueOf(valueOf).length()).append("Service command ").append(zzG).append(" ").append(stringExtra).append(" ").append(valueOf).toString());
        }
        if (intent.getStringExtra("unregistered") != null) {
            zzh zzabS = zzjw.zzabS();
            if (zzG == null) {
                zzG = "";
            }
            zzabS.zzeK(zzG);
            zzjw.zzabT().zzs(intent);
        } else if ("gcm.googleapis.com/refresh".equals(intent.getStringExtra("from"))) {
            zzjw.zzabS().zzeK(zzG);
            zza(intent, false, true);
        } else if ("RST".equals(stringExtra)) {
            zzjw.zzHi();
            zza(intent, true, true);
        } else if ("RST_FULL".equals(stringExtra)) {
            if (!zzjw.zzabS().isEmpty()) {
                zzjw.zzHi();
                zzjw.zzabS().zzHo();
                zza(intent, true, true);
            }
        } else if ("SYNC".equals(stringExtra)) {
            zzjw.zzabS().zzeK(zzG);
            zza(intent, false, true);
        } else if ("PING".equals(stringExtra)) {
            zzU(intent.getExtras());
        }
    }
}
