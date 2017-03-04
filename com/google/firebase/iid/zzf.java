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
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.MessengerCompat;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.util.Random;

public class zzf {
    static String zzbhW = null;
    static int zzbhX = 0;
    static int zzbhY = 0;
    static int zzbhZ = 0;
    PendingIntent zzbgF;
    Messenger zzbgJ;
    Messenger zzbib;
    MessengerCompat zzbic;
    long zzbid;
    long zzbie;
    int zzbif;
    int zzbig;
    long zzbih;
    private final SimpleArrayMap<String, zzb> zzclo = new SimpleArrayMap();
    Context zzqn;

    private interface zzb {
        void onError(String str);

        void zzK(Intent intent);
    }

    private static class zza implements zzb {
        private Intent intent;
        private final ConditionVariable zzclq;
        private String zzclr;

        private zza() {
            this.zzclq = new ConditionVariable();
        }

        public void onError(String str) {
            this.zzclr = str;
            this.zzclq.open();
        }

        public void zzK(Intent intent) {
            this.intent = intent;
            this.zzclq.open();
        }

        public Intent zzabS() throws IOException {
            if (!this.zzclq.block(30000)) {
                Log.w("InstanceID/Rpc", "No response");
                throw new IOException(InstanceID.ERROR_TIMEOUT);
            } else if (this.zzclr == null) {
                return this.intent;
            } else {
                throw new IOException(this.zzclr);
            }
        }
    }

    public zzf(Context context) {
        this.zzqn = context;
    }

    public static synchronized String zzHm() {
        String num;
        synchronized (zzf.class) {
            int i = zzbhZ;
            zzbhZ = i + 1;
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void zzay(String str, String str2) {
        synchronized (this.zzclo) {
            if (str == null) {
                for (int i = 0; i < this.zzclo.size(); i++) {
                    ((zzb) this.zzclo.valueAt(i)).onError(str2);
                }
                this.zzclo.clear();
            } else {
                zzb com_google_firebase_iid_zzf_zzb = (zzb) this.zzclo.remove(str);
                if (com_google_firebase_iid_zzf_zzb == null) {
                    String str3 = "InstanceID/Rpc";
                    String str4 = "Missing callback for ";
                    String valueOf = String.valueOf(str);
                    Log.w(str3, valueOf.length() != 0 ? str4.concat(valueOf) : new String(str4));
                    return;
                }
                com_google_firebase_iid_zzf_zzb.onError(str2);
            }
        }
    }

    private Intent zzb(Bundle bundle, KeyPair keyPair) throws IOException {
        String zzHm = zzHm();
        zza com_google_firebase_iid_zzf_zza = new zza();
        synchronized (this.zzclo) {
            this.zzclo.put(zzHm, com_google_firebase_iid_zzf_zza);
        }
        zza(bundle, keyPair, zzHm);
        try {
            Intent zzabS = com_google_firebase_iid_zzf_zza.zzabS();
            synchronized (this.zzclo) {
                this.zzclo.remove(zzHm);
            }
            return zzabS;
        } catch (Throwable th) {
            synchronized (this.zzclo) {
                this.zzclo.remove(zzHm);
            }
        }
    }

    private void zzb(String str, Intent intent) {
        synchronized (this.zzclo) {
            zzb com_google_firebase_iid_zzf_zzb = (zzb) this.zzclo.remove(str);
            if (com_google_firebase_iid_zzf_zzb == null) {
                String str2 = "InstanceID/Rpc";
                String str3 = "Missing callback for ";
                String valueOf = String.valueOf(str);
                Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
                return;
            }
            com_google_firebase_iid_zzf_zzb.zzK(intent);
        }
    }

    public static String zzbA(Context context) {
        ApplicationInfo applicationInfo;
        if (zzbhW != null) {
            return zzbhW;
        }
        zzbhX = Process.myUid();
        PackageManager packageManager = context.getPackageManager();
        for (ResolveInfo resolveInfo : packageManager.queryIntentServices(new Intent("com.google.android.c2dm.intent.REGISTER"), 0)) {
            if (packageManager.checkPermission("com.google.android.c2dm.permission.RECEIVE", resolveInfo.serviceInfo.packageName) == 0) {
                try {
                    ApplicationInfo applicationInfo2 = packageManager.getApplicationInfo(resolveInfo.serviceInfo.packageName, 0);
                    Log.w("InstanceID/Rpc", "Found " + applicationInfo2.uid);
                    zzbhY = applicationInfo2.uid;
                    zzbhW = resolveInfo.serviceInfo.packageName;
                    return zzbhW;
                } catch (NameNotFoundException e) {
                }
            } else {
                String valueOf = String.valueOf(resolveInfo.serviceInfo.packageName);
                String valueOf2 = String.valueOf("com.google.android.c2dm.intent.REGISTER");
                Log.w("InstanceID/Rpc", new StringBuilder((String.valueOf(valueOf).length() + 56) + String.valueOf(valueOf2).length()).append("Possible malicious package ").append(valueOf).append(" declares ").append(valueOf2).append(" without permission").toString());
            }
        }
        Log.w("InstanceID/Rpc", "Failed to resolve REGISTER intent, falling back");
        try {
            applicationInfo = packageManager.getApplicationInfo("com.google.android.gms", 0);
            zzbhW = applicationInfo.packageName;
            zzbhY = applicationInfo.uid;
            return zzbhW;
        } catch (NameNotFoundException e2) {
            try {
                applicationInfo = packageManager.getApplicationInfo("com.google.android.gsf", 0);
                zzbhW = applicationInfo.packageName;
                zzbhY = applicationInfo.uid;
                return zzbhW;
            } catch (NameNotFoundException e3) {
                Log.w("InstanceID/Rpc", "Both Google Play Services and legacy GSF package are missing");
                return null;
            }
        }
    }

    private void zzeF(String str) {
        if ("com.google.android.gsf".equals(zzbhW)) {
            this.zzbif++;
            if (this.zzbif >= 3) {
                if (this.zzbif == 3) {
                    this.zzbig = new Random().nextInt(1000) + 1000;
                }
                this.zzbig *= 2;
                this.zzbih = SystemClock.elapsedRealtime() + ((long) this.zzbig);
                Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(str).length() + 31).append("Backoff due to ").append(str).append(" for ").append(this.zzbig).toString());
            }
        }
    }

