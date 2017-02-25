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
    static final Pair<String, Long> zzbtb = new Pair("", Long.valueOf(0));
    private SharedPreferences zzagD;
    public final zzc zzbtc = new zzc("health_monitor", zzKm().zzpz());
    public final zzb zzbtd = new zzb(this, "last_upload", 0);
    public final zzb zzbte = new zzb(this, "last_upload_attempt", 0);
    public final zzb zzbtf = new zzb(this, "backoff", 0);
    public final zzb zzbtg = new zzb(this, "last_delete_stale", 0);
    public final zzb zzbth = new zzb(this, "midnight_offset", 0);
    private String zzbti;
    private boolean zzbtj;
    private long zzbtk;
    private String zzbtl;
    private long zzbtm;
    private final Object zzbtn = new Object();
    private SecureRandom zzbto;
    public final zzb zzbtp = new zzb(this, "time_before_start", 10000);
    public final zzb zzbtq = new zzb(this, "session_timeout", 1800000);
    public final zza zzbtr = new zza(this, "start_new_session", true);
    public final zzb zzbts = new zzb(this, "last_pause_time", 0);
    public final zzb zzbtt = new zzb(this, "time_active", 0);
    public boolean zzbtu;

    public final class zza {
        private final String zzAX;
        private boolean zzayS;
        private final boolean zzbtv;
        private boolean zzbtw;
        final /* synthetic */ zzaua zzbtx;

        public zza(zzaua com_google_android_gms_internal_zzaua, String str, boolean z) {
            this.zzbtx = com_google_android_gms_internal_zzaua;
            zzac.zzdr(str);
            this.zzAX = str;
            this.zzbtv = z;
        }

        @WorkerThread
        private void zzMo() {
            if (!this.zzbtw) {
                this.zzbtw = true;
                this.zzayS = this.zzbtx.zzagD.getBoolean(this.zzAX, this.zzbtv);
            }
        }

        @WorkerThread
        public boolean get() {
            zzMo();
            return this.zzayS;
        }

        @WorkerThread
        public void set(boolean z) {
            Editor edit = this.zzbtx.zzagD.edit();
            edit.putBoolean(this.zzAX, z);
            edit.apply();
            this.zzayS = z;
        }
    }

    public final class zzb {
        private final String zzAX;
        private long zzadd;
        private boolean zzbtw;
        final /* synthetic */ zzaua zzbtx;
        private final long zzbty;

        public zzb(zzaua com_google_android_gms_internal_zzaua, String str, long j) {
            this.zzbtx = com_google_android_gms_internal_zzaua;
            zzac.zzdr(str);
            this.zzAX = str;
            this.zzbty = j;
        }

        @WorkerThread
        private void zzMo() {
            if (!this.zzbtw) {
                this.zzbtw = true;
                this.zzadd = this.zzbtx.zzagD.getLong(this.zzAX, this.zzbty);
            }
        }

        @WorkerThread
        public long get() {
            zzMo();
            return this.zzadd;
        }

        @WorkerThread
        public void set(long j) {
            Editor edit = this.zzbtx.zzagD.edit();
            edit.putLong(this.zzAX, j);
            edit.apply();
            this.zzadd = j;
        }
    }

    public final class zzc {
        private final long zzagH;
        private final String zzbtA;
        private final String zzbtB;
        final /* synthetic */ zzaua zzbtx;
        final String zzbtz;

        private zzc(zzaua com_google_android_gms_internal_zzaua, String str, long j) {
            this.zzbtx = com_google_android_gms_internal_zzaua;
            zzac.zzdr(str);
            zzac.zzax(j > 0);
            this.zzbtz = String.valueOf(str).concat(":start");
            this.zzbtA = String.valueOf(str).concat(":count");
            this.zzbtB = String.valueOf(str).concat(":value");
            this.zzagH = j;
        }

        @WorkerThread
        private void zzqk() {
            this.zzbtx.zzmR();
            long currentTimeMillis = this.zzbtx.zznR().currentTimeMillis();
            Editor edit = this.zzbtx.zzagD.edit();
            edit.remove(this.zzbtA);
            edit.remove(this.zzbtB);
            edit.putLong(this.zzbtz, currentTimeMillis);
            edit.apply();
        }

        @WorkerThread
        private long zzql() {
            this.zzbtx.zzmR();
            long zzqn = zzqn();
            if (zzqn != 0) {
                return Math.abs(zzqn - this.zzbtx.zznR().currentTimeMillis());
            }
            zzqk();
            return 0;
        }

        @WorkerThread
        private long zzqn() {
            return this.zzbtx.zzMi().getLong(this.zzbtz, 0);
        }

        @WorkerThread
        public void zzcc(String str) {
            zzk(str, 1);
        }

        @WorkerThread
        public void zzk(String str, long j) {
            this.zzbtx.zzmR();
            if (zzqn() == 0) {
                zzqk();
            }
            if (str == null) {
                str = "";
            }
            long j2 = this.zzbtx.zzagD.getLong(this.zzbtA, 0);
            if (j2 <= 0) {
                Editor edit = this.zzbtx.zzagD.edit();
                edit.putString(this.zzbtB, str);
                edit.putLong(this.zzbtA, j);
                edit.apply();
                return;
            }
            Object obj = (this.zzbtx.zzMf().nextLong() & Long.MAX_VALUE) < (Long.MAX_VALUE / (j2 + j)) * j ? 1 : null;
            Editor edit2 = this.zzbtx.zzagD.edit();
            if (obj != null) {
                edit2.putString(this.zzbtB, str);
            }
            edit2.putLong(this.zzbtA, j2 + j);
            edit2.apply();
        }

        @WorkerThread
        public Pair<String, Long> zzqm() {
            this.zzbtx.zzmR();
            long zzql = zzql();
            if (zzql < this.zzagH) {
                return null;
            }
            if (zzql > this.zzagH * 2) {
                zzqk();
                return null;
            }
            String string = this.zzbtx.zzMi().getString(this.zzbtB, null);
            zzql = this.zzbtx.zzMi().getLong(this.zzbtA, 0);
            zzqk();
            return (string == null || zzql <= 0) ? zzaua.zzbtb : new Pair(string, Long.valueOf(zzql));
        }
    }

    zzaua(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    @WorkerThread
    private SecureRandom zzMf() {
        zzmR();
        if (this.zzbto == null) {
            this.zzbto = new SecureRandom();
        }
        return this.zzbto;
    }

    @WorkerThread
    private SharedPreferences zzMi() {
        zzmR();
        zzob();
        return this.zzagD;
    }

    @WorkerThread
    void setMeasurementEnabled(boolean z) {
        zzmR();
        zzKk().zzMd().zzj("Setting measurementEnabled", Boolean.valueOf(z));
        Editor edit = zzMi().edit();
        edit.putBoolean("measurement_enabled", z);
        edit.apply();
    }

    @WorkerThread
    String zzKp() {
        zzmR();
        try {
            return com.google.firebase.iid.zzc.zzabK().getId();
        } catch (IllegalStateException e) {
            zzKk().zzLZ().log("Failed to retrieve Firebase Instance Id");
            return null;
        }
    }

    @WorkerThread
    String zzMg() {
        zzMf().nextBytes(new byte[16]);
        return String.format(Locale.US, "%032x", new Object[]{new BigInteger(1, r0)});
    }

    @WorkerThread
    long zzMh() {
        zzob();
        zzmR();
        long j = this.zzbth.get();
        if (j != 0) {
            return j;
        }
        j = (long) (zzMf().nextInt(86400000) + 1);
        this.zzbth.set(j);
        return j;
    }

    @WorkerThread
    String zzMj() {
        zzmR();
        return zzMi().getString("gmp_app_id", null);
    }

    String zzMk() {
        String str;
        synchronized (this.zzbtn) {
            if (Math.abs(zznR().elapsedRealtime() - this.zzbtm) < 1000) {
                str = this.zzbtl;
            } else {
                str = null;
            }
        }
        return str;
    }

    @WorkerThread
    Boolean zzMl() {
        zzmR();
        return !zzMi().contains("use_service") ? null : Boolean.valueOf(zzMi().getBoolean("use_service", false));
    }

    @WorkerThread
    void zzMm() {
        boolean z = true;
        zzmR();
        zzKk().zzMd().log("Clearing collection preferences.");
        boolean contains = zzMi().contains("measurement_enabled");
        if (contains) {
            z = zzaL(true);
        }
        Editor edit = zzMi().edit();
        edit.clear();
        edit.apply();
        if (contains) {
            setMeasurementEnabled(z);
        }
    }

    @WorkerThread
    protected String zzMn() {
        zzmR();
        String string = zzMi().getString("previous_os_version", null);
        String zzLR = zzKb().zzLR();
        if (!(TextUtils.isEmpty(zzLR) || zzLR.equals(string))) {
            Editor edit = zzMi().edit();
            edit.putString("previous_os_version", zzLR);
            edit.apply();
        }
        return string;
    }

    @WorkerThread
    void zzaK(boolean z) {
        zzmR();
        zzKk().zzMd().zzj("Setting useService", Boolean.valueOf(z));
        Editor edit = zzMi().edit();
        edit.putBoolean("use_service", z);
        edit.apply();
    }

    @WorkerThread
    boolean zzaL(boolean z) {
        zzmR();
        return zzMi().getBoolean("measurement_enabled", z);
    }

    @WorkerThread
    @NonNull
    Pair<String, Boolean> zzfG(String str) {
        zzmR();
        long elapsedRealtime = zznR().elapsedRealtime();
        if (this.zzbti != null && elapsedRealtime < this.zzbtk) {
            return new Pair(this.zzbti, Boolean.valueOf(this.zzbtj));
        }
        this.zzbtk = elapsedRealtime + zzKm().zzfm(str);
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(true);
        try {
            Info advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
            this.zzbti = advertisingIdInfo.getId();
            if (this.zzbti == null) {
                this.zzbti = "";
            }
            this.zzbtj = advertisingIdInfo.isLimitAdTrackingEnabled();
        } catch (Throwable th) {
            zzKk().zzMc().zzj("Unable to get advertising id", th);
            this.zzbti = "";
        }
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(false);
        return new Pair(this.zzbti, Boolean.valueOf(this.zzbtj));
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
        Editor edit = zzMi().edit();
        edit.putString("gmp_app_id", str);
        edit.apply();
    }

    void zzfJ(String str) {
        synchronized (this.zzbtn) {
            this.zzbtl = str;
            this.zzbtm = zznR().elapsedRealtime();
        }
    }

    protected void zzmS() {
        this.zzagD = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
        this.zzbtu = this.zzagD.getBoolean("has_been_opened", false);
        if (!this.zzbtu) {
            Editor edit = this.zzagD.edit();
            edit.putBoolean("has_been_opened", true);
            edit.apply();
        }
    }
}
