package com.google.firebase.iid;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.MessengerCompat;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class zzf {
    static String zzbhq = null;
    static int zzbhr = 0;
    static int zzbhs = 0;
    static int zzbht = 0;
    PendingIntent zzbga;
    Messenger zzbge;
    int zzbhA;
    long zzbhB;
    Map<String, Object> zzbhu = new HashMap();
    Messenger zzbhv;
    MessengerCompat zzbhw;
    long zzbhx;
    long zzbhy;
    int zzbhz;
    Context zzqr;

    public zzf(Context context) {
        this.zzqr = context;
    }

    private void zzG(Object obj) {
        synchronized (getClass()) {
            for (String str : this.zzbhu.keySet()) {
                Object obj2 = this.zzbhu.get(str);
                this.zzbhu.put(str, obj);
                zzh(obj2, obj);
            }
        }
    }

    public static synchronized String zzGz() {
        String num;
        synchronized (zzf.class) {
            int i = zzbht;
            zzbht = i + 1;
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
                str = FirebaseInstanceId.zzv(instance.sign());
            } catch (Throwable e) {
                Log.e("InstanceID/Rpc", "Unable to sign registration request", e);
            }
        } catch (Throwable e2) {
            Log.e("InstanceID/Rpc", "Unable to encode string", e2);
        }
        return str;
    }

    private Intent zzb(Bundle bundle, KeyPair keyPair) throws IOException {
        Intent intent;
        ConditionVariable conditionVariable = new ConditionVariable();
        String zzGz = zzGz();
        synchronized (getClass()) {
            this.zzbhu.put(zzGz, conditionVariable);
        }
        zza(bundle, keyPair, zzGz);
        conditionVariable.block(30000);
        synchronized (getClass()) {
            Object remove = this.zzbhu.remove(zzGz);
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

    public static String zzbi(Context context) {
        if (zzbhq != null) {
            return zzbhq;
        }
        zzbhr = Process.myUid();
        PackageManager packageManager = context.getPackageManager();
        for (ResolveInfo resolveInfo : packageManager.queryIntentServices(new Intent("com.google.android.c2dm.intent.REGISTER"), 0)) {
            if (packageManager.checkPermission("com.google.android.c2dm.permission.RECEIVE", resolveInfo.serviceInfo.packageName) == 0) {
                try {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(resolveInfo.serviceInfo.packageName, 0);
                    Log.w("InstanceID/Rpc", "Found " + applicationInfo.uid);
                    zzbhs = applicationInfo.uid;
                    zzbhq = resolveInfo.serviceInfo.packageName;
                    return zzbhq;
                } catch (NameNotFoundException e) {
                }
            } else {
                String valueOf = String.valueOf(resolveInfo.serviceInfo.packageName);
                String valueOf2 = String.valueOf("com.google.android.c2dm.intent.REGISTER");
                Log.w("InstanceID/Rpc", new StringBuilder((String.valueOf(valueOf).length() + 56) + String.valueOf(valueOf2).length()).append("Possible malicious package ").append(valueOf).append(" declares ").append(valueOf2).append(" without permission").toString());
            }
        }
        Log.w("InstanceID/Rpc", "Failed to resolve REGISTER intent, falling back");
        ApplicationInfo applicationInfo2;
        try {
            applicationInfo2 = packageManager.getApplicationInfo("com.google.android.gms", 0);
            zzbhq = applicationInfo2.packageName;
            zzbhs = applicationInfo2.uid;
            return zzbhq;
        } catch (NameNotFoundException e2) {
            try {
                applicationInfo2 = packageManager.getApplicationInfo("com.google.android.gsf", 0);
                zzbhq = applicationInfo2.packageName;
                zzbhs = applicationInfo2.uid;
                return zzbhq;
            } catch (NameNotFoundException e3) {
                Log.w("InstanceID/Rpc", "Both Google Play Services and legacy GSF package are missing");
                return null;
            }
        }
    }

    private void zzeJ(String str) {
        if ("com.google.android.gsf".equals(zzbhq)) {
            this.zzbhz++;
            if (this.zzbhz >= 3) {
                if (this.zzbhz == 3) {
                    this.zzbhA = new Random().nextInt(1000) + 1000;
                }
                this.zzbhA *= 2;
                this.zzbhB = SystemClock.elapsedRealtime() + ((long) this.zzbhA);
                Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(str).length() + 31).append("Backoff due to ").append(str).append(" for ").append(this.zzbhA).toString());
            }
        }
    }

    private void zzh(Object obj, Object obj2) {
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
            Object obj2 = this.zzbhu.get(str);
            this.zzbhu.put(str, obj);
            zzh(obj2, obj);
        }
    }

    void zzGy() {
        if (this.zzbge == null) {
            zzbi(this.zzqr);
            this.zzbge = new Messenger(new Handler(this, Looper.getMainLooper()) {
                final /* synthetic */ zzf zzcjd;

                public void handleMessage(Message message) {
                    this.zzcjd.zze(message);
                }
            });
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

    public void zza(Bundle bundle, KeyPair keyPair, String str) throws IOException {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (this.zzbhB == 0 || elapsedRealtime > this.zzbhB) {
            zzGy();
            if (zzbhq == null) {
                throw new IOException(InstanceID.ERROR_MISSING_INSTANCEID_SERVICE);
            }
            this.zzbhx = SystemClock.elapsedRealtime();
            Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
            intent.setPackage(zzbhq);
            bundle.putString("gmsv", Integer.toString(FirebaseInstanceId.zzK(this.zzqr, zzbi(this.zzqr))));
            bundle.putString("osv", Integer.toString(VERSION.SDK_INT));
            bundle.putString("app_ver", Integer.toString(FirebaseInstanceId.zzbU(this.zzqr)));
            bundle.putString("app_ver_name", FirebaseInstanceId.zzbg(this.zzqr));
            bundle.putString("cliv", "fiid-10084000");
            bundle.putString("appid", FirebaseInstanceId.zza(keyPair));
            String zzbT = FirebaseInstanceId.zzbT(this.zzqr);
            if (zzbT != null) {
                bundle.putString("gmp_app_id", zzbT);
            }
            bundle.putString("pub2", FirebaseInstanceId.zzv(keyPair.getPublic().getEncoded()));
            bundle.putString("sig", zza(keyPair, this.zzqr.getPackageName(), zzbT));
            intent.putExtras(bundle);
            zzs(intent);
            zzb(intent, str);
            return;
        }
        elapsedRealtime = this.zzbhB - elapsedRealtime;
        Log.w("InstanceID/Rpc", "Backoff mode, next request attempt: " + elapsedRealtime + " interval: " + this.zzbhA);
        throw new IOException(InstanceID.ERROR_BACKOFF);
    }

    protected void zzb(Intent intent, String str) {
        this.zzbhx = SystemClock.elapsedRealtime();
        intent.putExtra("kid", new StringBuilder(String.valueOf(str).length() + 5).append("|ID|").append(str).append("|").toString());
        intent.putExtra("X-kid", new StringBuilder(String.valueOf(str).length() + 5).append("|ID|").append(str).append("|").toString());
        boolean equals = "com.google.android.gsf".equals(zzbhq);
        if (Log.isLoggable("InstanceID/Rpc", 3)) {
            String valueOf = String.valueOf(intent.getExtras());
            Log.d("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 8).append("Sending ").append(valueOf).toString());
        }
        if (equals) {
            this.zzqr.startService(intent);
            return;
        }
        intent.putExtra("google.messenger", this.zzbge);
        if (!(this.zzbhv == null && this.zzbhw == null)) {
            Message obtain = Message.obtain();
            obtain.obj = intent;
            try {
                if (this.zzbhv != null) {
                    this.zzbhv.send(obtain);
                    return;
                } else {
                    this.zzbhw.send(obtain);
                    return;
                }
            } catch (RemoteException e) {
                if (Log.isLoggable("InstanceID/Rpc", 3)) {
                    Log.d("InstanceID/Rpc", "Messenger failed, fallback to startService");
                }
            }
        }
        this.zzqr.startService(intent);
    }

    public void zze(Message message) {
        if (message != null) {
            if (message.obj instanceof Intent) {
                Intent intent = (Intent) message.obj;
                intent.setExtrasClassLoader(MessengerCompat.class.getClassLoader());
                if (intent.hasExtra("google.messenger")) {
                    Parcelable parcelableExtra = intent.getParcelableExtra("google.messenger");
                    if (parcelableExtra instanceof MessengerCompat) {
                        this.zzbhw = (MessengerCompat) parcelableExtra;
                    }
                    if (parcelableExtra instanceof Messenger) {
                        this.zzbhv = (Messenger) parcelableExtra;
                    }
                }
                zzv((Intent) message.obj);
                return;
            }
            Log.w("InstanceID/Rpc", "Dropping invalid message");
        }
    }

    synchronized void zzs(Intent intent) {
        if (this.zzbga == null) {
            Intent intent2 = new Intent();
            intent2.setPackage("com.google.example.invalidpackage");
            this.zzbga = PendingIntent.getBroadcast(this.zzqr, 0, intent2, 0);
        }
        intent.putExtra("app", this.zzbga);
    }

    String zzt(Intent intent) throws IOException {
        if (intent == null) {
            throw new IOException("SERVICE_NOT_AVAILABLE");
        }
        String stringExtra = intent.getStringExtra("registration_id");
        if (stringExtra == null) {
            stringExtra = intent.getStringExtra("unregistered");
        }
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

    void zzu(Intent intent) {
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
            this.zzbhy = SystemClock.elapsedRealtime();
            this.zzbhA = ((int) longExtra) * 1000;
            this.zzbhB = SystemClock.elapsedRealtime() + ((long) this.zzbhA);
            Log.w("InstanceID/Rpc", "Explicit request from server to backoff: " + this.zzbhA);
        } else if ("SERVICE_NOT_AVAILABLE".equals(valueOf) || "AUTHENTICATION_FAILED".equals(valueOf)) {
            zzeJ(valueOf);
        }
    }

    void zzv(Intent intent) {
        if (intent != null) {
            String stringExtra;
            String str;
            if ("com.google.android.c2dm.intent.REGISTRATION".equals(intent.getAction())) {
                stringExtra = intent.getStringExtra("registration_id");
                if (stringExtra == null) {
                    stringExtra = intent.getStringExtra("unregistered");
                }
                if (stringExtra == null) {
                    zzu(intent);
                    return;
                }
                this.zzbhx = SystemClock.elapsedRealtime();
                this.zzbhB = 0;
                this.zzbhz = 0;
                this.zzbhA = 0;
                if (stringExtra.startsWith("|")) {
                    String[] split = stringExtra.split("\\|");
                    if (!"ID".equals(split[1])) {
                        str = "InstanceID/Rpc";
                        String str2 = "Unexpected structured response ";
                        stringExtra = String.valueOf(stringExtra);
                        Log.w(str, stringExtra.length() != 0 ? str2.concat(stringExtra) : new String(str2));
                    }
                    str = split[2];
                    if (split.length > 4) {
                        if ("SYNC".equals(split[3])) {
                            FirebaseInstanceId.zzbh(this.zzqr);
                        } else if ("RST".equals(split[3])) {
                            FirebaseInstanceId.zza(this.zzqr, zzd.zzb(this.zzqr, null).zzaag());
                            intent.removeExtra("registration_id");
                            zzi(str, intent);
                            return;
                        }
                    }
                    stringExtra = split[split.length - 1];
                    if (stringExtra.startsWith(":")) {
                        stringExtra = stringExtra.substring(1);
                    }
                    intent.putExtra("registration_id", stringExtra);
                    stringExtra = str;
                } else {
                    stringExtra = null;
                }
                if (stringExtra != null) {
                    zzi(stringExtra, intent);
                } else if (Log.isLoggable("InstanceID/Rpc", 3)) {
                    Log.d("InstanceID/Rpc", "Ignoring response without a request ID");
                }
            } else if (Log.isLoggable("InstanceID/Rpc", 3)) {
                str = "InstanceID/Rpc";
                String str3 = "Unexpected response ";
                stringExtra = String.valueOf(intent.getAction());
                Log.d(str, stringExtra.length() != 0 ? str3.concat(stringExtra) : new String(str3));
            }
        } else if (Log.isLoggable("InstanceID/Rpc", 3)) {
            Log.d("InstanceID/Rpc", "Unexpected response: null");
        }
    }
}
