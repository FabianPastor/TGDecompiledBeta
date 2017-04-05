package com.google.android.gms.internal;

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

class zzaua extends zzauh {
    static final Pair<String, Long> zzbsX = new Pair("", Long.valueOf(0));
    private SharedPreferences zzagD;
    public final zzc zzbsY = new zzc("health_monitor", zzKn().zzpz());
    public final zzb zzbsZ = new zzb(this, "last_upload", 0);
    public final zzb zzbta = new zzb(this, "last_upload_attempt", 0);
    public final zzb zzbtb = new zzb(this, "backoff", 0);
    public final zzb zzbtc = new zzb(this, "last_delete_stale", 0);
    public final zzb zzbtd = new zzb(this, "midnight_offset", 0);
    private String zzbte;
    private boolean zzbtf;
    private long zzbtg;
    private String zzbth;
    private long zzbti;
    private final Object zzbtj = new Object();
    private SecureRandom zzbtk;
    public final zzb zzbtl = new zzb(this, "time_before_start", 10000);
    public final zzb zzbtm = new zzb(this, "session_timeout", 1800000);
    public final zza zzbtn = new zza(this, "start_new_session", true);
    public final zzb zzbto = new zzb(this, "last_pause_time", 0);
    public final zzb zzbtp = new zzb(this, "time_active", 0);
    public boolean zzbtq;

    public final class zza {
        private final String zzAX;
        private boolean zzayS;
        private final boolean zzbtr;
        private boolean zzbts;
        final /* synthetic */ zzaua zzbtt;

        public zza(zzaua com_google_android_gms_internal_zzaua, String str, boolean z) {
            this.zzbtt = com_google_android_gms_internal_zzaua;
            zzac.zzdr(str);
            this.zzAX = str;
            this.zzbtr = z;
        }

        @WorkerThread
        private void zzMp() {
            if (!this.zzbts) {
                this.zzbts = true;
                this.zzayS = this.zzbtt.zzagD.getBoolean(this.zzAX, this.zzbtr);
            }
        }

        @WorkerThread
        public boolean get() {
            zzMp();
            return this.zzayS;
        }

        @WorkerThread
        public void set(boolean z) {
            Editor edit = this.zzbtt.zzagD.edit();
            edit.putBoolean(this.zzAX, z);
            edit.apply();
            this.zzayS = z;
        }
    }

    public final class zzb {
        private final String zzAX;
        private long zzadd;
        private boolean zzbts;
        final /* synthetic */ zzaua zzbtt;
        private final long zzbtu;

        public zzb(zzaua com_google_android_gms_internal_zzaua, String str, long j) {
            this.zzbtt = com_google_android_gms_internal_zzaua;
            zzac.zzdr(str);
            this.zzAX = str;
            this.zzbtu = j;
        }

        @WorkerThread
        private void zzMp() {
            if (!this.zzbts) {
                this.zzbts = true;
                this.zzadd = this.zzbtt.zzagD.getLong(this.zzAX, this.zzbtu);
            }
        }

        @WorkerThread
        public long get() {
            zzMp();
            return this.zzadd;
        }

        @WorkerThread
        public void set(long j) {
            Editor edit = this.zzbtt.zzagD.edit();
            edit.putLong(this.zzAX, j);
            edit.apply();
            this.zzadd = j;
        }
    }

    public final class zzc {
        private final long zzagH;
        final /* synthetic */ zzaua zzbtt;
        final String zzbtv;
        private final String zzbtw;
        private final String zzbtx;

        private zzc(zzaua com_google_android_gms_internal_zzaua, String str, long j) {
            this.zzbtt = com_google_android_gms_internal_zzaua;
            zzac.zzdr(str);
            zzac.zzax(j > 0);
            this.zzbtv = String.valueOf(str).concat(":start");
            this.zzbtw = String.valueOf(str).concat(":count");
            this.zzbtx = String.valueOf(str).concat(":value");
            this.zzagH = j;
        }

        @WorkerThread
        private void zzqk() {
            this.zzbtt.zzmR();
            long currentTimeMillis = this.zzbtt.zznR().currentTimeMillis();
            Editor edit = this.zzbtt.zzagD.edit();
            edit.remove(this.zzbtw);
            edit.remove(this.zzbtx);
            edit.putLong(this.zzbtv, currentTimeMillis);
            edit.apply();
        }

        @WorkerThread
        private long zzql() {
            this.zzbtt.zzmR();
            long zzqn = zzqn();
            if (zzqn != 0) {
                return Math.abs(zzqn - this.zzbtt.zznR().currentTimeMillis());
            }
            zzqk();
            return 0;
        }

        @WorkerThread
        private long zzqn() {
            return this.zzbtt.zzMj().getLong(this.zzbtv, 0);
        }

        @WorkerThread
        public void zzcc(String str) {
            zzk(str, 1);
        }

        @WorkerThread
        public void zzk(String str, long j) {
            this.zzbtt.zzmR();
            if (zzqn() == 0) {
                zzqk();
            }
            if (str == null) {
                str = "";
            }
            long j2 = this.zzbtt.zzagD.getLong(this.zzbtw, 0);
            if (j2 <= 0) {
                Editor edit = this.zzbtt.zzagD.edit();
                edit.putString(this.zzbtx, str);
                edit.putLong(this.zzbtw, j);
                edit.apply();
                return;
            }
            Object obj = (this.zzbtt.zzMg().nextLong() & Long.MAX_VALUE) < (Long.MAX_VALUE / (j2 + j)) * j ? 1 : null;
            Editor edit2 = this.zzbtt.zzagD.edit();
            if (obj != null) {
                edit2.putString(this.zzbtx, str);
            }
            edit2.putLong(this.zzbtw, j2 + j);
            edit2.apply();
        }

