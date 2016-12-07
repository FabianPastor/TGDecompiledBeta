package com.google.android.gms.measurement.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.internal.zzac;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Locale;

class zzt extends zzaa {
    static final Pair<String, Long> apO = new Pair("", Long.valueOf(0));
    public final zzc apP = new zzc("health_monitor", zzbvi().zzadz());
    public final zzb apQ = new zzb(this, "last_upload", 0);
    public final zzb apR = new zzb(this, "last_upload_attempt", 0);
    public final zzb apS = new zzb(this, "backoff", 0);
    public final zzb apT = new zzb(this, "last_delete_stale", 0);
    public final zzb apU = new zzb(this, "midnight_offset", 0);
    private String apV;
    private boolean apW;
    private long apX;
    private SecureRandom apY;
    public final zzb apZ = new zzb(this, "time_before_start", 10000);
    public final zzb aqa = new zzb(this, "session_timeout", 1800000);
    public final zza aqb = new zza(this, "start_new_session", true);
    public final zzb aqc = new zzb(this, "last_pause_time", 0);
    public final zzb aqd = new zzb(this, "time_active", 0);
    public boolean aqe;
    private SharedPreferences dy;

    public final class zza {
        private final boolean aqf;
        private boolean aqg;
        final /* synthetic */ zzt aqh;
        private boolean vu;
        private final String zzbaf;

        public zza(zzt com_google_android_gms_measurement_internal_zzt, String str, boolean z) {
            this.aqh = com_google_android_gms_measurement_internal_zzt;
            zzac.zzhz(str);
            this.zzbaf = str;
            this.aqf = z;
        }

        @WorkerThread
        private void zzbwt() {
            if (!this.aqg) {
                this.aqg = true;
                this.vu = this.aqh.dy.getBoolean(this.zzbaf, this.aqf);
            }
        }

        @WorkerThread
        public boolean get() {
            zzbwt();
            return this.vu;
        }

        @WorkerThread
        public void set(boolean z) {
            Editor edit = this.aqh.dy.edit();
            edit.putBoolean(this.zzbaf, z);
            edit.apply();
            this.vu = z;
        }
    }

    public final class zzb {
        private long X;
        private boolean aqg;
        final /* synthetic */ zzt aqh;
        private final long aqi;
        private final String zzbaf;

        public zzb(zzt com_google_android_gms_measurement_internal_zzt, String str, long j) {
            this.aqh = com_google_android_gms_measurement_internal_zzt;
            zzac.zzhz(str);
            this.zzbaf = str;
            this.aqi = j;
        }

        @WorkerThread
        private void zzbwt() {
            if (!this.aqg) {
                this.aqg = true;
                this.X = this.aqh.dy.getLong(this.zzbaf, this.aqi);
            }
        }

        @WorkerThread
        public long get() {
            zzbwt();
            return this.X;
        }

        @WorkerThread
        public void set(long j) {
            Editor edit = this.aqh.dy.edit();
            edit.putLong(this.zzbaf, j);
            edit.apply();
            this.X = j;
        }
    }

    public final class zzc {
        final /* synthetic */ zzt aqh;
        final String aqj;
        private final String aqk;
        private final String aql;
        private final long dC;

        private zzc(zzt com_google_android_gms_measurement_internal_zzt, String str, long j) {
            this.aqh = com_google_android_gms_measurement_internal_zzt;
            zzac.zzhz(str);
            zzac.zzbs(j > 0);
            this.aqj = String.valueOf(str).concat(":start");
            this.aqk = String.valueOf(str).concat(":count");
            this.aql = String.valueOf(str).concat(":value");
            this.dC = j;
        }

        @WorkerThread
        private void zzafk() {
            this.aqh.zzyl();
            long currentTimeMillis = this.aqh.zzaan().currentTimeMillis();
            Editor edit = this.aqh.dy.edit();
            edit.remove(this.aqk);
            edit.remove(this.aql);
            edit.putLong(this.aqj, currentTimeMillis);
            edit.apply();
        }

        @WorkerThread
        private long zzafl() {
            this.aqh.zzyl();
            long zzafn = zzafn();
            if (zzafn != 0) {
                return Math.abs(zzafn - this.aqh.zzaan().currentTimeMillis());
            }
            zzafk();
            return 0;
        }

        @WorkerThread
        private long zzafn() {
            return this.aqh.zzbwo().getLong(this.aqj, 0);
        }

        @WorkerThread
        public Pair<String, Long> zzafm() {
            this.aqh.zzyl();
            long zzafl = zzafl();
            if (zzafl < this.dC) {
                return null;
            }
            if (zzafl > this.dC * 2) {
                zzafk();
                return null;
            }
            String string = this.aqh.zzbwo().getString(this.aql, null);
            zzafl = this.aqh.zzbwo().getLong(this.aqk, 0);
            zzafk();
            return (string == null || zzafl <= 0) ? zzt.apO : new Pair(string, Long.valueOf(zzafl));
        }

        @WorkerThread
        public void zzfd(String str) {
            zzi(str, 1);
        }

