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
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import android.util.Log;
import java.io.IOException;

public class FirebaseInstanceIdService extends zzb {
    private static BroadcastReceiver zzcln;
    @VisibleForTesting
    static final Object zzclo = new Object();
    @VisibleForTesting
    static boolean zzclp = false;
    private boolean zzclq = false;

    private String zzG(Intent intent) {
        String stringExtra = intent.getStringExtra("subtype");
        return stringExtra == null ? "" : stringExtra;
    }

    private int zza(Intent intent, boolean z) {
        int intExtra = intent == null ? 10 : intent.getIntExtra("next_retry_delay_in_seconds", 0);
        return (intExtra >= 10 || z) ? intExtra >= 10 ? intExtra > 28800 ? 28800 : intExtra : 10 : 30;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void zza(Context context, FirebaseInstanceId firebaseInstanceId) {
        synchronized (zzclo) {
            if (zzclp) {
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zza(Intent intent, boolean z, boolean z2) {
        synchronized (zzclo) {
            zzclp = false;
        }
        if (zzf.zzbA(this) != null) {
            FirebaseInstanceId instance = FirebaseInstanceId.getInstance();
            zza zzabN = instance.zzabN();
            if (zzabN == null || zzabN.zzjB(zzd.zzbhN)) {
                try {
                    String zzabO = instance.zzabO();
                    if (zzabO != null) {
                        if (this.zzclq) {
                            Log.d("FirebaseInstanceId", "get master token succeeded");
                        }
                        zza((Context) this, instance);
                        if (z2 || zzabN == null || !(zzabN == null || zzabO.equals(zzabN.zzbxT))) {
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
            zze zzabP = instance.zzabP();
            for (String zzabS = zzabP.zzabS(); zzabS != null; zzabS = zzabP.zzabS()) {
                String[] split = zzabS.split("!");
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
                                        if (!this.zzclq) {
                                            Log.d("FirebaseInstanceId", "subscribe operation succeeded");
                                            break;
                                        }
                                        break;
                                    case 1:
                                        FirebaseInstanceId.getInstance().zzjv(str2);
                                        if (!this.zzclq) {
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
                                if (!this.zzclq) {
                                    break;
                                }
                                Log.d("FirebaseInstanceId", "subscribe operation succeeded");
                                break;
                            case 1:
                                FirebaseInstanceId.getInstance().zzjv(str2);
                                if (!this.zzclq) {
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
                zzabP.zzjx(zzabS);
            }
            Log.d("FirebaseInstanceId", "topic sync succeeded");
        }
    }

    private void zza(zzf com_google_firebase_iid_zzf, Bundle bundle) {
        String zzbA = zzf.zzbA(this);
        if (zzbA == null) {
            Log.w("FirebaseInstanceId", "Unable to respond to ping due to missing target package");
            return;
        }
        Intent intent = new Intent("com.google.android.gcm.intent.SEND");
        intent.setPackage(zzbA);
        intent.putExtras(bundle);
        com_google_firebase_iid_zzf.zzp(intent);
        intent.putExtra("google.to", "google.com/iid");
        intent.putExtra("google.message_id", zzf.zzHn());
        sendOrderedBroadcast(intent, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
    }

    static void zzcs(Context context) {
        if (zzf.zzbA(context) != null) {
            synchronized (zzclo) {
                if (!zzclp) {
                    zzg.zzabU().zzf(context, zzqF(0));
                    zzclp = true;
                }
            }
        }
    }

    private static boolean zzct(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void zzd(Intent intent, String str) {
        boolean zzct = zzct(this);
        final int zza = zza(intent, zzct);
        Log.d("FirebaseInstanceId", new StringBuilder(String.valueOf(str).length() + 47).append("background sync failed: ").append(str).append(", retry in ").append(zza).append("s").toString());
        synchronized (zzclo) {
            zzqG(zza);
            zzclp = true;
        }
        if (!zzct) {
            if (this.zzclq) {
                Log.d("FirebaseInstanceId", "device not connected. Connectivity change received registered");
            }
            if (zzcln == null) {
                zzcln = new BroadcastReceiver(this) {
                    final /* synthetic */ FirebaseInstanceIdService zzcls;

                    public void onReceive(Context context, Intent intent) {
                        if (FirebaseInstanceIdService.zzct(context)) {
                            if (this.zzcls.zzclq) {
                                Log.d("FirebaseInstanceId", "connectivity changed. starting background sync.");
                            }
                            this.zzcls.getApplicationContext().unregisterReceiver(this);
                            zzg.zzabU().zzf(context, FirebaseInstanceIdService.zzqF(zza));
                        }
                    }
                };
            }
            getApplicationContext().registerReceiver(zzcln, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
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
        return zzg.zzabU().zzabV();
    }

    public boolean zzE(Intent intent) {
        this.zzclq = Log.isLoggable("FirebaseInstanceId", 3);
        if (intent.getStringExtra("error") == null && intent.getStringExtra("registration_id") == null) {
            return false;
        }
        String zzG = zzG(intent);
        if (this.zzclq) {
            String str = "FirebaseInstanceId";
            String str2 = "Register result in service ";
            String valueOf = String.valueOf(zzG);
            Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
        zzjw(zzG).zzabR().zzs(intent);
        return true;
    }

    public void zzF(Intent intent) {
        String zzG = zzG(intent);
        zzd zzjw = zzjw(zzG);
        String stringExtra = intent.getStringExtra("CMD");
        if (this.zzclq) {
            String valueOf = String.valueOf(intent.getExtras());
            Log.d("FirebaseInstanceId", new StringBuilder(((String.valueOf(zzG).length() + 18) + String.valueOf(stringExtra).length()) + String.valueOf(valueOf).length()).append("Service command ").append(zzG).append(" ").append(stringExtra).append(" ").append(valueOf).toString());
        }
        if (intent.getStringExtra("unregistered") != null) {
            zzh zzabQ = zzjw.zzabQ();
            if (zzG == null) {
                zzG = "";
            }
            zzabQ.zzeK(zzG);
            zzjw.zzabR().zzs(intent);
        } else if ("gcm.googleapis.com/refresh".equals(intent.getStringExtra("from"))) {
            zzjw.zzabQ().zzeK(zzG);
            zza(intent, false, true);
        } else if ("RST".equals(stringExtra)) {
            zzjw.zzHi();
            zza(intent, true, true);
        } else if ("RST_FULL".equals(stringExtra)) {
            if (!zzjw.zzabQ().isEmpty()) {
                zzjw.zzHi();
                zzjw.zzabQ().zzHo();
                zza(intent, true, true);
            }
        } else if ("SYNC".equals(stringExtra)) {
            zzjw.zzabQ().zzeK(zzG);
            zza(intent, false, true);
        } else if ("PING".equals(stringExtra)) {
            zza(zzjw.zzabR(), intent.getExtras());
        }
    }
}
