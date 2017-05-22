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
    static final Pair<String, Long> zzbsZ = new Pair("", Long.valueOf(0));
    private SharedPreferences zzagD;
    public final zzc zzbta = new zzc("health_monitor", zzKn().zzpz());
    public final zzb zzbtb = new zzb(this, "last_upload", 0);
    public final zzb zzbtc = new zzb(this, "last_upload_attempt", 0);
    public final zzb zzbtd = new zzb(this, "backoff", 0);
    public final zzb zzbte = new zzb(this, "last_delete_stale", 0);
    public final zzb zzbtf = new zzb(this, "midnight_offset", 0);
    private String zzbtg;
    private boolean zzbth;
    private long zzbti;
    private String zzbtj;
    private long zzbtk;
    private final Object zzbtl = new Object();
    private SecureRandom zzbtm;
    public final zzb zzbtn = new zzb(this, "time_before_start", 10000);
    public final zzb zzbto = new zzb(this, "session_timeout", 1800000);
    public final zza zzbtp = new zza(this, "start_new_session", true);
    public final zzb zzbtq = new zzb(this, "last_pause_time", 0);
    public final zzb zzbtr = new zzb(this, "time_active", 0);
    public boolean zzbts;

    public final class zza {
        private final String zzAX;
        private boolean zzayS;
        private final boolean zzbtt;
        private boolean zzbtu;
        final /* synthetic */ zzaua zzbtv;

        public zza(zzaua com_google_android_gms_internal_zzaua, String str, boolean z) {
            this.zzbtv = com_google_android_gms_internal_zzaua;
            zzac.zzdr(str);
            this.zzAX = str;
            this.zzbtt = z;
        }

        @WorkerThread
        private void zzMq() {
            if (!this.zzbtu) {
                this.zzbtu = true;
                this.zzayS = this.zzbtv.zzagD.getBoolean(this.zzAX, this.zzbtt);
            }
        }

        @WorkerThread
        public boolean get() {
            zzMq();
            return this.zzayS;
        }

        @WorkerThread
        public void set(boolean z) {
            Editor edit = this.zzbtv.zzagD.edit();
            edit.putBoolean(this.zzAX, z);
            edit.apply();
            this.zzayS = z;
        }
    }

    public final class zzb {
        private final String zzAX;
        private long zzadd;
        private boolean zzbtu;
        final /* synthetic */ zzaua zzbtv;
        private final long zzbtw;

        public zzb(zzaua com_google_android_gms_internal_zzaua, String str, long j) {
            this.zzbtv = com_google_android_gms_internal_zzaua;
            zzac.zzdr(str);
            this.zzAX = str;
            this.zzbtw = j;
        }

        @WorkerThread
        private void zzMq() {
            if (!this.zzbtu) {
                this.zzbtu = true;
                this.zzadd = this.zzbtv.zzagD.getLong(this.zzAX, this.zzbtw);
            }
        }

        @WorkerThread
        public long get() {
            zzMq();
            return this.zzadd;
        }

        @WorkerThread
        public void set(long j) {
            Editor edit = this.zzbtv.zzagD.edit();
            edit.putLong(this.zzAX, j);
            edit.apply();
            this.zzadd = j;
        }
    }

    public final class zzc {
        private final long zzagH;
        final /* synthetic */ zzaua zzbtv;
        final String zzbtx;
        private final String zzbty;
        private final String zzbtz;

        private zzc(zzaua com_google_android_gms_internal_zzaua, String str, long j) {
            this.zzbtv = com_google_android_gms_internal_zzaua;
            zzac.zzdr(str);
            zzac.zzaw(j > 0);
            this.zzbtx = String.valueOf(str).concat(":start");
            this.zzbty = String.valueOf(str).concat(":count");
            this.zzbtz = String.valueOf(str).concat(":value");
            this.zzagH = j;
        }

        @WorkerThread
        private void zzqk() {
            this.zzbtv.zzmR();
            long currentTimeMillis = this.zzbtv.zznR().currentTimeMillis();
            Editor edit = this.zzbtv.zzagD.edit();
            edit.remove(this.zzbty);
            edit.remove(this.zzbtz);
            edit.putLong(this.zzbtx, currentTimeMillis);
            edit.apply();
        }

        @WorkerThread
        private long zzql() {
            this.zzbtv.zzmR();
            long zzqn = zzqn();
            if (zzqn != 0) {
                return Math.abs(zzqn - this.zzbtv.zznR().currentTimeMillis());
            }
            zzqk();
            return 0;
        }

        @WorkerThread
        private long zzqn() {
            return this.zzbtv.zzMk().getLong(this.zzbtx, 0);
        }

        @WorkerThread
        public void zzcc(String str) {
            zzk(str, 1);
        }

        @WorkerThread
        public void zzk(String str, long j) {
            this.zzbtv.zzmR();
            if (zzqn() == 0) {
                zzqk();
            }
            if (str == null) {
                str = "";
            }
            long j2 = this.zzbtv.zzagD.getLong(this.zzbty, 0);
            if (j2 <= 0) {
                Editor edit = this.zzbtv.zzagD.edit();
                edit.putString(this.zzbtz, str);
                edit.putLong(this.zzbty, j);
                edit.apply();
                return;
            }
            Object obj = (this.zzbtv.zzMh().nextLong() & Long.MAX_VALUE) < (Long.MAX_VALUE / (j2 + j)) * j ? 1 : null;
            Editor edit2 = this.zzbtv.zzagD.edit();
            if (obj != null) {
                edit2.putString(this.zzbtz, str);
            }
            edit2.putLong(this.zzbty, j2 + j);
            edit2.apply();
        }

        @WorkerThread
        public Pair<String, Long> zzqm() {
            this.zzbtv.zzmR();
            long zzql = zzql();
            if (zzql < this.zzagH) {
                return null;
            }
            if (zzql > this.zzagH * 2) {
                zzqk();
                return null;
            }
            String string = this.zzbtv.zzMk().getString(this.zzbtz, null);
            zzql = this.zzbtv.zzMk().getLong(this.zzbty, 0);
            zzqk();
            return (string == null || zzql <= 0) ? zzaua.zzbsZ : new Pair(string, Long.valueOf(zzql));
        }
    }

    zzaua(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    @WorkerThread
    private SharedPreferences zzMk() {
        zzmR();
        zzob();
        return this.zzagD;
    }

    @WorkerThread
    void setMeasurementEnabled(boolean z) {
        zzmR();
        zzKl().zzMf().zzj("Setting measurementEnabled", Boolean.valueOf(z));
        Editor edit = zzMk().edit();
        edit.putBoolean("measurement_enabled", z);
        edit.apply();
    }

    @WorkerThread
    String zzKq() {
        zzmR();
        try {
            return com.google.firebase.iid.zzc.zzabN().getId();
        } catch (IllegalStateException e) {
            zzKl().zzMb().log("Failed to retrieve Firebase Instance Id");
            return null;
        }
    }

    @WorkerThread
    protected SecureRandom zzMh() {
        zzmR();
        if (this.zzbtm == null) {
            this.zzbtm = new SecureRandom();
        }
        return this.zzbtm;
    }

    @WorkerThread
    String zzMi() {
        zzMh().nextBytes(new byte[16]);
        return String.format(Locale.US, "%032x", new Object[]{new BigInteger(1, r0)});
    }

    @WorkerThread
    long zzMj() {
        zzob();
        zzmR();
        long j = this.zzbtf.get();
        if (j != 0) {
            return j;
        }
        j = (long) (zzMh().nextInt(86400000) + 1);
        this.zzbtf.set(j);
        return j;
    }

    @WorkerThread
    String zzMl() {
        zzmR();
        return zzMk().getString("gmp_app_id", null);
    }

    String zzMm() {
        String str;
        synchronized (this.zzbtl) {
            if (Math.abs(zznR().elapsedRealtime() - this.zzbtk) < 1000) {
                str = this.zzbtj;
            } else {
                str = null;
            }
        }
        return str;
    }

    @WorkerThread
    Boolean zzMn() {
        zzmR();
        return !zzMk().contains("use_service") ? null : Boolean.valueOf(zzMk().getBoolean("use_service", false));
    }

    @WorkerThread
    void zzMo() {
        boolean z = true;
        zzmR();
        zzKl().zzMf().log("Clearing collection preferences.");
        boolean contains = zzMk().contains("measurement_enabled");
        if (contains) {
            z = zzaK(true);
        }
        Editor edit = zzMk().edit();
        edit.clear();
        edit.apply();
        if (contains) {
            setMeasurementEnabled(z);
        }
    }

    @WorkerThread
    protected String zzMp() {
        zzmR();
        String string = zzMk().getString("previous_os_version", null);
        String zzLS = zzKc().zzLS();
        if (!(TextUtils.isEmpty(zzLS) || zzLS.equals(string))) {
            Editor edit = zzMk().edit();
            edit.putString("previous_os_version", zzLS);
            edit.apply();
        }
        return string;
    }

    @WorkerThread
    void zzaJ(boolean z) {
        zzmR();
        zzKl().zzMf().zzj("Setting useService", Boolean.valueOf(z));
        Editor edit = zzMk().edit();
        edit.putBoolean("use_service", z);
        edit.apply();
    }

    @WorkerThread
    boolean zzaK(boolean z) {
        zzmR();
        return zzMk().getBoolean("measurement_enabled", z);
    }

    @WorkerThread
    @NonNull
    Pair<String, Boolean> zzfG(String str) {
        zzmR();
        long elapsedRealtime = zznR().elapsedRealtime();
        if (this.zzbtg != null && elapsedRealtime < this.zzbti) {
            return new Pair(this.zzbtg, Boolean.valueOf(this.zzbth));
        }
        this.zzbti = elapsedRealtime + zzKn().zzfm(str);
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(true);
        try {
            Info advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
            this.zzbtg = advertisingIdInfo.getId();
            if (this.zzbtg == null) {
                this.zzbtg = "";
            }
            this.zzbth = advertisingIdInfo.isLimitAdTrackingEnabled();
        } catch (Throwable th) {
            zzKl().zzMe().zzj("Unable to get advertising id", th);
            this.zzbtg = "";
        }
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(false);
        return new Pair(this.zzbtg, Boolean.valueOf(this.zzbth));
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
        Editor edit = zzMk().edit();
        edit.putString("gmp_app_id", str);
        edit.apply();
    }

    void zzfJ(String str) {
        synchronized (this.zzbtl) {
            this.zzbtj = str;
            this.zzbtk = zznR().elapsedRealtime();
        }
    }

    protected void zzmS() {
        this.zzagD = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
        this.zzbts = this.zzagD.getBoolean("has_been_opened", false);
        if (!this.zzbts) {
            Editor edit = this.zzagD.edit();
            edit.putBoolean("has_been_opened", true);
            edit.apply();
        }
    }
}
