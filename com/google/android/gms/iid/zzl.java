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
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.util.zzq;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.telegram.messenger.exoplayer2.C;

public final class zzl {
    private static String zzifp = null;
    private static boolean zzifq = false;
    private static int zzifr = 0;
    private static int zzifs = 0;
    private static int zzift = 0;
    private static BroadcastReceiver zzifu = null;
    private Context zzair;
    private PendingIntent zzicn;
    private Messenger zzicr;
    private Map<String, Object> zzifv = new HashMap();
    private Messenger zzifw;
    private MessengerCompat zzifx;
    private long zzify;
    private long zzifz;
    private int zziga;
    private int zzigb;
    private long zzigc;

    public zzl(Context context) {
        this.zzair = context;
    }

    private static String zza(KeyPair keyPair, String... strArr) {
        String str = null;
        try {
            byte[] bytes = TextUtils.join("\n", strArr).getBytes(C.UTF8_NAME);
            try {
                PrivateKey privateKey = keyPair.getPrivate();
                Signature instance = Signature.getInstance(privateKey instanceof RSAPrivateKey ? "SHA256withRSA" : "SHA256withECDSA");
                instance.initSign(privateKey);
                instance.update(bytes);
                str = InstanceID.zzo(instance.sign());
            } catch (Throwable e) {
                Log.e("InstanceID/Rpc", "Unable to sign registration request", e);
            }
        } catch (Throwable e2) {
            Log.e("InstanceID/Rpc", "Unable to encode string", e2);
        }
        return str;
    }

