package com.google.firebase.iid;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.util.zzq;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.MessengerCompat;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.util.Random;

public final class zzl {
    private static PendingIntent zzbfP;
    private static String zzbgZ = null;
    private static boolean zzbha = false;
    private static int zzbhb = 0;
    private static int zzbhc = 0;
    private static int zzbhd = 0;
    private static BroadcastReceiver zzbhe = null;
    private Messenger zzbfT;
    private Messenger zzbhg;
    private MessengerCompat zzbhh;
    private long zzbhi;
    private long zzbhj;
    private int zzbhk;
    private int zzbhl;
    private long zzbhm;
    private final SimpleArrayMap<String, zzp> zzckI = new SimpleArrayMap();
    private Context zzqD;

    public zzl(Context context) {
        this.zzqD = context;
    }

    private static String zza(KeyPair keyPair, String... strArr) {
        String str = null;
        try {
            byte[] bytes = TextUtils.join("\n", strArr).getBytes("UTF-8");
            try {
                PrivateKey privateKey = keyPair.getPrivate();
                Signature instance = Signature.getInstance(privateKey instanceof RSAPrivateKey ? "SHA256withRSA" : "SHA256withECDSA");
                instance.initSign(privateKey);
                instance.update(bytes);
                str = FirebaseInstanceId.zzj(instance.sign());
            } catch (Throwable e) {
                Log.e("InstanceID/Rpc", "Unable to sign registration request", e);
            }
        } catch (Throwable e2) {
            Log.e("InstanceID/Rpc", "Unable to encode string", e2);
        }
        return str;
    }

    private static boolean zza(PackageManager packageManager) {
        for (ResolveInfo resolveInfo : packageManager.queryIntentServices(new Intent("com.google.android.c2dm.intent.REGISTER"), 0)) {
            if (zza(packageManager, resolveInfo.serviceInfo.packageName, "com.google.android.c2dm.intent.REGISTER")) {
                zzbha = false;
                return true;
            }
        }
        return false;
    }

