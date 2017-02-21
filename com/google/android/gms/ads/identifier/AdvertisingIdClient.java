package com.google.android.gms.ads.identifier;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.zzc;
import com.google.android.gms.common.zze;
import com.google.android.gms.internal.zzcn;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AdvertisingIdClient {
    private final Context mContext;
    com.google.android.gms.common.zza zzse;
    zzcn zzsf;
    boolean zzsg;
    Object zzsh;
    zza zzsi;
    final long zzsj;

    public static final class Info {
        private final String zzsp;
        private final boolean zzsq;

        public Info(String str, boolean z) {
            this.zzsp = str;
            this.zzsq = z;
        }

        public String getId() {
            return this.zzsp;
        }

        public boolean isLimitAdTrackingEnabled() {
            return this.zzsq;
        }

        public String toString() {
            String str = this.zzsp;
            return new StringBuilder(String.valueOf(str).length() + 7).append("{").append(str).append("}").append(this.zzsq).toString();
        }
    }

    static class zza extends Thread {
        private WeakReference<AdvertisingIdClient> zzsl;
        private long zzsm;
        CountDownLatch zzsn = new CountDownLatch(1);
        boolean zzso = false;

        public zza(AdvertisingIdClient advertisingIdClient, long j) {
            this.zzsl = new WeakReference(advertisingIdClient);
            this.zzsm = j;
            start();
        }

        private void disconnect() {
            AdvertisingIdClient advertisingIdClient = (AdvertisingIdClient) this.zzsl.get();
            if (advertisingIdClient != null) {
                advertisingIdClient.finish();
                this.zzso = true;
            }
        }

        public void cancel() {
            this.zzsn.countDown();
        }

        public void run() {
            try {
                if (!this.zzsn.await(this.zzsm, TimeUnit.MILLISECONDS)) {
                    disconnect();
                }
            } catch (InterruptedException e) {
                disconnect();
            }
        }

        public boolean zzby() {
            return this.zzso;
        }
    }

    public AdvertisingIdClient(Context context) {
        this(context, 30000, false);
    }

    public AdvertisingIdClient(Context context, long j, boolean z) {
        this.zzsh = new Object();
        zzac.zzw(context);
        if (z) {
            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                context = applicationContext;
            }
            this.mContext = context;
        } else {
            this.mContext = context;
        }
        this.zzsg = false;
        this.zzsj = j;
    }

    public static Info getAdvertisingIdInfo(Context context) throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        float f = 0.0f;
        boolean z = false;
        try {
            Context remoteContext = zze.getRemoteContext(context);
            if (remoteContext != null) {
                SharedPreferences sharedPreferences = remoteContext.getSharedPreferences("google_ads_flags", 1);
                z = sharedPreferences.getBoolean("gads:ad_id_app_context:enabled", false);
                f = sharedPreferences.getFloat("gads:ad_id_app_context:ping_ratio", 0.0f);
            }
        } catch (Throwable e) {
            Log.w("AdvertisingIdClient", "Error while reading from SharedPreferences ", e);
        }
        AdvertisingIdClient advertisingIdClient = new AdvertisingIdClient(context, -1, z);
        Info info;
        try {
            advertisingIdClient.zze(false);
            info = advertisingIdClient.getInfo();
            advertisingIdClient.zza(info, z, f, null);
            return info;
        } catch (Throwable th) {
            info = th;
            advertisingIdClient.zza(null, z, f, info);
            return null;
        } finally {
            advertisingIdClient.finish();
        }
    }

    public static void setShouldSkipGmsCoreVersionCheck(boolean z) {
    }

    static zzcn zza(Context context, com.google.android.gms.common.zza com_google_android_gms_common_zza) throws IOException {
        try {
            return com.google.android.gms.internal.zzcn.zza.zzf(com_google_android_gms_common_zza.zza(10000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            throw new IOException("Interrupted exception");
        } catch (Throwable th) {
            IOException iOException = new IOException(th);
        }
    }

    private void zza(Info info, boolean z, float f, Throwable th) {
        if (Math.random() <= ((double) f)) {
            final String uri = zza(info, z, th).toString();
            new Thread(this) {
                public void run() {
                    new zza().zzu(uri);
                }
            }.start();
        }
    }

    private void zzbx() {
        synchronized (this.zzsh) {
            if (this.zzsi != null) {
                this.zzsi.cancel();
                try {
                    this.zzsi.join();
                } catch (InterruptedException e) {
                }
            }
            if (this.zzsj > 0) {
                this.zzsi = new zza(this, this.zzsj);
            }
        }
    }

    static com.google.android.gms.common.zza zzf(Context context) throws IOException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        try {
            context.getPackageManager().getPackageInfo("com.android.vending", 0);
            switch (zzc.zzuz().isGooglePlayServicesAvailable(context)) {
                case 0:
                case 2:
                    ServiceConnection com_google_android_gms_common_zza = new com.google.android.gms.common.zza();
                    Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
                    intent.setPackage("com.google.android.gms");
                    try {
                        if (com.google.android.gms.common.stats.zza.zzyc().zza(context, intent, com_google_android_gms_common_zza, 1)) {
                            return com_google_android_gms_common_zza;
                        }
                        throw new IOException("Connection failure");
                    } catch (Throwable th) {
                        IOException iOException = new IOException(th);
                    }
                default:
                    throw new IOException("Google Play services not available");
            }
        } catch (NameNotFoundException e) {
            throw new GooglePlayServicesNotAvailableException(9);
        }
    }

    protected void finalize() throws Throwable {
        finish();
        super.finalize();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void finish() {
        zzac.zzdo("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            if (this.mContext == null || this.zzse == null) {
            } else {
                try {
                    if (this.zzsg) {
                        com.google.android.gms.common.stats.zza.zzyc().zza(this.mContext, this.zzse);
                    }
                } catch (Throwable e) {
                    Log.i("AdvertisingIdClient", "AdvertisingIdClient unbindService failed.", e);
                } catch (Throwable e2) {
                    Log.i("AdvertisingIdClient", "AdvertisingIdClient unbindService failed.", e2);
                }
                this.zzsg = false;
                this.zzsf = null;
                this.zzse = null;
            }
        }
    }

    public Info getInfo() throws IOException {
        Info info;
        zzac.zzdo("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            if (!this.zzsg) {
                synchronized (this.zzsh) {
                    if (this.zzsi == null || !this.zzsi.zzby()) {
                        throw new IOException("AdvertisingIdClient is not connected.");
                    }
                }
                try {
                    zze(false);
                    if (!this.zzsg) {
                        throw new IOException("AdvertisingIdClient cannot reconnect.");
                    }
                } catch (Throwable e) {
                    Log.i("AdvertisingIdClient", "GMS remote exception ", e);
                    throw new IOException("Remote exception");
                } catch (Throwable e2) {
                    throw new IOException("AdvertisingIdClient cannot reconnect.", e2);
                }
            }
            zzac.zzw(this.zzse);
            zzac.zzw(this.zzsf);
            info = new Info(this.zzsf.getId(), this.zzsf.zzf(true));
        }
        zzbx();
        return info;
    }

    public void start() throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        zze(true);
    }

    Uri zza(Info info, boolean z, Throwable th) {
        Bundle bundle = new Bundle();
        bundle.putString("app_context", z ? "1" : "0");
        if (info != null) {
            bundle.putString("limit_ad_tracking", info.isLimitAdTrackingEnabled() ? "1" : "0");
        }
        if (!(info == null || info.getId() == null)) {
            bundle.putString("ad_id_size", Integer.toString(info.getId().length()));
        }
        if (th != null) {
            bundle.putString("error", th.getClass().getName());
        }
        Builder buildUpon = Uri.parse("https://pagead2.googlesyndication.com/pagead/gen_204?id=gmob-apps").buildUpon();
        for (String str : bundle.keySet()) {
            buildUpon.appendQueryParameter(str, bundle.getString(str));
        }
        return buildUpon.build();
    }

    protected void zze(boolean z) throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        zzac.zzdo("Calling this from your main thread can lead to deadlock");
        synchronized (this) {
            if (this.zzsg) {
                finish();
            }
            this.zzse = zzf(this.mContext);
            this.zzsf = zza(this.mContext, this.zzse);
            this.zzsg = true;
            if (z) {
                zzbx();
            }
        }
    }
}
