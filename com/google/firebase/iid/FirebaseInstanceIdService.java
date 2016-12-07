package com.google.firebase.iid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import java.io.IOException;

public class FirebaseInstanceIdService extends zzb {
    private static BroadcastReceiver bhu;
    private static final Object bhv = new Object();
    private static boolean bhw = false;
    private boolean bhx = false;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void zza(Context context, FirebaseInstanceId firebaseInstanceId) {
        synchronized (bhv) {
            if (bhw) {
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zza(Intent intent, boolean z) {
        synchronized (bhv) {
            bhw = false;
        }
        if (zzf.zzdj(this) != null) {
            FirebaseInstanceId instance = FirebaseInstanceId.getInstance();
            if (instance.C() == null) {
                try {
                    if (instance.D() != null) {
                        if (this.bhx) {
                            Log.d("FirebaseInstanceId", "get master token succeeded");
                        }
                        zza((Context) this, instance);
                        onTokenRefresh();
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
            zze E = instance.E();
            for (String J = E.J(); J != null; J = E.J()) {
                String[] split = J.split("!");
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
                                        FirebaseInstanceId.getInstance().zzts(str2);
                                        if (!this.bhx) {
                                            Log.d("FirebaseInstanceId", "subscribe operation succeeded");
                                            break;
                                        }
                                        break;
                                    case 1:
                                        FirebaseInstanceId.getInstance().zztt(str2);
                                        if (!this.bhx) {
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
                                FirebaseInstanceId.getInstance().zzts(str2);
                                if (!this.bhx) {
                                    break;
                                }
                                Log.d("FirebaseInstanceId", "subscribe operation succeeded");
                                break;
                            case 1:
                                FirebaseInstanceId.getInstance().zztt(str2);
                                if (!this.bhx) {
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
                E.zztv(J);
            }
            Log.d("FirebaseInstanceId", "topic sync succeeded");
        }
    }

    private void zza(zzf com_google_firebase_iid_zzf, Bundle bundle) {
        String zzdj = zzf.zzdj(this);
        if (zzdj == null) {
            Log.w("FirebaseInstanceId", "Unable to respond to ping due to missing target package");
            return;
        }
        Intent intent = new Intent("com.google.android.gcm.intent.SEND");
        intent.setPackage(zzdj);
        intent.putExtras(bundle);
        com_google_firebase_iid_zzf.zzs(intent);
        intent.putExtra("google.to", "google.com/iid");
        intent.putExtra("google.message_id", zzf.zzbov());
        sendOrderedBroadcast(intent, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
    }

    private static Intent zzagk(int i) {
        Context applicationContext = FirebaseApp.getInstance().getApplicationContext();
        Intent intent = new Intent("ACTION_TOKEN_REFRESH_RETRY");
        intent.putExtra("next_retry_delay_in_seconds", i);
        return FirebaseInstanceIdInternalReceiver.zzg(applicationContext, intent);
    }

    private void zzagl(int i) {
        ((AlarmManager) getSystemService("alarm")).set(3, SystemClock.elapsedRealtime() + ((long) (i * 1000)), PendingIntent.getBroadcast(this, 0, zzagk(i * 2), 268435456));
    }

    private String zzai(Intent intent) {
        String stringExtra = intent.getStringExtra("subtype");
        return stringExtra == null ? "" : stringExtra;
    }

    private int zzb(Intent intent, boolean z) {
        int intExtra = intent == null ? 10 : intent.getIntExtra("next_retry_delay_in_seconds", 0);
        return (intExtra >= 10 || z) ? intExtra >= 10 ? intExtra > 28800 ? 28800 : intExtra : 10 : 30;
    }

    private void zzd(Intent intent, String str) {
        boolean zzeu = zzeu(this);
        final int zzb = zzb(intent, zzeu);
        Log.d("FirebaseInstanceId", new StringBuilder(String.valueOf(str).length() + 47).append("background sync failed: ").append(str).append(", retry in ").append(zzb).append("s").toString());
        synchronized (bhv) {
            zzagl(zzb);
            bhw = true;
        }
        if (!zzeu) {
            if (this.bhx) {
                Log.d("FirebaseInstanceId", "device not connected. Connectivity change received registered");
            }
            if (bhu == null) {
                bhu = new BroadcastReceiver(this) {
                    final /* synthetic */ FirebaseInstanceIdService bhz;

                    public void onReceive(Context context, Intent intent) {
                        if (FirebaseInstanceIdService.zzeu(context)) {
                            if (this.bhz.bhx) {
                                Log.d("FirebaseInstanceId", "connectivity changed. starting background sync.");
                            }
                            this.bhz.getApplicationContext().unregisterReceiver(this);
                            context.sendBroadcast(FirebaseInstanceIdService.zzagk(zzb));
                        }
                    }
                };
            }
            getApplicationContext().registerReceiver(bhu, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }

    static void zzet(Context context) {
        if (zzf.zzdj(context) != null) {
            synchronized (bhv) {
                if (!bhw) {
                    context.sendBroadcast(zzagk(0));
                    bhw = true;
                }
            }
        }
    }

    private static boolean zzeu(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private zzd zztu(String str) {
        if (str == null) {
            return zzd.zzb(this, null);
        }
        Bundle bundle = new Bundle();
        bundle.putString("subtype", str);
        return zzd.zzb(this, bundle);
    }

    @WorkerThread
    public void onTokenRefresh() {
    }

    protected Intent zzae(Intent intent) {
        return FirebaseInstanceIdInternalReceiver.F();
    }

    public boolean zzag(Intent intent) {
        this.bhx = Log.isLoggable("FirebaseInstanceId", 3);
        if (intent.getStringExtra("error") == null && intent.getStringExtra("registration_id") == null) {
            return false;
        }
        String zzai = zzai(intent);
        if (this.bhx) {
            String str = "FirebaseInstanceId";
            String str2 = "Register result in service ";
            String valueOf = String.valueOf(zzai);
            Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
        zztu(zzai).I().zzv(intent);
        return true;
    }

    public void zzah(Intent intent) {
        String zzai = zzai(intent);
        zzd zztu = zztu(zzai);
        String stringExtra = intent.getStringExtra("CMD");
        if (this.bhx) {
            String valueOf = String.valueOf(intent.getExtras());
            Log.d("FirebaseInstanceId", new StringBuilder(((String.valueOf(zzai).length() + 18) + String.valueOf(stringExtra).length()) + String.valueOf(valueOf).length()).append("Service command ").append(zzai).append(" ").append(stringExtra).append(" ").append(valueOf).toString());
        }
        if (intent.getStringExtra("unregistered") != null) {
            zzg H = zztu.H();
            if (zzai == null) {
                zzai = "";
            }
            H.zzku(zzai);
            zztu.I().zzv(intent);
        } else if ("gcm.googleapis.com/refresh".equals(intent.getStringExtra("from"))) {
            zztu.H().zzku(zzai);
            zza(intent, false);
        } else if ("RST".equals(stringExtra)) {
            zztu.zzboq();
            zza(intent, true);
        } else if ("RST_FULL".equals(stringExtra)) {
            if (!zztu.H().isEmpty()) {
                zztu.zzboq();
                zztu.H().zzbow();
                zza(intent, true);
            }
        } else if ("SYNC".equals(stringExtra)) {
            zztu.H().zzku(zzai);
            zza(intent, false);
        } else if ("PING".equals(stringExtra)) {
            zza(zztu.I(), intent.getExtras());
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
                zza(intent, false);
                return;
            default:
                zzah(intent);
                return;
        }
    }
}