    void zzHl() {
        if (this.zzbgJ == null) {
            zzbA(this.zzqn);
            this.zzbgJ = new Messenger(new Handler(this, Looper.getMainLooper()) {
                final /* synthetic */ zzf zzclp;

                public void handleMessage(Message message) {
                    this.zzclp.zze(message);
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
        if (this.zzbih == 0 || elapsedRealtime > this.zzbih) {
            zzHl();
            if (zzbhW == null) {
                throw new IOException(InstanceID.ERROR_MISSING_INSTANCEID_SERVICE);
            }
            this.zzbid = SystemClock.elapsedRealtime();
            Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
            intent.setPackage(zzbhW);
            bundle.putString("gmsv", Integer.toString(FirebaseInstanceId.zzQ(this.zzqn, zzbA(this.zzqn))));
            bundle.putString("osv", Integer.toString(VERSION.SDK_INT));
            bundle.putString("app_ver", Integer.toString(FirebaseInstanceId.zzcs(this.zzqn)));
            bundle.putString("app_ver_name", FirebaseInstanceId.zzby(this.zzqn));
            bundle.putString("cliv", "fiid-10298000");
            bundle.putString("appid", FirebaseInstanceId.zza(keyPair));
            String zzcr = FirebaseInstanceId.zzcr(this.zzqn);
            if (zzcr != null) {
                bundle.putString("gmp_app_id", zzcr);
            }
            bundle.putString("pub2", FirebaseInstanceId.zzv(keyPair.getPublic().getEncoded()));
            bundle.putString("sig", zza(keyPair, this.zzqn.getPackageName(), zzcr));
            intent.putExtras(bundle);
            zzs(intent);
            zzb(intent, str);
            return;
        }
        elapsedRealtime = this.zzbih - elapsedRealtime;
        Log.w("InstanceID/Rpc", "Backoff mode, next request attempt: " + elapsedRealtime + " interval: " + this.zzbig);
        throw new IOException(InstanceID.ERROR_BACKOFF);
    }

    protected void zzb(Intent intent, String str) {
        this.zzbid = SystemClock.elapsedRealtime();
        intent.putExtra("kid", new StringBuilder(String.valueOf(str).length() + 5).append("|ID|").append(str).append("|").toString());
        intent.putExtra("X-kid", new StringBuilder(String.valueOf(str).length() + 5).append("|ID|").append(str).append("|").toString());
        boolean equals = "com.google.android.gsf".equals(zzbhW);
        if (Log.isLoggable("InstanceID/Rpc", 3)) {
            String valueOf = String.valueOf(intent.getExtras());
            Log.d("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 8).append("Sending ").append(valueOf).toString());
        }
        if (equals) {
            this.zzqn.startService(intent);
            return;
        }
        intent.putExtra("google.messenger", this.zzbgJ);
        if (!(this.zzbib == null && this.zzbic == null)) {
            Message obtain = Message.obtain();
            obtain.obj = intent;
            try {
                if (this.zzbib != null) {
                    this.zzbib.send(obtain);
                    return;
                } else {
                    this.zzbic.send(obtain);
                    return;
                }
            } catch (RemoteException e) {
                if (Log.isLoggable("InstanceID/Rpc", 3)) {
                    Log.d("InstanceID/Rpc", "Messenger failed, fallback to startService");
                }
            }
        }
        this.zzqn.startService(intent);
    }

    public void zze(Message message) {
        if (message != null) {
            if (message.obj instanceof Intent) {
                Intent intent = (Intent) message.obj;
                intent.setExtrasClassLoader(MessengerCompat.class.getClassLoader());
                if (intent.hasExtra("google.messenger")) {
                    Parcelable parcelableExtra = intent.getParcelableExtra("google.messenger");
                    if (parcelableExtra instanceof MessengerCompat) {
                        this.zzbic = (MessengerCompat) parcelableExtra;
                    }
                    if (parcelableExtra instanceof Messenger) {
                        this.zzbib = (Messenger) parcelableExtra;
                    }
                }
                zzv((Intent) message.obj);
                return;
            }
            Log.w("InstanceID/Rpc", "Dropping invalid message");
        }
    }

    synchronized void zzs(Intent intent) {
        if (this.zzbgF == null) {
            Intent intent2 = new Intent();
            intent2.setPackage("com.google.example.invalidpackage");
            this.zzbgF = PendingIntent.getBroadcast(this.zzqn, 0, intent2, 0);
        }
        intent.putExtra("app", this.zzbgF);
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
        String valueOf2;
        if (Log.isLoggable("InstanceID/Rpc", 3)) {
            valueOf = "InstanceID/Rpc";
            String str = "Received InstanceID error ";
            valueOf2 = String.valueOf(stringExtra);
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
        zzay(valueOf2, valueOf);
        long longExtra = intent.getLongExtra("Retry-After", 0);
        if (longExtra > 0) {
            this.zzbie = SystemClock.elapsedRealtime();
            this.zzbig = ((int) longExtra) * 1000;
            this.zzbih = SystemClock.elapsedRealtime() + ((long) this.zzbig);
            Log.w("InstanceID/Rpc", "Explicit request from server to backoff: " + this.zzbig);
        } else if ("SERVICE_NOT_AVAILABLE".equals(valueOf) || "AUTHENTICATION_FAILED".equals(valueOf)) {
            zzeF(valueOf);
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
                this.zzbid = SystemClock.elapsedRealtime();
                this.zzbih = 0;
                this.zzbif = 0;
                this.zzbig = 0;
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
                            FirebaseInstanceId.zzbz(this.zzqn);
                        } else if ("RST".equals(split[3])) {
                            FirebaseInstanceId.zza(this.zzqn, zzd.zzb(this.zzqn, null).zzabP());
                            intent.removeExtra("registration_id");
                            zzb(str, intent);
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
                    zzb(stringExtra, intent);
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