        @WorkerThread
        public void zzi(String str, long j) {
            this.aqh.zzyl();
            if (zzafn() == 0) {
                zzafk();
            }
            if (str == null) {
                str = "";
            }
            long j2 = this.aqh.dy.getLong(this.aqk, 0);
            if (j2 <= 0) {
                Editor edit = this.aqh.dy.edit();
                edit.putString(this.aql, str);
                edit.putLong(this.aqk, j);
                edit.apply();
                return;
            }
            Object obj = (this.aqh.zzbwl().nextLong() & Long.MAX_VALUE) < (Long.MAX_VALUE / (j2 + j)) * j ? 1 : null;
            Editor edit2 = this.aqh.dy.edit();
            if (obj != null) {
                edit2.putString(this.aql, str);
            }
            edit2.putLong(this.aqk, j2 + j);
            edit2.apply();
        }
    }

    zzt(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    @WorkerThread
    private SecureRandom zzbwl() {
        zzyl();
        if (this.apY == null) {
            this.apY = new SecureRandom();
        }
        return this.apY;
    }

    @WorkerThread
    private SharedPreferences zzbwo() {
        zzyl();
        zzaax();
        return this.dy;
    }

    @WorkerThread
    void setMeasurementEnabled(boolean z) {
        zzyl();
        zzbvg().zzbwj().zzj("Setting measurementEnabled", Boolean.valueOf(z));
        Editor edit = zzbwo().edit();
        edit.putBoolean("measurement_enabled", z);
        edit.apply();
    }

    @WorkerThread
    String zzbst() {
        zzyl();
        try {
            return com.google.firebase.iid.zzc.A().getId();
        } catch (IllegalStateException e) {
            zzbvg().zzbwe().log("Failed to retrieve Firebase Instance Id");
            return null;
        }
    }

    @WorkerThread
    String zzbwm() {
        zzbwl().nextBytes(new byte[16]);
        return String.format(Locale.US, "%032x", new Object[]{new BigInteger(1, r0)});
    }

    @WorkerThread
    long zzbwn() {
        zzaax();
        zzyl();
        long j = this.apU.get();
        if (j != 0) {
            return j;
        }
        j = (long) (zzbwl().nextInt(86400000) + 1);
        this.apU.set(j);
        return j;
    }

    @WorkerThread
    String zzbwp() {
        zzyl();
        return zzbwo().getString("gmp_app_id", null);
    }

    @WorkerThread
    Boolean zzbwq() {
        zzyl();
        return !zzbwo().contains("use_service") ? null : Boolean.valueOf(zzbwo().getBoolean("use_service", false));
    }

    @WorkerThread
    void zzbwr() {
        boolean z = true;
        zzyl();
        zzbvg().zzbwj().log("Clearing collection preferences.");
        boolean contains = zzbwo().contains("measurement_enabled");
        if (contains) {
            z = zzcg(true);
        }
        Editor edit = zzbwo().edit();
        edit.clear();
        edit.apply();
        if (contains) {
            setMeasurementEnabled(z);
        }
    }

    @WorkerThread
    protected String zzbws() {
        zzyl();
        String string = zzbwo().getString("previous_os_version", null);
        String zzbvv = zzbuz().zzbvv();
        if (!(TextUtils.isEmpty(zzbvv) || zzbvv.equals(string))) {
            Editor edit = zzbwo().edit();
            edit.putString("previous_os_version", zzbvv);
            edit.apply();
        }
        return string;
    }

    @WorkerThread
    void zzcf(boolean z) {
        zzyl();
        zzbvg().zzbwj().zzj("Setting useService", Boolean.valueOf(z));
        Editor edit = zzbwo().edit();
        edit.putBoolean("use_service", z);
        edit.apply();
    }

    @WorkerThread
    boolean zzcg(boolean z) {
        zzyl();
        return zzbwo().getBoolean("measurement_enabled", z);
    }

    @WorkerThread
    @NonNull
    Pair<String, Boolean> zzml(String str) {
        zzyl();
        long elapsedRealtime = zzaan().elapsedRealtime();
        if (this.apV != null && elapsedRealtime < this.apX) {
            return new Pair(this.apV, Boolean.valueOf(this.apW));
        }
        this.apX = elapsedRealtime + zzbvi().zzlr(str);
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(true);
        try {
            Info advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
            this.apV = advertisingIdInfo.getId();
            if (this.apV == null) {
                this.apV = "";
            }
            this.apW = advertisingIdInfo.isLimitAdTrackingEnabled();
        } catch (Throwable th) {
            zzbvg().zzbwi().zzj("Unable to get advertising id", th);
            this.apV = "";
        }
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(false);
        return new Pair(this.apV, Boolean.valueOf(this.apW));
    }

    String zzmm(String str) {
        String str2 = (String) zzml(str).first;
        if (zzal.zzfi("MD5") == null) {
            return null;
        }
        return String.format(Locale.US, "%032X", new Object[]{new BigInteger(1, zzal.zzfi("MD5").digest(str2.getBytes()))});
    }

    @WorkerThread
    void zzmn(String str) {
        zzyl();
        Editor edit = zzbwo().edit();
        edit.putString("gmp_app_id", str);
        edit.apply();
    }

    protected void zzym() {
        this.dy = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
        this.aqe = this.dy.getBoolean("has_been_opened", false);
        if (!this.aqe) {
            Editor edit = this.dy.edit();
            edit.putBoolean("has_been_opened", true);
            edit.apply();
        }
    }
}
