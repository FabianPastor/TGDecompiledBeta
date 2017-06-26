package com.google.android.gms.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import java.math.BigInteger;
import java.util.Locale;

final class zzcfv extends zzchi {
    static final Pair<String, Long> zzbri = new Pair("", Long.valueOf(0));
    private SharedPreferences zzaix;
    public final zzcfy zzbrA = new zzcfy(this, "last_pause_time", 0);
    public final zzcfy zzbrB = new zzcfy(this, "time_active", 0);
    public boolean zzbrC;
    public final zzcfz zzbrj = new zzcfz(this, "health_monitor", zzcel.zzxK());
    public final zzcfy zzbrk = new zzcfy(this, "last_upload", 0);
    public final zzcfy zzbrl = new zzcfy(this, "last_upload_attempt", 0);
    public final zzcfy zzbrm = new zzcfy(this, "backoff", 0);
    public final zzcfy zzbrn = new zzcfy(this, "last_delete_stale", 0);
    public final zzcfy zzbro = new zzcfy(this, "midnight_offset", 0);
    public final zzcfy zzbrp = new zzcfy(this, "first_open_time", 0);
    public final zzcga zzbrq = new zzcga(this, "app_instance_id", null);
    private String zzbrr;
    private boolean zzbrs;
    private long zzbrt;
    private String zzbru;
    private long zzbrv;
    private final Object zzbrw = new Object();
    public final zzcfy zzbrx = new zzcfy(this, "time_before_start", 10000);
    public final zzcfy zzbry = new zzcfy(this, "session_timeout", 1800000);
    public final zzcfx zzbrz = new zzcfx(this, "start_new_session", true);

    zzcfv(zzcgk com_google_android_gms_internal_zzcgk) {
        super(com_google_android_gms_internal_zzcgk);
    }

    @WorkerThread
    private final SharedPreferences zzyF() {
        zzjC();
        zzkD();
        return this.zzaix;
    }

    @WorkerThread
    final void setMeasurementEnabled(boolean z) {
        zzjC();
        zzwF().zzyD().zzj("Setting measurementEnabled", Boolean.valueOf(z));
        Editor edit = zzyF().edit();
        edit.putBoolean("measurement_enabled", z);
        edit.apply();
    }

    @WorkerThread
    final void zzak(boolean z) {
        zzjC();
        zzwF().zzyD().zzj("Setting useService", Boolean.valueOf(z));
        Editor edit = zzyF().edit();
        edit.putBoolean("use_service", z);
        edit.apply();
    }

    @WorkerThread
    final boolean zzal(boolean z) {
        zzjC();
        return zzyF().getBoolean("measurement_enabled", z);
    }

    @WorkerThread
    @NonNull
    final Pair<String, Boolean> zzeb(String str) {
        zzjC();
        long elapsedRealtime = zzkq().elapsedRealtime();
        if (this.zzbrr != null && elapsedRealtime < this.zzbrt) {
            return new Pair(this.zzbrr, Boolean.valueOf(this.zzbrs));
        }
        this.zzbrt = elapsedRealtime + zzwH().zza(str, zzcfa.zzbpW);
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(true);
        try {
            Info advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
            if (advertisingIdInfo != null) {
                this.zzbrr = advertisingIdInfo.getId();
                this.zzbrs = advertisingIdInfo.isLimitAdTrackingEnabled();
            }
            if (this.zzbrr == null) {
                this.zzbrr = "";
            }
        } catch (Throwable th) {
            zzwF().zzyC().zzj("Unable to get advertising id", th);
            this.zzbrr = "";
        }
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(false);
        return new Pair(this.zzbrr, Boolean.valueOf(this.zzbrs));
    }

    @WorkerThread
    final String zzec(String str) {
        zzjC();
        String str2 = (String) zzeb(str).first;
        if (zzcjk.zzbE("MD5") == null) {
            return null;
        }
        return String.format(Locale.US, "%032X", new Object[]{new BigInteger(1, zzcjk.zzbE("MD5").digest(str2.getBytes()))});
    }

    @WorkerThread
    final void zzed(String str) {
        zzjC();
        Editor edit = zzyF().edit();
        edit.putString("gmp_app_id", str);
        edit.apply();
    }

    final void zzee(String str) {
        synchronized (this.zzbrw) {
            this.zzbru = str;
            this.zzbrv = zzkq().elapsedRealtime();
        }
    }

    protected final void zzjD() {
        this.zzaix = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
        this.zzbrC = this.zzaix.getBoolean("has_been_opened", false);
        if (!this.zzbrC) {
            Editor edit = this.zzaix.edit();
            edit.putBoolean("has_been_opened", true);
            edit.apply();
        }
    }

    @WorkerThread
    final String zzyG() {
        zzjC();
        return zzyF().getString("gmp_app_id", null);
    }

    final String zzyH() {
        String str;
        synchronized (this.zzbrw) {
            if (Math.abs(zzkq().elapsedRealtime() - this.zzbrv) < 1000) {
                str = this.zzbru;
            } else {
                str = null;
            }
        }
        return str;
    }

    @WorkerThread
    final Boolean zzyI() {
        zzjC();
        return !zzyF().contains("use_service") ? null : Boolean.valueOf(zzyF().getBoolean("use_service", false));
    }

    @WorkerThread
    final void zzyJ() {
        boolean z = true;
        zzjC();
        zzwF().zzyD().log("Clearing collection preferences.");
        boolean contains = zzyF().contains("measurement_enabled");
        if (contains) {
            z = zzal(true);
        }
        Editor edit = zzyF().edit();
        edit.clear();
        edit.apply();
        if (contains) {
            setMeasurementEnabled(z);
        }
    }

    @WorkerThread
    protected final String zzyK() {
        zzjC();
        String string = zzyF().getString("previous_os_version", null);
        zzwv().zzkD();
        String str = VERSION.RELEASE;
        if (!(TextUtils.isEmpty(str) || str.equals(string))) {
            Editor edit = zzyF().edit();
            edit.putString("previous_os_version", str);
            edit.apply();
        }
        return string;
    }
}
