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
    private static BroadcastReceiver zzclf;
    @VisibleForTesting
    static final Object zzclg = new Object();
    @VisibleForTesting
    static boolean zzclh = false;
    private boolean zzcli = false;

    private String zzJ(Intent intent) {
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
        synchronized (zzclg) {
            if (zzclh) {
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zza(Intent intent, boolean z, boolean z2) {
        synchronized (zzclg) {
            zzclh = false;
        }
        if (zzf.zzbA(this) != null) {
            FirebaseInstanceId instance = FirebaseInstanceId.getInstance();
            zza zzabM = instance.zzabM();
            if (zzabM == null || zzabM.zzjB(zzd.zzbhM)) {
                try {
                    String zzabN = instance.zzabN();
                    if (zzabN != null) {
                        if (this.zzcli) {
                            Log.d("FirebaseInstanceId", "get master token succeeded");
                        }
                        zza((Context) this, instance);
                        if (z2 || zzabM == null || !(zzabM == null || zzabN.equals(zzabM.zzbxX))) {
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
            zze zzabO = instance.zzabO();
            for (String zzabR = zzabO.zzabR(); zzabR != null; zzabR = zzabO.zzabR()) {
                String[] split = zzabR.split("!");
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
                                        if (!this.zzcli) {
                                            Log.d("FirebaseInstanceId", "subscribe operation succeeded");
                                            break;
                                        }
                                        break;
                                    case 1:
                                        FirebaseInstanceId.getInstance().zzjv(str2);
                                        if (!this.zzcli) {
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
                                if (!this.zzcli) {
                                    break;
                                }
                                Log.d("FirebaseInstanceId", "subscribe operation succeeded");
                                break;
                            case 1:
                                FirebaseInstanceId.getInstance().zzjv(str2);
                                if (!this.zzcli) {
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
                zzabO.zzjx(zzabR);
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
        com_google_firebase_iid_zzf.zzs(intent);
        intent.putExtra("google.to", "google.com/iid");
        intent.putExtra("google.message_id", zzf.zzHm());
        sendOrderedBroadcast(intent, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
    }

    static void zzct(Context context) {
        if (zzf.zzbA(context) != null) {
            synchronized (zzclg) {
                if (!zzclh) {
                    zzg.zzabT().zzf(context, zzqE(0));
                    zzclh = true;
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
        final int zza = zza(intent, zzcu);
        Log.d("FirebaseInstanceId", new StringBuilder(String.valueOf(str).length() + 47).append("background sync failed: ").append(str).append(", retry in ").append(zza).append("s").toString());
        synchronized (zzclg) {
            zzqF(zza);
            zzclh = true;
        }
        if (!zzcu) {
            if (this.zzcli) {
                Log.d("FirebaseInstanceId", "device not connected. Connectivity change received registered");
            }
            if (zzclf == null) {
                zzclf = new BroadcastReceiver(this) {
                    final /* synthetic */ FirebaseInstanceIdService zzclk;

                    public void onReceive(Context context, Intent intent) {
                        if (FirebaseInstanceIdService.zzcu(context)) {
                            if (this.zzclk.zzcli) {
                                Log.d("FirebaseInstanceId", "connectivity changed. starting background sync.");
                            }
                            this.zzclk.getApplicationContext().unregisterReceiver(this);
                            zzg.zzabT().zzf(context, FirebaseInstanceIdService.zzqE(zza));
                        }
                    }
                };
            }
            getApplicationContext().registerReceiver(zzclf, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
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

    private static Intent zzqE(int i) {
        Intent intent = new Intent("ACTION_TOKEN_REFRESH_RETRY");
        intent.putExtra("next_retry_delay_in_seconds", i);
        return intent;
    }

    private void zzqF(int i) {
        ((AlarmManager) getSystemService("alarm")).set(3, SystemClock.elapsedRealtime() + ((long) (i * 1000)), zzg.zza(this, 0, zzqE(i * 2), 134217728));
    }

    @WorkerThread
    public void onTokenRefresh() {
    }

    protected Intent zzF(Intent intent) {
        return zzg.zzabT().zzabU();
    }

    public boolean zzH(Intent intent) {
        this.zzcli = Log.isLoggable("FirebaseInstanceId", 3);
        if (intent.getStringExtra("error") == null && intent.getStringExtra("registration_id") == null) {
            return false;
        }
        String zzJ = zzJ(intent);
        if (this.zzcli) {
            String str = "FirebaseInstanceId";
            String str2 = "Register result in service ";
            String valueOf = String.valueOf(zzJ);
            Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
        zzjw(zzJ).zzabQ().zzv(intent);
        return true;
    }

    public void zzI(Intent intent) {
        String zzJ = zzJ(intent);
        zzd zzjw = zzjw(zzJ);
        String stringExtra = intent.getStringExtra("CMD");
        if (this.zzcli) {
            String valueOf = String.valueOf(intent.getExtras());
            Log.d("FirebaseInstanceId", new StringBuilder(((String.valueOf(zzJ).length() + 18) + String.valueOf(stringExtra).length()) + String.valueOf(valueOf).length()).append("Service command ").append(zzJ).append(" ").append(stringExtra).append(" ").append(valueOf).toString());
        }
        if (intent.getStringExtra("unregistered") != null) {
            zzh zzabP = zzjw.zzabP();
            if (zzJ == null) {
                zzJ = "";
            }
            zzabP.zzeK(zzJ);
            zzjw.zzabQ().zzv(intent);
        } else if ("gcm.googleapis.com/refresh".equals(intent.getStringExtra("from"))) {
            zzjw.zzabP().zzeK(zzJ);
            zza(intent, false, true);
        } else if ("RST".equals(stringExtra)) {
            zzjw.zzHh();
            zza(intent, true, true);
        } else if ("RST_FULL".equals(stringExtra)) {
            if (!zzjw.zzabP().isEmpty()) {
                zzjw.zzHh();
                zzjw.zzabP().zzHn();
                zza(intent, true, true);
            }
        } else if ("SYNC".equals(stringExtra)) {
            zzjw.zzabP().zzeK(zzJ);
            zza(intent, false, true);
        } else if ("PING".equals(stringExtra)) {
            zza(zzjw.zzabQ(), intent.getExtras());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void zzm(Intent intent) {
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
                zzI(intent);
                return;
        }
    }
}
