package com.google.android.gms.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import java.math.BigInteger;
import java.util.Locale;

final class zzchx extends zzcjl {
    static final Pair<String, Long> zzjcp = new Pair(TtmlNode.ANONYMOUS_REGION_ID, Long.valueOf(0));
    private SharedPreferences zzdyn;
    public final zzcib zzjcq = new zzcib(this, "health_monitor", Math.max(0, ((Long) zzchc.zzjaf.get()).longValue()));
    public final zzcia zzjcr = new zzcia(this, "last_upload", 0);
    public final zzcia zzjcs = new zzcia(this, "last_upload_attempt", 0);
    public final zzcia zzjct = new zzcia(this, "backoff", 0);
    public final zzcia zzjcu = new zzcia(this, "last_delete_stale", 0);
    public final zzcia zzjcv = new zzcia(this, "midnight_offset", 0);
    public final zzcia zzjcw = new zzcia(this, "first_open_time", 0);
    public final zzcic zzjcx = new zzcic(this, "app_instance_id", null);
    private String zzjcy;
    private boolean zzjcz;
    private long zzjda;
    private String zzjdb;
    private long zzjdc;
    private final Object zzjdd = new Object();
    public final zzcia zzjde = new zzcia(this, "time_before_start", 10000);
    public final zzcia zzjdf = new zzcia(this, "session_timeout", 1800000);
    public final zzchz zzjdg = new zzchz(this, "start_new_session", true);
    public final zzcia zzjdh = new zzcia(this, "last_pause_time", 0);
    public final zzcia zzjdi = new zzcia(this, "time_active", 0);
    public boolean zzjdj;

    zzchx(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
    }

    private final SharedPreferences zzazl() {
        zzve();
        zzxf();
        return this.zzdyn;
    }

    final void setMeasurementEnabled(boolean z) {
        zzve();
        zzawy().zzazj().zzj("Setting measurementEnabled", Boolean.valueOf(z));
        Editor edit = zzazl().edit();
        edit.putBoolean("measurement_enabled", z);
        edit.apply();
    }

    protected final boolean zzaxz() {
        return true;
    }

    protected final void zzayy() {
        this.zzdyn = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
        this.zzjdj = this.zzdyn.getBoolean("has_been_opened", false);
        if (!this.zzjdj) {
            Editor edit = this.zzdyn.edit();
            edit.putBoolean("has_been_opened", true);
            edit.apply();
        }
    }

    final String zzazm() {
        zzve();
        return zzazl().getString("gmp_app_id", null);
    }

    final String zzazn() {
        String str;
        synchronized (this.zzjdd) {
            if (Math.abs(zzws().elapsedRealtime() - this.zzjdc) < 1000) {
                str = this.zzjdb;
            } else {
                str = null;
            }
        }
        return str;
    }

    final Boolean zzazo() {
        zzve();
        return !zzazl().contains("use_service") ? null : Boolean.valueOf(zzazl().getBoolean("use_service", false));
    }

    final void zzazp() {
        boolean z = true;
        zzve();
        zzawy().zzazj().log("Clearing collection preferences.");
        boolean contains = zzazl().contains("measurement_enabled");
        if (contains) {
            z = zzbn(true);
        }
        Editor edit = zzazl().edit();
        edit.clear();
        edit.apply();
        if (contains) {
            setMeasurementEnabled(z);
        }
    }

    protected final String zzazq() {
        zzve();
        String string = zzazl().getString("previous_os_version", null);
        zzawo().zzxf();
        String str = VERSION.RELEASE;
        if (!(TextUtils.isEmpty(str) || str.equals(string))) {
            Editor edit = zzazl().edit();
            edit.putString("previous_os_version", str);
            edit.apply();
        }
        return string;
    }

    final void zzbm(boolean z) {
        zzve();
        zzawy().zzazj().zzj("Setting useService", Boolean.valueOf(z));
        Editor edit = zzazl().edit();
        edit.putBoolean("use_service", z);
        edit.apply();
    }

    final boolean zzbn(boolean z) {
        zzve();
        return zzazl().getBoolean("measurement_enabled", z);
    }

    final Pair<String, Boolean> zzjm(String str) {
        zzve();
        long elapsedRealtime = zzws().elapsedRealtime();
        if (this.zzjcy != null && elapsedRealtime < this.zzjda) {
            return new Pair(this.zzjcy, Boolean.valueOf(this.zzjcz));
        }
        this.zzjda = elapsedRealtime + zzaxa().zza(str, zzchc.zzjae);
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(true);
        try {
            Info advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
            if (advertisingIdInfo != null) {
                this.zzjcy = advertisingIdInfo.getId();
                this.zzjcz = advertisingIdInfo.isLimitAdTrackingEnabled();
            }
            if (this.zzjcy == null) {
                this.zzjcy = TtmlNode.ANONYMOUS_REGION_ID;
            }
        } catch (Throwable th) {
            zzawy().zzazi().zzj("Unable to get advertising id", th);
            this.zzjcy = TtmlNode.ANONYMOUS_REGION_ID;
        }
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(false);
        return new Pair(this.zzjcy, Boolean.valueOf(this.zzjcz));
    }

    final String zzjn(String str) {
        zzve();
        String str2 = (String) zzjm(str).first;
        if (zzclq.zzek("MD5") == null) {
            return null;
        }
        return String.format(Locale.US, "%032X", new Object[]{new BigInteger(1, zzclq.zzek("MD5").digest(str2.getBytes()))});
    }

    final void zzjo(String str) {
        zzve();
        Editor edit = zzazl().edit();
        edit.putString("gmp_app_id", str);
        edit.apply();
    }

    final void zzjp(String str) {
        synchronized (this.zzjdd) {
            this.zzjdb = str;
            this.zzjdc = zzws().elapsedRealtime();
        }
    }
}