    private static boolean zza(PackageManager packageManager, String str, String str2) {
        if (packageManager.checkPermission("com.google.android.c2dm.permission.SEND", str) == 0) {
            return zzb(packageManager, str);
        }
        Log.w("InstanceID/Rpc", new StringBuilder((String.valueOf(str).length() + 56) + String.valueOf(str2).length()).append("Possible malicious package ").append(str).append(" declares ").append(str2).append(" without permission").toString());
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void zzah(String str, String str2) {
        synchronized (this.zzckI) {
            if (str == null) {
                for (int i = 0; i < this.zzckI.size(); i++) {
                    ((zzp) this.zzckI.valueAt(i)).onError(str2);
                }
                this.zzckI.clear();
            } else {
                zzp com_google_firebase_iid_zzp = (zzp) this.zzckI.remove(str);
                if (com_google_firebase_iid_zzp == null) {
                    String str3 = "InstanceID/Rpc";
                    String str4 = "Missing callback for ";
                    String valueOf = String.valueOf(str);
                    Log.w(str3, valueOf.length() != 0 ? str4.concat(valueOf) : new String(str4));
                    return;
                }
                com_google_firebase_iid_zzp.onError(str2);
            }
        }
    }

    private final Intent zzb(Bundle bundle, KeyPair keyPair) throws IOException {
        String zzvO = zzvO();
        zzo com_google_firebase_iid_zzo = new zzo();
        synchronized (this.zzckI) {
            this.zzckI.put(zzvO, com_google_firebase_iid_zzo);
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (this.zzbhm == 0 || elapsedRealtime > this.zzbhm) {
            zzvN();
            if (zzbgZ == null) {
                throw new IOException(InstanceID.ERROR_MISSING_INSTANCEID_SERVICE);
            }
            this.zzbhi = SystemClock.elapsedRealtime();
            Intent intent = new Intent(zzbha ? "com.google.iid.TOKEN_REQUEST" : "com.google.android.c2dm.intent.REGISTER");
            intent.setPackage(zzbgZ);
            bundle.putString("gmsv", Integer.toString(FirebaseInstanceId.zzO(this.zzqD, zzbd(this.zzqD))));
            bundle.putString("osv", Integer.toString(VERSION.SDK_INT));
            bundle.putString("app_ver", Integer.toString(FirebaseInstanceId.zzbF(this.zzqD)));
            bundle.putString("app_ver_name", FirebaseInstanceId.zzbb(this.zzqD));
            bundle.putString("cliv", "fiid-11020000");
            bundle.putString("appid", FirebaseInstanceId.zza(keyPair));
            bundle.putString("pub2", FirebaseInstanceId.zzj(keyPair.getPublic().getEncoded()));
            bundle.putString("sig", zza(keyPair, this.zzqD.getPackageName(), r0));
            intent.putExtras(bundle);
            zzd(this.zzqD, intent);
            this.zzbhi = SystemClock.elapsedRealtime();
            intent.putExtra("kid", new StringBuilder(String.valueOf(zzvO).length() + 5).append("|ID|").append(zzvO).append("|").toString());
            intent.putExtra("X-kid", new StringBuilder(String.valueOf(zzvO).length() + 5).append("|ID|").append(zzvO).append("|").toString());
            boolean equals = "com.google.android.gsf".equals(zzbgZ);
            if (Log.isLoggable("InstanceID/Rpc", 3)) {
                String valueOf = String.valueOf(intent.getExtras());
                Log.d("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 8).append("Sending ").append(valueOf).toString());
            }
            if (equals) {
                synchronized (this) {
                    if (zzbhe == null) {
                        zzbhe = new zzn(this);
                        if (Log.isLoggable("InstanceID/Rpc", 3)) {
                            Log.d("InstanceID/Rpc", "Registered GSF callback receiver");
                        }
                        IntentFilter intentFilter = new IntentFilter("com.google.android.c2dm.intent.REGISTRATION");
                        intentFilter.addCategory(this.zzqD.getPackageName());
                        this.zzqD.registerReceiver(zzbhe, intentFilter, "com.google.android.c2dm.permission.SEND", null);
                    }
                }
                this.zzqD.startService(intent);
            } else {
                intent.putExtra("google.messenger", this.zzbfT);
                if (!(this.zzbhg == null && this.zzbhh == null)) {
                    Message obtain = Message.obtain();
                    obtain.obj = intent;
                    try {
                        if (this.zzbhg != null) {
                            this.zzbhg.send(obtain);
                        } else {
                            this.zzbhh.send(obtain);
                        }
                    } catch (RemoteException e) {
                        if (Log.isLoggable("InstanceID/Rpc", 3)) {
                            Log.d("InstanceID/Rpc", "Messenger failed, fallback to startService");
                        }
                    }
                }
                if (zzbha) {
                    this.zzqD.sendBroadcast(intent);
                } else {
                    this.zzqD.startService(intent);
                }
            }
            try {
                Intent zzJW = com_google_firebase_iid_zzo.zzJW();
                synchronized (this.zzckI) {
                    this.zzckI.remove(zzvO);
                }
                return zzJW;
            } catch (Throwable th) {
                synchronized (this.zzckI) {
                    this.zzckI.remove(zzvO);
                }
            }
        } else {
            Log.w("InstanceID/Rpc", "Backoff mode, next request attempt: " + (this.zzbhm - elapsedRealtime) + " interval: " + this.zzbhl);
            throw new IOException(InstanceID.ERROR_BACKOFF);
        }
    }

    private final void zzb(String str, Intent intent) {
        synchronized (this.zzckI) {
            zzp com_google_firebase_iid_zzp = (zzp) this.zzckI.remove(str);
            if (com_google_firebase_iid_zzp == null) {
                String str2 = "InstanceID/Rpc";
                String str3 = "Missing callback for ";
                String valueOf = String.valueOf(str);
                Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
                return;
            }
            com_google_firebase_iid_zzp.zzq(intent);
        }
    }

    private static boolean zzb(PackageManager packageManager, String str) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
            zzbgZ = applicationInfo.packageName;
            zzbhc = applicationInfo.uid;
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static String zzbd(Context context) {
        if (zzbgZ != null) {
            return zzbgZ;
        }
        boolean z;
        zzbhb = Process.myUid();
        PackageManager packageManager = context.getPackageManager();
        for (ResolveInfo resolveInfo : packageManager.queryBroadcastReceivers(new Intent("com.google.iid.TOKEN_REQUEST"), 0)) {
            if (zza(packageManager, resolveInfo.activityInfo.packageName, "com.google.iid.TOKEN_REQUEST")) {
                zzbha = true;
                z = true;
                break;
            }
        }
        z = false;
        if (z) {
            return zzbgZ;
        }
        if (!zzq.isAtLeastO() && zza(packageManager)) {
            return zzbgZ;
        }
        Log.w("InstanceID/Rpc", "Failed to resolve IID implementation package, falling back");
        if (zzb(packageManager, "com.google.android.gms")) {
            zzbha = zzq.isAtLeastO();
            return zzbgZ;
        } else if (zzq.zzse() || !zzb(packageManager, "com.google.android.gsf")) {
            Log.w("InstanceID/Rpc", "Google Play services is missing, unable to get tokens");
            return null;
        } else {
            zzbha = false;
            return zzbgZ;
        }
    }

    public static synchronized void zzd(Context context, Intent intent) {
        synchronized (zzl.class) {
            if (zzbfP == null) {
                Intent intent2 = new Intent();
                intent2.setPackage("com.google.example.invalidpackage");
                zzbfP = PendingIntent.getBroadcast(context, 0, intent2, 0);
            }
            intent.putExtra("app", zzbfP);
        }
    }

    private final void zzvN() {
        if (this.zzbfT == null) {
            zzbd(this.zzqD);
            this.zzbfT = new Messenger(new zzm(this, Looper.getMainLooper()));
        }
    }

    public static synchronized String zzvO() {
        String num;
        synchronized (zzl.class) {
            int i = zzbhd;
            zzbhd = i + 1;
            num = Integer.toString(i);
        }
        return num;
    }

    final Intent zza(Bundle bundle, KeyPair keyPair) throws IOException {
        Intent zzb = zzb(bundle, keyPair);
        if (zzb == null || !zzb.hasExtra("google.messenger")) {
            return zzb;
        }
        zzb = zzb(bundle, keyPair);
        return (zzb == null || !zzb.hasExtra("google.messenger")) ? zzb : null;
    }

    final void zzc(Message message) {
        if (message != null) {
            if (message.obj instanceof Intent) {
                Intent intent = (Intent) message.obj;
                intent.setExtrasClassLoader(MessengerCompat.class.getClassLoader());
                if (intent.hasExtra("google.messenger")) {
                    Parcelable parcelableExtra = intent.getParcelableExtra("google.messenger");
                    if (parcelableExtra instanceof MessengerCompat) {
                        this.zzbhh = (MessengerCompat) parcelableExtra;
                    }
                    if (parcelableExtra instanceof Messenger) {
                        this.zzbhg = (Messenger) parcelableExtra;
                    }
                }
                zzi((Intent) message.obj);
                return;
            }
            Log.w("InstanceID/Rpc", "Dropping invalid message");
        }
    }

    final void zzi(Intent intent) {
        String str = null;
        if (intent != null) {
            String stringExtra;
            String stringExtra2;
            if ("com.google.android.c2dm.intent.REGISTRATION".equals(intent.getAction())) {
                stringExtra = intent.getStringExtra("registration_id");
                if (stringExtra == null) {
                    stringExtra = intent.getStringExtra("unregistered");
                }
                String str2;
                String[] split;
                if (stringExtra == null) {
                    stringExtra2 = intent.getStringExtra("error");
                    if (stringExtra2 == null) {
                        str = String.valueOf(intent.getExtras());
                        Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(str).length() + 49).append("Unexpected response, no error or registration id ").append(str).toString());
                        return;
                    }
                    if (Log.isLoggable("InstanceID/Rpc", 3)) {
                        String str3 = "InstanceID/Rpc";
                        str2 = "Received InstanceID error ";
                        stringExtra = String.valueOf(stringExtra2);
                        Log.d(str3, stringExtra.length() != 0 ? str2.concat(stringExtra) : new String(str2));
                    }
                    if (stringExtra2.startsWith("|")) {
                        split = stringExtra2.split("\\|");
                        if (!"ID".equals(split[1])) {
                            str2 = "InstanceID/Rpc";
                            String str4 = "Unexpected structured response ";
                            stringExtra = String.valueOf(stringExtra2);
                            Log.w(str2, stringExtra.length() != 0 ? str4.concat(stringExtra) : new String(str4));
                        }
                        if (split.length > 2) {
                            stringExtra = split[2];
                            str = split[3];
                            if (str.startsWith(":")) {
                                str = str.substring(1);
                            }
                        } else {
                            str = "UNKNOWN";
                            stringExtra = null;
                        }
                        intent.putExtra("error", str);
                    } else {
                        stringExtra = null;
                        str = stringExtra2;
                    }
                    zzah(stringExtra, str);
                    long longExtra = intent.getLongExtra("Retry-After", 0);
                    if (longExtra > 0) {
                        this.zzbhj = SystemClock.elapsedRealtime();
                        this.zzbhl = ((int) longExtra) * 1000;
                        this.zzbhm = SystemClock.elapsedRealtime() + ((long) this.zzbhl);
                        Log.w("InstanceID/Rpc", "Explicit request from server to backoff: " + this.zzbhl);
                        return;
                    } else if (("SERVICE_NOT_AVAILABLE".equals(str) || "AUTHENTICATION_FAILED".equals(str)) && "com.google.android.gsf".equals(zzbgZ)) {
                        this.zzbhk++;
                        if (this.zzbhk >= 3) {
                            if (this.zzbhk == 3) {
                                this.zzbhl = new Random().nextInt(1000) + 1000;
                            }
                            this.zzbhl <<= 1;
                            this.zzbhm = SystemClock.elapsedRealtime() + ((long) this.zzbhl);
                            Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(str).length() + 31).append("Backoff due to ").append(str).append(" for ").append(this.zzbhl).toString());
                            return;
                        }
                        return;
                    } else {
                        return;
                    }
                }
                this.zzbhi = SystemClock.elapsedRealtime();
                this.zzbhm = 0;
                this.zzbhk = 0;
                this.zzbhl = 0;
                if (stringExtra.startsWith("|")) {
                    split = stringExtra.split("\\|");
                    if (!"ID".equals(split[1])) {
                        stringExtra2 = "InstanceID/Rpc";
                        str2 = "Unexpected structured response ";
                        stringExtra = String.valueOf(stringExtra);
                        Log.w(stringExtra2, stringExtra.length() != 0 ? str2.concat(stringExtra) : new String(str2));
                    }
                    stringExtra2 = split[2];
                    if (split.length > 4) {
                        if ("SYNC".equals(split[3])) {
                            FirebaseInstanceId.zzbG(this.zzqD);
                        } else if ("RST".equals(split[3])) {
                            Context context = this.zzqD;
                            zzj.zzb(this.zzqD, null);
                            FirebaseInstanceId.zza(context, zzj.zzJT());
                            intent.removeExtra("registration_id");
                            zzb(stringExtra2, intent);
                            return;
                        }
                    }
                    stringExtra = split[split.length - 1];
                    if (stringExtra.startsWith(":")) {
                        stringExtra = stringExtra.substring(1);
                    }
                    intent.putExtra("registration_id", stringExtra);
                    str = stringExtra2;
                }
                if (str != null) {
                    zzb(str, intent);
                } else if (Log.isLoggable("InstanceID/Rpc", 3)) {
                    Log.d("InstanceID/Rpc", "Ignoring response without a request ID");
                }
            } else if (Log.isLoggable("InstanceID/Rpc", 3)) {
                str = "InstanceID/Rpc";
                stringExtra2 = "Unexpected response ";
                stringExtra = String.valueOf(intent.getAction());
                Log.d(str, stringExtra.length() != 0 ? stringExtra2.concat(stringExtra) : new String(stringExtra2));
            }
        } else if (Log.isLoggable("InstanceID/Rpc", 3)) {
            Log.d("InstanceID/Rpc", "Unexpected response: null");
        }
    }
}
