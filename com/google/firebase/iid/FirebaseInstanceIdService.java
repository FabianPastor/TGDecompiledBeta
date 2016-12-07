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
    private static BroadcastReceiver bkG;
    private static final Object bkH = new Object();
    private static boolean bkI = false;
    private boolean bkJ = false;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void zza(Context context, FirebaseInstanceId firebaseInstanceId) {
        synchronized (bkH) {
            if (bkI) {
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zza(Intent intent, boolean z) {
        synchronized (bkH) {
            bkI = false;
        }
        if (zzf.zzdg(this) != null) {
            FirebaseInstanceId instance = FirebaseInstanceId.getInstance();
            if (instance.E() == null) {
                try {
                    if (instance.F() != null) {
                        if (this.bkJ) {
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
            zze G = instance.G();
            for (String L = G.L(); L != null; L = G.L()) {
                String[] split = L.split("!");
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
                                        FirebaseInstanceId.getInstance().zztr(str2);
                                        if (!this.bkJ) {
                                            Log.d("FirebaseInstanceId", "subscribe operation succeeded");
                                            break;
                                        }
                                        break;
                                    case 1:
                                        FirebaseInstanceId.getInstance().zzts(str2);
                                        if (!this.bkJ) {
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
                                FirebaseInstanceId.getInstance().zztr(str2);
                                if (!this.bkJ) {
                                    break;
                                }
                                Log.d("FirebaseInstanceId", "subscribe operation succeeded");
                                break;
                            case 1:
                                FirebaseInstanceId.getInstance().zzts(str2);
                                if (!this.bkJ) {
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
                G.zztu(L);
            }
            Log.d("FirebaseInstanceId", "topic sync succeeded");
        }
    }

    private void zza(zzf com_google_firebase_iid_zzf, Bundle bundle) {
        String zzdg = zzf.zzdg(this);
        if (zzdg == null) {
            Log.w("FirebaseInstanceId", "Unable to respond to ping due to missing target package");
            return;
        }
        Intent intent = new Intent("com.google.android.gcm.intent.SEND");
        intent.setPackage(zzdg);
        intent.putExtras(bundle);
        com_google_firebase_iid_zzf.zzs(intent);
        intent.putExtra("google.to", "google.com/iid");
        intent.putExtra("google.message_id", zzf.zzboo());
        sendOrderedBroadcast(intent, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
    }

    private static Intent zzagb(int i) {
        Context applicationContext = FirebaseApp.getInstance().getApplicationContext();
        Intent intent = new Intent("ACTION_TOKEN_REFRESH_RETRY");
        intent.putExtra("next_retry_delay_in_seconds", i);
        return FirebaseInstanceIdInternalReceiver.zzf(applicationContext, intent);
    }

    private void zzagc(int i) {
        ((AlarmManager) getSystemService("alarm")).set(3, SystemClock.elapsedRealtime() + ((long) (i * 1000)), PendingIntent.getBroadcast(this, 0, zzagb(i * 2), 268435456));
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
        boolean zzes = zzes(this);
        final int zzb = zzb(intent, zzes);
        Log.d("FirebaseInstanceId", new StringBuilder(String.valueOf(str).length() + 47).append("background sync failed: ").append(str).append(", retry in ").append(zzb).append("s").toString());
        synchronized (bkH) {
            zzagc(zzb);
            bkI = true;
        }
        if (!zzes) {
            if (this.bkJ) {
                Log.d("FirebaseInstanceId", "device not connected. Connectivity change received registered");
            }
            if (bkG == null) {
                bkG = new BroadcastReceiver(this) {
                    final /* synthetic */ FirebaseInstanceIdService bkL;

                    public void onReceive(Context context, Intent intent) {
                        if (FirebaseInstanceIdService.zzes(context)) {
                            if (this.bkL.bkJ) {
                                Log.d("FirebaseInstanceId", "connectivity changed. starting background sync.");
                            }
                            this.bkL.getApplicationContext().unregisterReceiver(this);
                            context.sendBroadcast(FirebaseInstanceIdService.zzagb(zzb));
                        }
                    }
                };
            }
            getApplicationContext().registerReceiver(bkG, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }

    static void zzer(Context context) {
        if (zzf.zzdg(context) != null) {
            synchronized (bkH) {
                if (!bkI) {
                    context.sendBroadcast(zzagb(0));
                    bkI = true;
                }
            }
        }
    }

    private static boolean zzes(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private zzd zztt(String str) {
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
        return FirebaseInstanceIdInternalReceiver.H();
    }

    public boolean zzag(Intent intent) {
        this.bkJ = Log.isLoggable("FirebaseInstanceId", 3);
        if (intent.getStringExtra("error") == null && intent.getStringExtra("registration_id") == null) {
            return false;
        }
        String zzai = zzai(intent);
        if (this.bkJ) {
            String str = "FirebaseInstanceId";
            String str2 = "Register result in service ";
            String valueOf = String.valueOf(zzai);
            Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
        zztt(zzai).K().zzv(intent);
        return true;
    }

    public void zzah(Intent intent) {
        String zzai = zzai(intent);
        zzd zztt = zztt(zzai);
        String stringExtra = intent.getStringExtra("CMD");
        if (this.bkJ) {
            String valueOf = String.valueOf(intent.getExtras());
            Log.d("FirebaseInstanceId", new StringBuilder(((String.valueOf(zzai).length() + 18) + String.valueOf(stringExtra).length()) + String.valueOf(valueOf).length()).append("Service command ").append(zzai).append(" ").append(stringExtra).append(" ").append(valueOf).toString());
        }
        if (intent.getStringExtra("unregistered") != null) {
            zzg J = zztt.J();
            if (zzai == null) {
                zzai = "";
            }
            J.zzku(zzai);
            zztt.K().zzv(intent);
        } else if ("gcm.googleapis.com/refresh".equals(intent.getStringExtra("from"))) {
            zztt.J().zzku(zzai);
            zza(intent, false);
        } else if ("RST".equals(stringExtra)) {
            zztt.zzboj();
            zza(intent, true);
        } else if ("RST_FULL".equals(stringExtra)) {
            if (!zztt.J().isEmpty()) {
                zztt.zzboj();
                zztt.J().zzbop();
                zza(intent, true);
            }
        } else if ("SYNC".equals(stringExtra)) {
            zztt.J().zzku(zzai);
            zza(intent, false);
        } else if ("PING".equals(stringExtra)) {
            zza(zztt.K(), intent.getExtras());
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
