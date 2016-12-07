package com.google.android.gms.gcm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.zzc;
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
    public static int agY = 5000000;
    public static int agZ = 6500000;
    public static int aha = 7000000;
    static GoogleCloudMessaging ahb;
    private static final AtomicInteger ahe = new AtomicInteger(1);
    private PendingIntent ahc;
    private Map<String, Handler> ahd = Collections.synchronizedMap(new HashMap());
    private final BlockingQueue<Intent> ahf = new LinkedBlockingQueue();
    final Messenger ahg = new Messenger(new Handler(this, Looper.getMainLooper()) {
        final /* synthetic */ GoogleCloudMessaging ahh;

        public void handleMessage(Message message) {
            if (message == null || !(message.obj instanceof Intent)) {
                Log.w(GoogleCloudMessaging.INSTANCE_ID_SCOPE, "Dropping invalid message");
            }
            Intent intent = (Intent) message.obj;
            if ("com.google.android.c2dm.intent.REGISTRATION".equals(intent.getAction())) {
                this.ahh.ahf.add(intent);
            } else if (!this.ahh.zzq(intent)) {
                intent.setPackage(this.ahh.zzahs.getPackageName());
                this.ahh.zzahs.sendBroadcast(intent);
            }
        }
    });
    private Context zzahs;

    public static synchronized GoogleCloudMessaging getInstance(Context context) {
        GoogleCloudMessaging googleCloudMessaging;
        synchronized (GoogleCloudMessaging.class) {
            if (ahb == null) {
                ahb = new GoogleCloudMessaging();
                ahb.zzahs = context.getApplicationContext();
            }
            googleCloudMessaging = ahb;
        }
        return googleCloudMessaging;
    }

    static String zza(Intent intent, String str) throws IOException {
        if (intent == null) {
            throw new IOException("SERVICE_NOT_AVAILABLE");
        }
        String stringExtra = intent.getStringExtra(str);
        if (stringExtra != null) {
            return stringExtra;
        }
        stringExtra = intent.getStringExtra("error");
        if (stringExtra != null) {
            throw new IOException(stringExtra);
        }
        throw new IOException("SERVICE_NOT_AVAILABLE");
    }

    private void zza(String str, String str2, long j, int i, Bundle bundle) throws IOException {
        if (str == null) {
            throw new IllegalArgumentException("Missing 'to'");
        }
        String zzdb = zzdb(this.zzahs);
        if (zzdb == null) {
            throw new IOException("SERVICE_NOT_AVAILABLE");
        }
        Intent intent = new Intent("com.google.android.gcm.intent.SEND");
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        zzr(intent);
        intent.setPackage(zzdb);
        intent.putExtra("google.to", str);
        intent.putExtra("google.message_id", str2);
        intent.putExtra("google.ttl", Long.toString(j));
        intent.putExtra("google.delay", Integer.toString(i));
        intent.putExtra("google.from", zzko(str));
        if (zzdb.contains(".gsf")) {
            Bundle bundle2 = new Bundle();
            for (String zzdb2 : bundle.keySet()) {
                Object obj = bundle.get(zzdb2);
                if (obj instanceof String) {
                    String str3 = "gcm.";
                    zzdb2 = String.valueOf(zzdb2);
                    bundle2.putString(zzdb2.length() != 0 ? str3.concat(zzdb2) : new String(str3), (String) obj);
                }
            }
            bundle2.putString("google.to", str);
            bundle2.putString("google.message_id", str2);
            InstanceID.getInstance(this.zzahs).zzc(INSTANCE_ID_SCOPE, "upstream", bundle2);
            return;
        }
        this.zzahs.sendOrderedBroadcast(intent, "com.google.android.gtalkservice.permission.GTALK_SERVICE");
    }

    private String zzbnt() {
        String valueOf = String.valueOf("google.rpc");
        String valueOf2 = String.valueOf(String.valueOf(ahe.getAndIncrement()));
        return valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
    }

    public static String zzdb(Context context) {
        return zzc.zzdg(context);
    }

    public static int zzdc(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String zzdb = zzdb(context);
        if (zzdb != null) {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(zzdb, 0);
                if (packageInfo != null) {
                    return packageInfo.versionCode;
                }
            } catch (NameNotFoundException e) {
            }
        }
        return -1;
    }

    private String zzko(String str) {
        int indexOf = str.indexOf(64);
        if (indexOf > 0) {
            str = str.substring(0, indexOf);
        }
        return InstanceID.getInstance(this.zzahs).zzbok().zzh("", str, INSTANCE_ID_SCOPE);
    }

    private boolean zzq(Intent intent) {
        Object stringExtra = intent.getStringExtra("In-Reply-To");
        if (stringExtra == null && intent.hasExtra("error")) {
            stringExtra = intent.getStringExtra("google.message_id");
        }
        if (stringExtra != null) {
            Handler handler = (Handler) this.ahd.remove(stringExtra);
            if (handler != null) {
                Message obtain = Message.obtain();
                obtain.obj = intent;
                return handler.sendMessage(obtain);
            }
        }
        return false;
    }

    public void close() {
        ahb = null;
        zza.agO = null;
        zzbnu();
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
        String zzdb;
        zzdb = zzdb(this.zzahs);
        if (zzdb == null) {
            throw new IOException("SERVICE_NOT_AVAILABLE");
        }
        String zzf = zzf(strArr);
        Bundle bundle = new Bundle();
        if (zzdb.contains(".gsf")) {
            bundle.putString("legacy.sender", zzf);
            zzdb = InstanceID.getInstance(this.zzahs).getToken(zzf, INSTANCE_ID_SCOPE, bundle);
        } else {
            bundle.putString("sender", zzf);
            zzdb = zza(zzai(bundle), "registration_id");
        }
        return zzdb;
    }

    @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
    public void send(String str, String str2, long j, Bundle bundle) throws IOException {
        zza(str, str2, j, -1, bundle);
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
        InstanceID.getInstance(this.zzahs).deleteInstanceID();
    }

    @Deprecated
    Intent zzai(Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        } else if (zzdc(this.zzahs) < 0) {
            throw new IOException("Google Play Services missing");
        } else {
            if (bundle == null) {
                bundle = new Bundle();
            }
            Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
            intent.setPackage(zzdb(this.zzahs));
            zzr(intent);
            intent.putExtra("google.message_id", zzbnt());
            intent.putExtras(bundle);
            intent.putExtra("google.messenger", this.ahg);
            this.zzahs.startService(intent);
            try {
                return (Intent) this.ahf.poll(30000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new IOException(e.getMessage());
            }
        }
    }

    synchronized void zzbnu() {
        if (this.ahc != null) {
            this.ahc.cancel();
            this.ahc = null;
        }
    }

    String zzf(String... strArr) {
        if (strArr == null || strArr.length == 0) {
            throw new IllegalArgumentException("No senderIds");
        }
        StringBuilder stringBuilder = new StringBuilder(strArr[0]);
        for (int i = 1; i < strArr.length; i++) {
            stringBuilder.append(',').append(strArr[i]);
        }
        return stringBuilder.toString();
    }

    synchronized void zzr(Intent intent) {
        if (this.ahc == null) {
            Intent intent2 = new Intent();
            intent2.setPackage("com.google.example.invalidpackage");
            this.ahc = PendingIntent.getBroadcast(this.zzahs, 0, intent2, 0);
        }
        intent.putExtra("app", this.ahc);
    }
}
