package com.google.android.gms.gcm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.RequiresPermission;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.zze;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GoogleCloudMessaging {
    public static final String ERROR_MAIN_THREAD = "MAIN_THREAD";
    public static final String ERROR_SERVICE_NOT_AVAILABLE = "SERVICE_NOT_AVAILABLE";
    public static final String INSTANCE_ID_SCOPE = "GCM";
    @Deprecated
    public static final String MESSAGE_TYPE_DELETED = "deleted_messages";
    @Deprecated
    public static final String MESSAGE_TYPE_MESSAGE = "gcm";
    @Deprecated
    public static final String MESSAGE_TYPE_SEND_ERROR = "send_error";
    @Deprecated
    public static final String MESSAGE_TYPE_SEND_EVENT = "send_event";
    public static int zzbfL = 5000000;
    private static int zzbfM = 6500000;
    private static int zzbfN = 7000000;
    private static GoogleCloudMessaging zzbfO;
    private static final AtomicInteger zzbfR = new AtomicInteger(1);
    private PendingIntent zzbfP;
    private Map<String, Handler> zzbfQ = Collections.synchronizedMap(new HashMap());
    private final BlockingQueue<Intent> zzbfS = new LinkedBlockingQueue();
    private Messenger zzbfT = new Messenger(new zzc(this, Looper.getMainLooper()));
    private Context zzqD;

    public static synchronized GoogleCloudMessaging getInstance(Context context) {
        GoogleCloudMessaging googleCloudMessaging;
        synchronized (GoogleCloudMessaging.class) {
            if (zzbfO == null) {
                googleCloudMessaging = new GoogleCloudMessaging();
                zzbfO = googleCloudMessaging;
                googleCloudMessaging.zzqD = context.getApplicationContext();
            }
            googleCloudMessaging = zzbfO;
        }
        return googleCloudMessaging;
    }

    @Deprecated
    private final Intent zza(Bundle bundle, boolean z) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        } else if (zzaZ(this.zzqD) < 0) {
            throw new IOException("Google Play Services missing");
        } else {
            Intent intent = new Intent(z ? "com.google.iid.TOKEN_REQUEST" : "com.google.android.c2dm.intent.REGISTER");
            intent.setPackage(zze.zzbd(this.zzqD));
            zzf(intent);
            String valueOf = String.valueOf("google.rpc");
            String valueOf2 = String.valueOf(String.valueOf(zzbfR.getAndIncrement()));
            intent.putExtra("google.message_id", valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
            intent.putExtras(bundle);
            intent.putExtra("google.messenger", this.zzbfT);
            if (z) {
                this.zzqD.sendBroadcast(intent);
            } else {
                this.zzqD.startService(intent);
            }
            try {
                return (Intent) this.zzbfS.poll(30000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new IOException(e.getMessage());
            }
        }
    }

    @Deprecated
    private final synchronized String zza(boolean z, String... strArr) throws IOException {
        String zzbd;
        zzbd = zze.zzbd(this.zzqD);
        if (zzbd == null) {
            throw new IOException("SERVICE_NOT_AVAILABLE");
        }
        String zzc = zzc(strArr);
        Bundle bundle = new Bundle();
        if (zzbd.contains(".gsf")) {
            bundle.putString("legacy.sender", zzc);
            zzbd = InstanceID.getInstance(this.zzqD).getToken(zzc, INSTANCE_ID_SCOPE, bundle);
        } else {
            bundle.putString("sender", zzc);
            Intent zza = zza(bundle, z);
            zzbd = "registration_id";
            if (zza == null) {
                throw new IOException("SERVICE_NOT_AVAILABLE");
            }
            zzbd = zza.getStringExtra(zzbd);
            if (zzbd == null) {
                zzbd = zza.getStringExtra("error");
                if (zzbd != null) {
                    throw new IOException(zzbd);
                }
                throw new IOException("SERVICE_NOT_AVAILABLE");
            }
        }
        return zzbd;
    }

    public static int zzaZ(Context context) {
        String zzbd = zze.zzbd(context);
        if (zzbd != null) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(zzbd, 0);
                if (packageInfo != null) {
                    return packageInfo.versionCode;
                }
            } catch (NameNotFoundException e) {
            }
        }
        return -1;
    }

    private static String zzc(String... strArr) {
        if (strArr == null || strArr.length == 0) {
            throw new IllegalArgumentException("No senderIds");
        }
        StringBuilder stringBuilder = new StringBuilder(strArr[0]);
        for (int i = 1; i < strArr.length; i++) {
            stringBuilder.append(',').append(strArr[i]);
        }
        return stringBuilder.toString();
    }

    private final boolean zze(Intent intent) {
        Object stringExtra = intent.getStringExtra("In-Reply-To");
        if (stringExtra == null && intent.hasExtra("error")) {
            stringExtra = intent.getStringExtra("google.message_id");
        }
        if (stringExtra != null) {
            Handler handler = (Handler) this.zzbfQ.remove(stringExtra);
            if (handler != null) {
                Message obtain = Message.obtain();
                obtain.obj = intent;
                return handler.sendMessage(obtain);
            }
        }
        return false;
    }

    private final synchronized void zzf(Intent intent) {
        if (this.zzbfP == null) {
            Intent intent2 = new Intent();
            intent2.setPackage("com.google.example.invalidpackage");
            this.zzbfP = PendingIntent.getBroadcast(this.zzqD, 0, intent2, 0);
        }
        intent.putExtra("app", this.zzbfP);
    }

    private final synchronized void zzvD() {
        if (this.zzbfP != null) {
            this.zzbfP.cancel();
            this.zzbfP = null;
        }
    }

    public void close() {
        zzbfO = null;
        zza.zzbfw = null;
        zzvD();
    }

    public String getMessageType(Intent intent) {
        if (!"com.google.android.c2dm.intent.RECEIVE".equals(intent.getAction())) {
            return null;
        }
        String stringExtra = intent.getStringExtra("message_type");
        return stringExtra == null ? MESSAGE_TYPE_MESSAGE : stringExtra;
    }

    @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
    @Deprecated
    public synchronized String register(String... strArr) throws IOException {
        return zza(zze.zzbc(this.zzqD), strArr);
    }

    @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
    public void send(String str, String str2, long j, Bundle bundle) throws IOException {
        if (str == null) {
            throw new IllegalArgumentException("Missing 'to'");
        }
        String zzbd = zze.zzbd(this.zzqD);
        if (zzbd == null) {
            throw new IOException("SERVICE_NOT_AVAILABLE");
        }
        Intent intent = new Intent("com.google.android.gcm.intent.SEND");
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        zzf(intent);
        intent.setPackage(zzbd);
        intent.putExtra("google.to", str);
        intent.putExtra("google.message_id", str2);
        intent.putExtra("google.ttl", Long.toString(j));
        intent.putExtra("google.delay", Integer.toString(-1));
        String str3 = "google.from";
        int indexOf = str.indexOf(64);
        String substring = indexOf > 0 ? str.substring(0, indexOf) : str;
        InstanceID.getInstance(this.zzqD);
        intent.putExtra(str3, InstanceID.zzvM().zzf("", substring, INSTANCE_ID_SCOPE));
        if (zzbd.contains(".gsf")) {
            Bundle bundle2 = new Bundle();
            for (String substring2 : bundle.keySet()) {
                Object obj = bundle.get(substring2);
                if (obj instanceof String) {
                    String str4 = "gcm.";
                    substring2 = String.valueOf(substring2);
                    bundle2.putString(substring2.length() != 0 ? str4.concat(substring2) : new String(str4), (String) obj);
                }
            }
            bundle2.putString("google.to", str);
            bundle2.putString("google.message_id", str2);
            InstanceID.getInstance(this.zzqD).zzc(INSTANCE_ID_SCOPE, "upstream", bundle2);
            return;
        }
        this.zzqD.sendOrderedBroadcast(intent, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
    }

    @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
    public void send(String str, String str2, Bundle bundle) throws IOException {
        send(str, str2, -1, bundle);
    }

    @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
    @Deprecated
    public synchronized void unregister() throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        InstanceID.getInstance(this.zzqD).deleteInstanceID();
    }
}