    private static boolean zza(PackageManager packageManager) {
        for (ResolveInfo resolveInfo : packageManager.queryBroadcastReceivers(new Intent("com.google.iid.TOKEN_REQUEST"), 0)) {
            if (zza(packageManager, resolveInfo.activityInfo.packageName, "com.google.iid.TOKEN_REQUEST")) {
                zzifq = true;
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

    private final void zzae(Object obj) {
        synchronized (getClass()) {
            for (String str : this.zzifv.keySet()) {
                Object obj2 = this.zzifv.get(str);
                this.zzifv.put(str, obj);
                zze(obj2, obj);
            }
        }
    }

    private final void zzavh() {
        if (this.zzicr == null) {
            zzdp(this.zzair);
            this.zzicr = new Messenger(new zzm(this, Looper.getMainLooper()));
        }
    }

    private static synchronized String zzavi() {
        String num;
        synchronized (zzl.class) {
            int i = zzift;
            zzift = i + 1;
            num = Integer.toString(i);
        }
        return num;
    }

    private final Intent zzb(Bundle bundle, KeyPair keyPair) throws IOException {
        ConditionVariable conditionVariable = new ConditionVariable();
        String zzavi = zzavi();
        synchronized (getClass()) {
            this.zzifv.put(zzavi, conditionVariable);
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (this.zzigc == 0 || elapsedRealtime > this.zzigc) {
            zzavh();
            if (zzifp == null) {
                throw new IOException("MISSING_INSTANCEID_SERVICE");
            }
            String valueOf;
            Object remove;
            Intent intent;
            this.zzify = SystemClock.elapsedRealtime();
            Intent intent2 = new Intent(zzifq ? "com.google.iid.TOKEN_REQUEST" : "com.google.android.c2dm.intent.REGISTER");
            intent2.setPackage(zzifp);
            bundle.putString("gmsv", Integer.toString(zzdq(this.zzair)));
            bundle.putString("osv", Integer.toString(VERSION.SDK_INT));
            bundle.putString("app_ver", Integer.toString(InstanceID.zzdm(this.zzair)));
            bundle.putString("app_ver_name", InstanceID.zzdn(this.zzair));
            bundle.putString("cliv", "iid-11910000");
            bundle.putString("appid", InstanceID.zza(keyPair));
            bundle.putString("pub2", InstanceID.zzo(keyPair.getPublic().getEncoded()));
            bundle.putString("sig", zza(keyPair, this.zzair.getPackageName(), valueOf));
            intent2.putExtras(bundle);
            zzi(intent2);
            this.zzify = SystemClock.elapsedRealtime();
            intent2.putExtra("kid", new StringBuilder(String.valueOf(zzavi).length() + 5).append("|ID|").append(zzavi).append("|").toString());
            intent2.putExtra("X-kid", new StringBuilder(String.valueOf(zzavi).length() + 5).append("|ID|").append(zzavi).append("|").toString());
            boolean equals = "com.google.android.gsf".equals(zzifp);
            String stringExtra = intent2.getStringExtra("useGsf");
            if (stringExtra != null) {
                equals = "1".equals(stringExtra);
            }
            if (Log.isLoggable("InstanceID/Rpc", 3)) {
                String valueOf2 = String.valueOf(intent2.getExtras());
                Log.d("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf2).length() + 8).append("Sending ").append(valueOf2).toString());
            }
            if (this.zzifw != null) {
                intent2.putExtra("google.messenger", this.zzicr);
                Message obtain = Message.obtain();
                obtain.obj = intent2;
                try {
                    this.zzifw.send(obtain);
                } catch (RemoteException e) {
                    if (Log.isLoggable("InstanceID/Rpc", 3)) {
                        Log.d("InstanceID/Rpc", "Messenger failed, fallback to startService");
                    }
                }
                conditionVariable.block(30000);
                synchronized (getClass()) {
                    remove = this.zzifv.remove(zzavi);
                    if (remove instanceof Intent) {
                        intent = (Intent) remove;
                    } else if (remove instanceof String) {
                        valueOf = String.valueOf(remove);
                        Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 12).append("No response ").append(valueOf).toString());
                        throw new IOException("TIMEOUT");
                    } else {
                        throw new IOException((String) remove);
                    }
                }
                return intent;
            }
            if (equals) {
                synchronized (this) {
                    if (zzifu == null) {
                        zzifu = new zzn(this);
                        if (Log.isLoggable("InstanceID/Rpc", 3)) {
                            Log.d("InstanceID/Rpc", "Registered GSF callback receiver");
                        }
                        IntentFilter intentFilter = new IntentFilter("com.google.android.c2dm.intent.REGISTRATION");
                        intentFilter.addCategory(this.zzair.getPackageName());
                        this.zzair.registerReceiver(zzifu, intentFilter, "com.google.android.c2dm.permission.SEND", null);
                    }
                }
                this.zzair.sendBroadcast(intent2);
            } else {
                intent2.putExtra("google.messenger", this.zzicr);
                intent2.putExtra("messenger2", "1");
                if (this.zzifx != null) {
                    Message obtain2 = Message.obtain();
                    obtain2.obj = intent2;
                    try {
                        this.zzifx.send(obtain2);
                    } catch (RemoteException e2) {
                        if (Log.isLoggable("InstanceID/Rpc", 3)) {
                            Log.d("InstanceID/Rpc", "Messenger failed, fallback to startService");
                        }
                    }
                }
                if (zzifq) {
                    this.zzair.sendBroadcast(intent2);
                } else {
                    this.zzair.startService(intent2);
                }
            }
            conditionVariable.block(30000);
            synchronized (getClass()) {
                remove = this.zzifv.remove(zzavi);
                if (remove instanceof Intent) {
                    intent = (Intent) remove;
                } else if (remove instanceof String) {
                    valueOf = String.valueOf(remove);
                    Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 12).append("No response ").append(valueOf).toString());
                    throw new IOException("TIMEOUT");
                } else {
                    throw new IOException((String) remove);
                }
            }
            return intent;
        }
        Log.w("InstanceID/Rpc", "Backoff mode, next request attempt: " + (this.zzigc - elapsedRealtime) + " interval: " + this.zzigb);
        throw new IOException("RETRY_LATER");
    }

    private static boolean zzb(PackageManager packageManager, String str) {
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
            zzifp = applicationInfo.packageName;
            zzifs = applicationInfo.uid;
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static String zzdp(Context context) {
        if (zzifp != null) {
            return zzifp;
        }
        zzifr = Process.myUid();
        PackageManager packageManager = context.getPackageManager();
        if (!zzq.isAtLeastO()) {
            boolean z;
            for (ResolveInfo resolveInfo : packageManager.queryIntentServices(new Intent("com.google.android.c2dm.intent.REGISTER"), 0)) {
                if (zza(packageManager, resolveInfo.serviceInfo.packageName, "com.google.android.c2dm.intent.REGISTER")) {
                    zzifq = false;
                    z = true;
                    break;
                }
            }
            z = false;
            if (z) {
                return zzifp;
            }
        }
        if (zza(packageManager)) {
            return zzifp;
        }
        Log.w("InstanceID/Rpc", "Failed to resolve IID implementation package, falling back");
        if (zzb(packageManager, "com.google.android.gms")) {
            zzifq = zzq.isAtLeastO();
            return zzifp;
        } else if (zzq.zzamn() || !zzb(packageManager, "com.google.android.gsf")) {
            Log.w("InstanceID/Rpc", "Google Play services is missing, unable to get tokens");
            return null;
        } else {
            zzifq = false;
            return zzifp;
        }
    }

    private static int zzdq(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(zzdp(context), 0).versionCode;
        } catch (NameNotFoundException e) {
            return -1;
        }
    }

    private static void zze(Object obj, Object obj2) {
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

    private final synchronized void zzi(Intent intent) {
        if (this.zzicn == null) {
            Intent intent2 = new Intent();
            intent2.setPackage("com.google.example.invalidpackage");
            this.zzicn = PendingIntent.getBroadcast(this.zzair, 0, intent2, 0);
        }
        intent.putExtra("app", this.zzicn);
    }

    private final void zzi(String str, Object obj) {
        synchronized (getClass()) {
            Object obj2 = this.zzifv.get(str);
            this.zzifv.put(str, obj);
            zze(obj2, obj);
        }
    }

    static String zzj(Intent intent) throws IOException {
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

    final Intent zza(Bundle bundle, KeyPair keyPair) throws IOException {
        Intent zzb = zzb(bundle, keyPair);
        if (zzb == null || !zzb.hasExtra("google.messenger")) {
            return zzb;
        }
        zzb = zzb(bundle, keyPair);
        return (zzb == null || !zzb.hasExtra("google.messenger")) ? zzb : null;
    }

    public final void zzc(Message message) {
        if (message != null) {
            if (message.obj instanceof Intent) {
                Intent intent = (Intent) message.obj;
                intent.setExtrasClassLoader(MessengerCompat.class.getClassLoader());
                if (intent.hasExtra("google.messenger")) {
                    Parcelable parcelableExtra = intent.getParcelableExtra("google.messenger");
                    if (parcelableExtra instanceof MessengerCompat) {
                        this.zzifx = (MessengerCompat) parcelableExtra;
                    }
                    if (parcelableExtra instanceof Messenger) {
                        this.zzifw = (Messenger) parcelableExtra;
                    }
                }
                zzk((Intent) message.obj);
                return;
            }
            Log.w("InstanceID/Rpc", "Dropping invalid message");
        }
    }

    public final void zzk(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            String stringExtra;
            String stringExtra2;
            if ("com.google.android.c2dm.intent.REGISTRATION".equals(action) || "com.google.android.gms.iid.InstanceID".equals(action)) {
                action = intent.getStringExtra("registration_id");
                stringExtra = action == null ? intent.getStringExtra("unregistered") : action;
                String str;
                String str2;
                if (stringExtra == null) {
                    stringExtra2 = intent.getStringExtra("error");
                    if (stringExtra2 == null) {
                        stringExtra = String.valueOf(intent.getExtras());
                        Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(stringExtra).length() + 49).append("Unexpected response, no error or registration id ").append(stringExtra).toString());
                        return;
                    }
                    if (Log.isLoggable("InstanceID/Rpc", 3)) {
                        stringExtra = "InstanceID/Rpc";
                        str = "Received InstanceID error ";
                        action = String.valueOf(stringExtra2);
                        Log.d(stringExtra, action.length() != 0 ? str.concat(action) : new String(str));
                    }
                    if (stringExtra2.startsWith("|")) {
                        String[] split = stringExtra2.split("\\|");
                        if (!"ID".equals(split[1])) {
                            str2 = "InstanceID/Rpc";
                            String str3 = "Unexpected structured response ";
                            action = String.valueOf(stringExtra2);
                            Log.w(str2, action.length() != 0 ? str3.concat(action) : new String(str3));
                        }
                        if (split.length > 2) {
                            action = split[2];
                            stringExtra = split[3];
                            if (stringExtra.startsWith(":")) {
                                stringExtra = stringExtra.substring(1);
                            }
                        } else {
                            stringExtra = "UNKNOWN";
                            action = null;
                        }
                        intent.putExtra("error", stringExtra);
                    } else {
                        action = null;
                        stringExtra = stringExtra2;
                    }
                    if (action == null) {
                        zzae(stringExtra);
                    } else {
                        zzi(action, stringExtra);
                    }
                    long longExtra = intent.getLongExtra("Retry-After", 0);
                    if (longExtra > 0) {
                        this.zzifz = SystemClock.elapsedRealtime();
                        this.zzigb = ((int) longExtra) * 1000;
                        this.zzigc = SystemClock.elapsedRealtime() + ((long) this.zzigb);
                        Log.w("InstanceID/Rpc", "Explicit request from server to backoff: " + this.zzigb);
                        return;
                    } else if (("SERVICE_NOT_AVAILABLE".equals(stringExtra) || "AUTHENTICATION_FAILED".equals(stringExtra)) && "com.google.android.gsf".equals(zzifp)) {
                        this.zziga++;
                        if (this.zziga >= 3) {
                            if (this.zziga == 3) {
                                this.zzigb = new Random().nextInt(1000) + 1000;
                            }
                            this.zzigb <<= 1;
                            this.zzigc = SystemClock.elapsedRealtime() + ((long) this.zzigb);
                            Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(stringExtra).length() + 31).append("Backoff due to ").append(stringExtra).append(" for ").append(this.zzigb).toString());
                            return;
                        }
                        return;
                    } else {
                        return;
                    }
                }
                this.zzify = SystemClock.elapsedRealtime();
                this.zzigc = 0;
                this.zziga = 0;
                this.zzigb = 0;
                action = null;
                if (stringExtra.startsWith("|")) {
                    String[] split2 = stringExtra.split("\\|");
                    if (!"ID".equals(split2[1])) {
                        str = "InstanceID/Rpc";
                        str2 = "Unexpected structured response ";
                        action = String.valueOf(stringExtra);
                        Log.w(str, action.length() != 0 ? str2.concat(action) : new String(str2));
                    }
                    stringExtra = split2[2];
                    if (split2.length > 4) {
                        Context context;
                        if ("SYNC".equals(split2[3])) {
                            context = this.zzair;
                            Intent intent2 = new Intent("com.google.android.gms.iid.InstanceID");
                            intent2.putExtra("CMD", "SYNC");
                            intent2.setClassName(context, "com.google.android.gms.gcm.GcmReceiver");
                            context.sendBroadcast(intent2);
                        } else if ("RST".equals(split2[3])) {
                            context = this.zzair;
                            InstanceID.getInstance(this.zzair);
                            InstanceIDListenerService.zza(context, InstanceID.zzavg());
                            intent.removeExtra("registration_id");
                            zzi(stringExtra, intent);
                            return;
                        }
                    }
                    action = split2[split2.length - 1];
                    if (action.startsWith(":")) {
                        action = action.substring(1);
                    }
                    intent.putExtra("registration_id", action);
                    action = stringExtra;
                }
                if (action == null) {
                    zzae(intent);
                } else {
                    zzi(action, intent);
                }
            } else if (Log.isLoggable("InstanceID/Rpc", 3)) {
                stringExtra = "InstanceID/Rpc";
                stringExtra2 = "Unexpected response ";
                action = String.valueOf(intent.getAction());
                Log.d(stringExtra, action.length() != 0 ? stringExtra2.concat(action) : new String(stringExtra2));
            }
        } else if (Log.isLoggable("InstanceID/Rpc", 3)) {
            Log.d("InstanceID/Rpc", "Unexpected response: null");
        }
    }
}
