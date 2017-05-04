package com.google.android.gms.iid;

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
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.util.zzt;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class zzc {
    static String zzbhQ = null;
    static boolean zzbhR = false;
    static int zzbhS = 0;
    static int zzbhT = 0;
    static int zzbhU = 0;
    static BroadcastReceiver zzbhV = null;
    PendingIntent zzbgG;
    Messenger zzbgK;
    Map<String, Object> zzbhW = new HashMap();
    Messenger zzbhX;
    MessengerCompat zzbhY;
    long zzbhZ;
    long zzbia;
    int zzbib;
    int zzbic;
    long zzbid;
    Context zzqn;

    public zzc(Context context) {
        this.zzqn = context;
    }

    private void zzG(Object obj) {
        synchronized (getClass()) {
            for (String str : this.zzbhW.keySet()) {
                Object obj2 = this.zzbhW.get(str);
                this.zzbhW.put(str, obj);
                zzg(obj2, obj);
            }
        }
    }

    public static synchronized String zzHn() {
        String num;
        synchronized (zzc.class) {
            int i = zzbhU;
            zzbhU = i + 1;
            num = Integer.toString(i);
        }
        return num;
    }

    static String zza(KeyPair keyPair, String... strArr) {
        String str = null;
        try {
            byte[] bytes = TextUtils.join("\n", strArr).getBytes("UTF-8");
            try {
                PrivateKey privateKey = keyPair.getPrivate();
                Signature instance = Signature.getInstance(privateKey instanceof RSAPrivateKey ? "SHA256withRSA" : "SHA256withECDSA");
                instance.initSign(privateKey);
                instance.update(bytes);
                str = InstanceID.zzv(instance.sign());
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
                zzbhR = false;
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

    private Intent zzb(Bundle bundle, KeyPair keyPair) throws IOException {
        Intent intent;
        ConditionVariable conditionVariable = new ConditionVariable();
        String zzHn = zzHn();
        synchronized (getClass()) {
            this.zzbhW.put(zzHn, conditionVariable);
        }
        zza(bundle, keyPair, zzHn);
        conditionVariable.block(30000);
        synchronized (getClass()) {
            Object remove = this.zzbhW.remove(zzHn);
            if (remove instanceof Intent) {
                intent = (Intent) remove;
            } else if (remove instanceof String) {
                throw new IOException((String) remove);
            } else {
                String valueOf = String.valueOf(remove);
                Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 12).append("No response ").append(valueOf).toString());
                throw new IOException(InstanceID.ERROR_TIMEOUT);
            }
        }
        return intent;
    }

    private static boolean zzb(PackageManager packageManager) {
        for (ResolveInfo resolveInfo : packageManager.queryBroadcastReceivers(new Intent("com.google.iid.TOKEN_REQUEST"), 0)) {
            if (zza(packageManager, resolveInfo.activityInfo.packageName, "com.google.iid.TOKEN_REQUEST")) {
                zzbhR = true;
                return true;
            }
        }
        return false;
    }

    private static boolean zzb(PackageManager packageManager, String str) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
            zzbhQ = applicationInfo.packageName;
            zzbhT = applicationInfo.uid;
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static String zzbA(Context context) {
        if (zzbhQ != null) {
            return zzbhQ;
        }
        zzbhS = Process.myUid();
        PackageManager packageManager = context.getPackageManager();
        if (zzt.zzzq()) {
            if (zzb(packageManager) || zza(packageManager)) {
                return zzbhQ;
            }
        } else if (zza(packageManager) || zzb(packageManager)) {
            return zzbhQ;
        }
        Log.w("InstanceID/Rpc", "Failed to resolve IID implementation package, falling back");
        if (zzb(packageManager, "com.google.android.gms")) {
            zzbhR = zzt.zzzq();
            return zzbhQ;
        } else if (VERSION.SDK_INT >= 21 || !zzb(packageManager, "com.google.android.gsf")) {
            Log.w("InstanceID/Rpc", "Google Play services is missing, unable to get tokens");
            return null;
        } else {
            zzbhR = false;
            return zzbhQ;
        }
    }

    private static int zzbB(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(zzbA(context), 0).versionCode;
        } catch (NameNotFoundException e) {
            return -1;
        }
    }

    public static boolean zzbz(Context context) {
        if (zzbhQ != null) {
            zzbA(context);
        }
        return zzbhR;
    }

    private void zzeF(String str) {
        if ("com.google.android.gsf".equals(zzbhQ)) {
            this.zzbib++;
            if (this.zzbib >= 3) {
                if (this.zzbib == 3) {
                    this.zzbic = new Random().nextInt(1000) + 1000;
                }
                this.zzbic *= 2;
                this.zzbid = SystemClock.elapsedRealtime() + ((long) this.zzbic);
                Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(str).length() + 31).append("Backoff due to ").append(str).append(" for ").append(this.zzbic).toString());
            }
        }
    }

    private void zzg(Object obj, Object obj2) {
        if (obj instanceof ConditionVariable) {
            ((ConditionVariable) obj).open();
        }
        if (obj instanceof Messenger) {
            Messenger messenger = (Messenger) obj;
            Message obtain = Message.obtain();
            obtain.obj = obj2;
            try {
                messenger.send(obtain);
            } catch (RemoteException e) {
                String valueOf = String.valueOf(e);
                Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 24).append("Failed to send response ").append(valueOf).toString());
            }
        }
    }

    private void zzi(String str, Object obj) {
        synchronized (getClass()) {
            Object obj2 = this.zzbhW.get(str);
            this.zzbhW.put(str, obj);
            zzg(obj2, obj);
        }
    }

    void zzHl() {
        if (this.zzbgK == null) {
            zzbA(this.zzqn);
            this.zzbgK = new Messenger(new Handler(this, Looper.getMainLooper()) {
                final /* synthetic */ zzc zzbie;

                public void handleMessage(Message message) {
                    this.zzbie.zze(message);
                }
            });
        }
    }

    void zzHm() {
        synchronized (this) {
            if (zzbhV == null) {
                zzbhV = new BroadcastReceiver(this) {
                    final /* synthetic */ zzc zzbie;

                    {
                        this.zzbie = r1;
                    }

                    public void onReceive(Context context, Intent intent) {
                        if (Log.isLoggable("InstanceID/Rpc", 3)) {
                            Log.d("InstanceID/Rpc", "Received GSF callback via dynamic receiver");
                        }
                        this.zzbie.zzs(intent);
                    }
                };
                if (Log.isLoggable("InstanceID/Rpc", 3)) {
                    Log.d("InstanceID/Rpc", "Registered GSF callback receiver");
                }
                IntentFilter intentFilter = new IntentFilter("com.google.android.c2dm.intent.REGISTRATION");
                intentFilter.addCategory(this.zzqn.getPackageName());
                this.zzqn.registerReceiver(zzbhV, intentFilter, "com.google.android.c2dm.permission.SEND", null);
            }
        }
    }

    Intent zza(Bundle bundle, KeyPair keyPair) throws IOException {
        Intent zzb = zzb(bundle, keyPair);
        if (zzb == null || !zzb.hasExtra("google.messenger")) {
            return zzb;
        }
        zzb = zzb(bundle, keyPair);
        return (zzb == null || !zzb.hasExtra("google.messenger")) ? zzb : null;
    }

    void zza(Bundle bundle, KeyPair keyPair, String str) throws IOException {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (this.zzbid == 0 || elapsedRealtime > this.zzbid) {
            zzHl();
            if (zzbhQ == null) {
                throw new IOException(InstanceID.ERROR_MISSING_INSTANCEID_SERVICE);
            }
            this.zzbhZ = SystemClock.elapsedRealtime();
            Intent intent = new Intent(zzbhR ? "com.google.iid.TOKEN_REQUEST" : "com.google.android.c2dm.intent.REGISTER");
            intent.setPackage(zzbhQ);
            bundle.putString("gmsv", Integer.toString(zzbB(this.zzqn)));
            bundle.putString("osv", Integer.toString(VERSION.SDK_INT));
            bundle.putString("app_ver", Integer.toString(InstanceID.zzbw(this.zzqn)));
            bundle.putString("app_ver_name", InstanceID.zzbx(this.zzqn));
            bundle.putString("cliv", "iid-10240000");
            bundle.putString("appid", InstanceID.zza(keyPair));
            bundle.putString("pub2", InstanceID.zzv(keyPair.getPublic().getEncoded()));
            bundle.putString("sig", zza(keyPair, this.zzqn.getPackageName(), r0));
            intent.putExtras(bundle);
            zzp(intent);
            zzb(intent, str);
            return;
        }
        Log.w("InstanceID/Rpc", "Backoff mode, next request attempt: " + (this.zzbid - elapsedRealtime) + " interval: " + this.zzbic);
        throw new IOException(InstanceID.ERROR_BACKOFF);
    }

    protected void zzb(Intent intent, String str) {
        this.zzbhZ = SystemClock.elapsedRealtime();
        intent.putExtra("kid", new StringBuilder(String.valueOf(str).length() + 5).append("|ID|").append(str).append("|").toString());
        intent.putExtra("X-kid", new StringBuilder(String.valueOf(str).length() + 5).append("|ID|").append(str).append("|").toString());
        boolean equals = "com.google.android.gsf".equals(zzbhQ);
        String stringExtra = intent.getStringExtra("useGsf");
        if (stringExtra != null) {
            equals = "1".equals(stringExtra);
        }
        if (Log.isLoggable("InstanceID/Rpc", 3)) {
            String valueOf = String.valueOf(intent.getExtras());
            Log.d("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 8).append("Sending ").append(valueOf).toString());
        }
        if (this.zzbhX != null) {
            intent.putExtra("google.messenger", this.zzbgK);
            Message obtain = Message.obtain();
            obtain.obj = intent;
            try {
                this.zzbhX.send(obtain);
                return;
            } catch (RemoteException e) {
                if (Log.isLoggable("InstanceID/Rpc", 3)) {
                    Log.d("InstanceID/Rpc", "Messenger failed, fallback to startService");
                }
            }
        }
        if (equals) {
            zzHm();
            this.zzqn.sendBroadcast(intent);
            return;
        }
        intent.putExtra("google.messenger", this.zzbgK);
        intent.putExtra("messenger2", "1");
        if (this.zzbhY != null) {
            Message obtain2 = Message.obtain();
            obtain2.obj = intent;
            try {
                this.zzbhY.send(obtain2);
                return;
            } catch (RemoteException e2) {
                if (Log.isLoggable("InstanceID/Rpc", 3)) {
                    Log.d("InstanceID/Rpc", "Messenger failed, fallback to startService");
                }
            }
        }
        if (zzbhR) {
            this.zzqn.sendBroadcast(intent);
        } else {
            this.zzqn.startService(intent);
        }
    }

    public void zze(Message message) {
        if (message != null) {
            if (message.obj instanceof Intent) {
                Intent intent = (Intent) message.obj;
                intent.setExtrasClassLoader(MessengerCompat.class.getClassLoader());
                if (intent.hasExtra("google.messenger")) {
                    Parcelable parcelableExtra = intent.getParcelableExtra("google.messenger");
                    if (parcelableExtra instanceof MessengerCompat) {
                        this.zzbhY = (MessengerCompat) parcelableExtra;
                    }
                    if (parcelableExtra instanceof Messenger) {
                        this.zzbhX = (Messenger) parcelableExtra;
                    }
                }
                zzs((Intent) message.obj);
                return;
            }
            Log.w("InstanceID/Rpc", "Dropping invalid message");
        }
    }

    synchronized void zzp(Intent intent) {
        if (this.zzbgG == null) {
            Intent intent2 = new Intent();
            intent2.setPackage("com.google.example.invalidpackage");
            this.zzbgG = PendingIntent.getBroadcast(this.zzqn, 0, intent2, 0);
        }
        intent.putExtra("app", this.zzbgG);
    }

    String zzq(Intent intent) throws IOException {
        if (intent == null) {
            throw new IOException("SERVICE_NOT_AVAILABLE");
        }
        String stringExtra = intent.getStringExtra("registration_id");
        if (stringExtra == null) {
            stringExtra = intent.getStringExtra("unregistered");
        }
        intent.getLongExtra("Retry-After", 0);
        if (stringExtra != null) {
            return stringExtra;
        }
        stringExtra = intent.getStringExtra("error");
        if (stringExtra != null) {
            throw new IOException(stringExtra);
        }
        String valueOf = String.valueOf(intent.getExtras());
        Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 29).append("Unexpected response from GCM ").append(valueOf).toString(), new Throwable());
        throw new IOException("SERVICE_NOT_AVAILABLE");
    }

    void zzr(Intent intent) {
        String stringExtra = intent.getStringExtra("error");
        if (stringExtra == null) {
            String valueOf = String.valueOf(intent.getExtras());
            Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 49).append("Unexpected response, no error or registration id ").append(valueOf).toString());
            return;
        }
        if (Log.isLoggable("InstanceID/Rpc", 3)) {
            valueOf = "InstanceID/Rpc";
            String str = "Received InstanceID error ";
            String valueOf2 = String.valueOf(stringExtra);
            Log.d(valueOf, valueOf2.length() != 0 ? str.concat(valueOf2) : new String(str));
        }
        if (stringExtra.startsWith("|")) {
            String[] split = stringExtra.split("\\|");
            if (!"ID".equals(split[1])) {
                String str2 = "InstanceID/Rpc";
                String str3 = "Unexpected structured response ";
                valueOf2 = String.valueOf(stringExtra);
                Log.w(str2, valueOf2.length() != 0 ? str3.concat(valueOf2) : new String(str3));
            }
            if (split.length > 2) {
                valueOf2 = split[2];
                valueOf = split[3];
                if (valueOf.startsWith(":")) {
                    valueOf = valueOf.substring(1);
                }
            } else {
                valueOf = "UNKNOWN";
                valueOf2 = null;
            }
            intent.putExtra("error", valueOf);
        } else {
            valueOf2 = null;
            valueOf = stringExtra;
        }
        if (valueOf2 == null) {
            zzG(valueOf);
        } else {
            zzi(valueOf2, valueOf);
        }
        long longExtra = intent.getLongExtra("Retry-After", 0);
        if (longExtra > 0) {
            this.zzbia = SystemClock.elapsedRealtime();
            this.zzbic = ((int) longExtra) * 1000;
            this.zzbid = SystemClock.elapsedRealtime() + ((long) this.zzbic);
            Log.w("InstanceID/Rpc", "Explicit request from server to backoff: " + this.zzbic);
        } else if ("SERVICE_NOT_AVAILABLE".equals(valueOf) || "AUTHENTICATION_FAILED".equals(valueOf)) {
            zzeF(valueOf);
        }
    }

    public void zzs(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            String stringExtra;
            if ("com.google.android.c2dm.intent.REGISTRATION".equals(action) || "com.google.android.gms.iid.InstanceID".equals(action)) {
                action = intent.getStringExtra("registration_id");
                stringExtra = action == null ? intent.getStringExtra("unregistered") : action;
                if (stringExtra == null) {
                    zzr(intent);
                    return;
                }
                this.zzbhZ = SystemClock.elapsedRealtime();
                this.zzbid = 0;
                this.zzbib = 0;
                this.zzbic = 0;
                action = null;
                if (stringExtra.startsWith("|")) {
                    String[] split = stringExtra.split("\\|");
                    if (!"ID".equals(split[1])) {
                        String str = "InstanceID/Rpc";
                        String str2 = "Unexpected structured response ";
                        action = String.valueOf(stringExtra);
                        Log.w(str, action.length() != 0 ? str2.concat(action) : new String(str2));
                    }
                    stringExtra = split[2];
                    if (split.length > 4) {
                        if ("SYNC".equals(split[3])) {
                            InstanceIDListenerService.zzby(this.zzqn);
                        } else if ("RST".equals(split[3])) {
                            InstanceIDListenerService.zza(this.zzqn, InstanceID.getInstance(this.zzqn).zzHj());
                            intent.removeExtra("registration_id");
                            zzi(stringExtra, intent);
                            return;
                        }
                    }
                    action = split[split.length - 1];
                    if (action.startsWith(":")) {
                        action = action.substring(1);
                    }
                    intent.putExtra("registration_id", action);
                    action = stringExtra;
                }
                if (action == null) {
                    zzG(intent);
                } else {
                    zzi(action, intent);
                }
            } else if (Log.isLoggable("InstanceID/Rpc", 3)) {
                stringExtra = "InstanceID/Rpc";
                String str3 = "Unexpected response ";
                action = String.valueOf(intent.getAction());
                Log.d(stringExtra, action.length() != 0 ? str3.concat(action) : new String(str3));
            }
        } else if (Log.isLoggable("InstanceID/Rpc", 3)) {
            Log.d("InstanceID/Rpc", "Unexpected response: null");
        }
    }
}