        @WorkerThread
        public Pair<String, Long> zzqm() {
            this.zzbtt.zzmR();
            long zzql = zzql();
            if (zzql < this.zzagH) {
                return null;
            }
            if (zzql > this.zzagH * 2) {
                zzqk();
                return null;
            }
            String string = this.zzbtt.zzMj().getString(this.zzbtx, null);
            zzql = this.zzbtt.zzMj().getLong(this.zzbtw, 0);
            zzqk();
            return (string == null || zzql <= 0) ? zzaua.zzbsX : new Pair(string, Long.valueOf(zzql));
        }
    }

    zzaua(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    @WorkerThread
    private SecureRandom zzMg() {
        zzmR();
        if (this.zzbtk == null) {
            this.zzbtk = new SecureRandom();
        }
        return this.zzbtk;
    }

    @WorkerThread
    private SharedPreferences zzMj() {
        zzmR();
        zzob();
        return this.zzagD;
    }

    @WorkerThread
    void setMeasurementEnabled(boolean z) {
        zzmR();
        zzKl().zzMe().zzj("Setting measurementEnabled", Boolean.valueOf(z));
        Editor edit = zzMj().edit();
        edit.putBoolean("measurement_enabled", z);
        edit.apply();
    }

    @WorkerThread
    String zzKq() {
        zzmR();
        try {
            return com.google.firebase.iid.zzc.zzabL().getId();
        } catch (IllegalStateException e) {
            zzKl().zzMa().log("Failed to retrieve Firebase Instance Id");
            return null;
        }
    }

    @WorkerThread
    String zzMh() {
        zzMg().nextBytes(new byte[16]);
        return String.format(Locale.US, "%032x", new Object[]{new BigInteger(1, r0)});
    }

    @WorkerThread
    long zzMi() {
        zzob();
        zzmR();
        long j = this.zzbtd.get();
        if (j != 0) {
            return j;
        }
        j = (long) (zzMg().nextInt(86400000) + 1);
        this.zzbtd.set(j);
        return j;
    }

    @WorkerThread
    String zzMk() {
        zzmR();
        return zzMj().getString("gmp_app_id", null);
    }

    String zzMl() {
        String str;
        synchronized (this.zzbtj) {
            if (Math.abs(zznR().elapsedRealtime() - this.zzbti) < 1000) {
                str = this.zzbth;
            } else {
                str = null;
            }
        }
        return str;
    }

    @WorkerThread
    Boolean zzMm() {
        zzmR();
        return !zzMj().contains("use_service") ? null : Boolean.valueOf(zzMj().getBoolean("use_service", false));
    }

    @WorkerThread
    void zzMn() {
        boolean z = true;
        zzmR();
        zzKl().zzMe().log("Clearing collection preferences.");
        boolean contains = zzMj().contains("measurement_enabled");
        if (contains) {
            z = zzaL(true);
        }
        Editor edit = zzMj().edit();
        edit.clear();
        edit.apply();
        if (contains) {
            setMeasurementEnabled(z);
        }
    }

    @WorkerThread
    protected String zzMo() {
        zzmR();
        String string = zzMj().getString("previous_os_version", null);
        String zzLS = zzKc().zzLS();
        if (!(TextUtils.isEmpty(zzLS) || zzLS.equals(string))) {
            Editor edit = zzMj().edit();
            edit.putString("previous_os_version", zzLS);
            edit.apply();
        }
        return string;
    }

    @WorkerThread
    void zzaK(boolean z) {
        zzmR();
        zzKl().zzMe().zzj("Setting useService", Boolean.valueOf(z));
        Editor edit = zzMj().edit();
        edit.putBoolean("use_service", z);
        edit.apply();
    }

    @WorkerThread
    boolean zzaL(boolean z) {
        zzmR();
        return zzMj().getBoolean("measurement_enabled", z);
    }

    @WorkerThread
    @NonNull
    Pair<String, Boolean> zzfG(String str) {
        zzmR();
        long elapsedRealtime = zznR().elapsedRealtime();
        if (this.zzbte != null && elapsedRealtime < this.zzbtg) {
            return new Pair(this.zzbte, Boolean.valueOf(this.zzbtf));
        }
        this.zzbtg = elapsedRealtime + zzKn().zzfm(str);
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(true);
        try {
            Info advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
            this.zzbte = advertisingIdInfo.getId();
            if (this.zzbte == null) {
                this.zzbte = "";
            }
            this.zzbtf = advertisingIdInfo.isLimitAdTrackingEnabled();
        } catch (Throwable th) {
            zzKl().zzMd().zzj("Unable to get advertising id", th);
            this.zzbte = "";
        }
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(false);
        return new Pair(this.zzbte, Boolean.valueOf(this.zzbtf));
    }

    @WorkerThread
    String zzfH(String str) {
        zzmR();
        String str2 = (String) zzfG(str).first;
        if (zzaut.zzch("MD5") == null) {
            return null;
        }
        return String.format(Locale.US, "%032X", new Object[]{new BigInteger(1, zzaut.zzch("MD5").digest(str2.getBytes()))});
    }

    @WorkerThread
    void zzfI(String str) {
        zzmR();
        Editor edit = zzMj().edit();
        edit.putString("gmp_app_id", str);
        edit.apply();
    }

    void zzfJ(String str) {
        synchronized (this.zzbtj) {
            this.zzbth = str;
            this.zzbti = zznR().elapsedRealtime();
        }
    }

    protected void zzmS() {
        this.zzagD = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
        this.zzbtq = this.zzagD.getBoolean("has_been_opened", false);
        if (!this.zzbtq) {
            Editor edit = this.zzagD.edit();
            edit.putBoolean("has_been_opened", true);
            edit.apply();
        }
    }
}
