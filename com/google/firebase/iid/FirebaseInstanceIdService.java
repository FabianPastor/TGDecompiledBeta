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
    private static Object zzckB = new Object();
    @VisibleForTesting
    private static boolean zzckC = false;
    private boolean zzckD = false;

    static class zza extends BroadcastReceiver {
        @Nullable
        private static BroadcastReceiver receiver;
        private int zzckE;

        private zza(int i) {
            this.zzckE = i;
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
                } else if (FirebaseInstanceIdService.zzbJ(context)) {
                    if (Log.isLoggable("FirebaseInstanceId", 3)) {
                        Log.d("FirebaseInstanceId", "connectivity changed. starting background sync.");
                    }
                    context.getApplicationContext().unregisterReceiver(this);
                    receiver = null;
                    zzq.zzJX().zze(context, FirebaseInstanceIdService.zzbZ(this.zzckE));
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void zza(Context context, FirebaseInstanceId firebaseInstanceId) {
        synchronized (zzckB) {
            if (zzckC) {
            }
        }
    }

    private final void zza(Intent intent, String str) {
        int i = 28800;
        boolean zzbJ = zzbJ(this);
        int intExtra = intent == null ? 10 : intent.getIntExtra("next_retry_delay_in_seconds", 0);
        if (intExtra < 10 && !zzbJ) {
            i = 30;
        } else if (intExtra < 10) {
            i = 10;
        } else if (intExtra <= 28800) {
            i = intExtra;
        }
        Log.d("FirebaseInstanceId", new StringBuilder(String.valueOf(str).length() + 47).append("background sync failed: ").append(str).append(", retry in ").append(i).append("s").toString());
        synchronized (zzckB) {
            ((AlarmManager) getSystemService("alarm")).set(3, SystemClock.elapsedRealtime() + ((long) (i * 1000)), zzq.zza(this, 0, zzbZ(i << 1), 134217728));
            zzckC = true;
        }
        if (!zzbJ) {
            if (this.zzckD) {
                Log.d("FirebaseInstanceId", "device not connected. Connectivity change received registered");
            }
            zza.zzl(this, i);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void zza(Intent intent, boolean z, boolean z2) {
        synchronized (zzckB) {
            zzckC = false;
        }
        if (zzl.zzbd(this) != null) {
            FirebaseInstanceId instance = FirebaseInstanceId.getInstance();
            zzs zzJQ = instance.zzJQ();
            if (zzJQ == null || zzJQ.zzhp(zzj.zzbgW)) {
                try {
                    String zzJR = instance.zzJR();
                    if (zzJR != null) {
                        if (this.zzckD) {
                            Log.d("FirebaseInstanceId", "get master token succeeded");
                        }
                        zza((Context) this, instance);
                        if (z2 || zzJQ == null || !(zzJQ == null || zzJR.equals(zzJQ.zzbPJ))) {
                            onTokenRefresh();
                            return;
                        }
                        return;
                    }
                    zza(intent, "returned token is null");
                    return;
                } catch (IOException e) {
                    zza(intent, e.getMessage());
                    return;
                } catch (Throwable e2) {
                    Log.e("FirebaseInstanceId", "Unable to get master token", e2);
                    return;
                }
            }
            zzk zzJS = FirebaseInstanceId.zzJS();
            for (String zzJV = zzJS.zzJV(); zzJV != null; zzJV = zzJS.zzJV()) {
                String[] split = zzJV.split("!");
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
                                        FirebaseInstanceId.getInstance().zzhg(str2);
                                        if (!this.zzckD) {
                                            Log.d("FirebaseInstanceId", "subscribe operation succeeded");
                                            break;
                                        }
                                        break;
                                    case 1:
                                        FirebaseInstanceId.getInstance().zzhh(str2);
                                        if (!this.zzckD) {
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
                                FirebaseInstanceId.getInstance().zzhg(str2);
                                if (!this.zzckD) {
                                    break;
                                }
                                Log.d("FirebaseInstanceId", "subscribe operation succeeded");
                                break;
                            case 1:
                                FirebaseInstanceId.getInstance().zzhh(str2);
                                if (!this.zzckD) {
                                    break;
                                }
                                Log.d("FirebaseInstanceId", "unsubscribe operation succeeded");
                                break;
                            default:
                                continue;
                        }
                    } catch (IOException e3) {
                        zza(intent, e3.getMessage());
                        return;
                    }
                }
                zzJS.zzhj(zzJV);
            }
            Log.d("FirebaseInstanceId", "topic sync succeeded");
        }
    }

    static void zzbI(Context context) {
        if (zzl.zzbd(context) != null) {
            synchronized (zzckB) {
                if (!zzckC) {
                    zzq.zzJX().zze(context, zzbZ(0));
                    zzckC = true;
                }
            }
        }
    }

    private static boolean zzbJ(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static Intent zzbZ(int i) {
        Intent intent = new Intent("ACTION_TOKEN_REFRESH_RETRY");
        intent.putExtra("next_retry_delay_in_seconds", i);
        return intent;
    }

    private final zzj zzhi(String str) {
        if (str == null) {
            return zzj.zzb(this, null);
        }
        Bundle bundle = new Bundle();
        bundle.putString("subtype", str);
        return zzj.zzb(this, bundle);
    }

    private static String zzp(Intent intent) {
        String stringExtra = intent.getStringExtra("subtype");
        return stringExtra == null ? "" : stringExtra;
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
                action = zzp(intent);
                zzj zzhi = zzhi(action);
                String stringExtra = intent.getStringExtra("CMD");
                if (this.zzckD) {
                    String valueOf = String.valueOf(intent.getExtras());
                    Log.d("FirebaseInstanceId", new StringBuilder(((String.valueOf(action).length() + 18) + String.valueOf(stringExtra).length()) + String.valueOf(valueOf).length()).append("Service command ").append(action).append(" ").append(stringExtra).append(" ").append(valueOf).toString());
                }
                if (intent.getStringExtra("unregistered") != null) {
                    zzr zzJT = zzj.zzJT();
                    if (action == null) {
                        action = "";
                    }
                    zzJT.zzdr(action);
                    zzj.zzJU().zzi(intent);
                    return;
                } else if ("gcm.googleapis.com/refresh".equals(intent.getStringExtra("from"))) {
                    zzj.zzJT().zzdr(action);
                    zza(intent, false, true);
                    return;
                } else if ("RST".equals(stringExtra)) {
                    zzhi.zzvL();
                    zza(intent, true, true);
                    return;
                } else if ("RST_FULL".equals(stringExtra)) {
                    if (!zzj.zzJT().isEmpty()) {
                        zzhi.zzvL();
                        zzj.zzJT().zzvP();
                        zza(intent, true, true);
                        return;
                    }
                    return;
                } else if ("SYNC".equals(stringExtra)) {
                    zzj.zzJT().zzdr(action);
                    zza(intent, false, true);
                    return;
                } else if ("PING".equals(stringExtra)) {
                    Bundle extras = intent.getExtras();
                    String zzbd = zzl.zzbd(this);
                    if (zzbd == null) {
                        Log.w("FirebaseInstanceId", "Unable to respond to ping due to missing target package");
                        return;
                    }
                    Intent intent2 = new Intent("com.google.android.gcm.intent.SEND");
                    intent2.setPackage(zzbd);
                    intent2.putExtras(extras);
                    zzl.zzd(this, intent2);
                    intent2.putExtra("google.to", "google.com/iid");
                    intent2.putExtra("google.message_id", zzl.zzvO());
                    sendOrderedBroadcast(intent2, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
                    return;
                } else {
                    return;
                }
        }
    }

    @WorkerThread
    public void onTokenRefresh() {
    }

    protected final Intent zzn(Intent intent) {
        return (Intent) zzq.zzJX().zzckP.poll();
    }

    public final boolean zzo(Intent intent) {
        this.zzckD = Log.isLoggable("FirebaseInstanceId", 3);
        if (intent.getStringExtra("error") == null && intent.getStringExtra("registration_id") == null) {
            return false;
        }
        String zzp = zzp(intent);
        if (this.zzckD) {
            String str = "FirebaseInstanceId";
            String str2 = "Register result in service ";
            String valueOf = String.valueOf(zzp);
            Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
        zzhi(zzp);
        zzj.zzJU().zzi(intent);
        return true;
    }
}
