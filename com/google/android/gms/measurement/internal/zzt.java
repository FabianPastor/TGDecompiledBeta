package com.google.android.gms.measurement.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.internal.zzaa;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Locale;

class zzt extends zzaa {
    static final Pair<String, Long> asX = new Pair("", Long.valueOf(0));
    public final zzc asY = new zzc("health_monitor", zzbwd().zzafj());
    public final zzb asZ = new zzb(this, "last_upload", 0);
    public final zzb ata = new zzb(this, "last_upload_attempt", 0);
    public final zzb atb = new zzb(this, "backoff", 0);
    public final zzb atc = new zzb(this, "last_delete_stale", 0);
    public final zzb atd = new zzb(this, "midnight_offset", 0);
    private String ate;
    private boolean atf;
    private long atg;
    private SecureRandom ath;
    public final zzb ati = new zzb(this, "time_before_start", 10000);
    public final zzb atj = new zzb(this, "session_timeout", 1800000);
    public final zza atk = new zza(this, "start_new_session", true);
    public final zzb atl = new zzb(this, "last_pause_time", 0);
    public final zzb atm = new zzb(this, "time_active", 0);
    public boolean atn;
    private SharedPreferences fF;

    public final class zza {
        private final boolean ato;
        private boolean atp;
        final /* synthetic */ zzt atq;
        private boolean xv;
        private final String zzbcn;

        public zza(zzt com_google_android_gms_measurement_internal_zzt, String str, boolean z) {
            this.atq = com_google_android_gms_measurement_internal_zzt;
            zzaa.zzib(str);
            this.zzbcn = str;
            this.ato = z;
        }

        @WorkerThread
        private void zzbxo() {
            if (!this.atp) {
                this.atp = true;
                this.xv = this.atq.fF.getBoolean(this.zzbcn, this.ato);
            }
        }

        @WorkerThread
        public boolean get() {
            zzbxo();
            return this.xv;
        }

        @WorkerThread
        public void set(boolean z) {
            Editor edit = this.atq.fF.edit();
            edit.putBoolean(this.zzbcn, z);
            edit.apply();
            this.xv = z;
        }
    }

    public final class zzb {
        private boolean atp;
        final /* synthetic */ zzt atq;
        private final long atr;
        private long cf;
        private final String zzbcn;

        public zzb(zzt com_google_android_gms_measurement_internal_zzt, String str, long j) {
            this.atq = com_google_android_gms_measurement_internal_zzt;
            zzaa.zzib(str);
            this.zzbcn = str;
            this.atr = j;
        }

        @WorkerThread
        private void zzbxo() {
            if (!this.atp) {
                this.atp = true;
                this.cf = this.atq.fF.getLong(this.zzbcn, this.atr);
            }
        }

        @WorkerThread
        public long get() {
            zzbxo();
            return this.cf;
        }

        @WorkerThread
        public void set(long j) {
            Editor edit = this.atq.fF.edit();
            edit.putLong(this.zzbcn, j);
            edit.apply();
            this.cf = j;
        }
    }

    public final class zzc {
        final /* synthetic */ zzt atq;
        final String ats;
        private final String att;
        private final String atu;
        private final long fJ;

        private zzc(zzt com_google_android_gms_measurement_internal_zzt, String str, long j) {
            this.atq = com_google_android_gms_measurement_internal_zzt;
            zzaa.zzib(str);
            zzaa.zzbt(j > 0);
            this.ats = String.valueOf(str).concat(":start");
            this.att = String.valueOf(str).concat(":count");
            this.atu = String.valueOf(str).concat(":value");
            this.fJ = j;
        }

        @WorkerThread
        private void zzagu() {
            this.atq.zzzx();
            long currentTimeMillis = this.atq.zzabz().currentTimeMillis();
            Editor edit = this.atq.fF.edit();
            edit.remove(this.att);
            edit.remove(this.atu);
            edit.putLong(this.ats, currentTimeMillis);
            edit.apply();
        }

        @WorkerThread
        private long zzagv() {
            this.atq.zzzx();
            long zzagx = zzagx();
            if (zzagx != 0) {
                return Math.abs(zzagx - this.atq.zzabz().currentTimeMillis());
            }
            zzagu();
            return 0;
        }

        @WorkerThread
        private long zzagx() {
            return this.atq.zzbxj().getLong(this.ats, 0);
        }

        @WorkerThread
        public Pair<String, Long> zzagw() {
            this.atq.zzzx();
            long zzagv = zzagv();
            if (zzagv < this.fJ) {
                return null;
            }
            if (zzagv > this.fJ * 2) {
                zzagu();
                return null;
            }
            String string = this.atq.zzbxj().getString(this.atu, null);
            zzagv = this.atq.zzbxj().getLong(this.att, 0);
            zzagu();
            return (string == null || zzagv <= 0) ? zzt.asX : new Pair(string, Long.valueOf(zzagv));
        }

        @WorkerThread
        public void zzfg(String str) {
            zzg(str, 1);
        }

        @WorkerThread
        public void zzg(String str, long j) {
            this.atq.zzzx();
            if (zzagx() == 0) {
                zzagu();
            }
            if (str == null) {
                str = "";
            }
            long j2 = this.atq.fF.getLong(this.att, 0);
            if (j2 <= 0) {
                Editor edit = this.atq.fF.edit();
                edit.putString(this.atu, str);
                edit.putLong(this.att, j);
                edit.apply();
                return;
            }
            Object obj = (this.atq.zzbxg().nextLong() & Long.MAX_VALUE) < (Long.MAX_VALUE / (j2 + j)) * j ? 1 : null;
            Editor edit2 = this.atq.fF.edit();
            if (obj != null) {
                edit2.putString(this.atu, str);
            }
            edit2.putLong(this.att, j2 + j);
            edit2.apply();
        }
    }

    zzt(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    @WorkerThread
    private SecureRandom zzbxg() {
        zzzx();
        if (this.ath == null) {
            this.ath = new SecureRandom();
        }
        return this.ath;
    }

    @WorkerThread
    private SharedPreferences zzbxj() {
        zzzx();
        zzacj();
        return this.fF;
    }

    @WorkerThread
    void setMeasurementEnabled(boolean z) {
        zzzx();
        zzbwb().zzbxe().zzj("Setting measurementEnabled", Boolean.valueOf(z));
        Editor edit = zzbxj().edit();
        edit.putBoolean("measurement_enabled", z);
        edit.apply();
    }

    @WorkerThread
    String zzbtj() {
        zzzx();
        try {
            return com.google.firebase.iid.zzc.C().getId();
        } catch (IllegalStateException e) {
            zzbwb().zzbxa().log("Failed to retrieve Firebase Instance Id");
            return null;
        }
    }

    @WorkerThread
    String zzbxh() {
        zzbxg().nextBytes(new byte[16]);
        return String.format(Locale.US, "%032x", new Object[]{new BigInteger(1, r0)});
    }

    @WorkerThread
    long zzbxi() {
        zzacj();
        zzzx();
        long j = this.atd.get();
        if (j != 0) {
            return j;
        }
        j = (long) (zzbxg().nextInt(86400000) + 1);
        this.atd.set(j);
        return j;
    }

    @WorkerThread
    String zzbxk() {
        zzzx();
        return zzbxj().getString("gmp_app_id", null);
    }

    @WorkerThread
    Boolean zzbxl() {
        zzzx();
        return !zzbxj().contains("use_service") ? null : Boolean.valueOf(zzbxj().getBoolean("use_service", false));
    }

    @WorkerThread
    void zzbxm() {
        boolean z = true;
        zzzx();
        zzbwb().zzbxe().log("Clearing collection preferences.");
        boolean contains = zzbxj().contains("measurement_enabled");
        if (contains) {
            z = zzch(true);
        }
        Editor edit = zzbxj().edit();
        edit.clear();
        edit.apply();
        if (contains) {
            setMeasurementEnabled(z);
        }
    }

    @WorkerThread
    protected String zzbxn() {
        zzzx();
        String string = zzbxj().getString("previous_os_version", null);
        String zzbws = zzbvs().zzbws();
        if (!(TextUtils.isEmpty(zzbws) || zzbws.equals(string))) {
            Editor edit = zzbxj().edit();
            edit.putString("previous_os_version", zzbws);
            edit.apply();
        }
        return string;
    }

    @WorkerThread
    void zzcg(boolean z) {
        zzzx();
        zzbwb().zzbxe().zzj("Setting useService", Boolean.valueOf(z));
        Editor edit = zzbxj().edit();
        edit.putBoolean("use_service", z);
        edit.apply();
    }

    @WorkerThread
    boolean zzch(boolean z) {
        zzzx();
        return zzbxj().getBoolean("measurement_enabled", z);
    }

    @WorkerThread
    @NonNull
    Pair<String, Boolean> zzmk(String str) {
        zzzx();
        long elapsedRealtime = zzabz().elapsedRealtime();
        if (this.ate != null && elapsedRealtime < this.atg) {
            return new Pair(this.ate, Boolean.valueOf(this.atf));
        }
        this.atg = elapsedRealtime + zzbwd().zzlr(str);
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(true);
        try {
            Info advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
            this.ate = advertisingIdInfo.getId();
            if (this.ate == null) {
                this.ate = "";
            }
            this.atf = advertisingIdInfo.isLimitAdTrackingEnabled();
        } catch (Throwable th) {
            zzbwb().zzbxd().zzj("Unable to get advertising id", th);
            this.ate = "";
        }
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(false);
        return new Pair(this.ate, Boolean.valueOf(this.atf));
    }

    @WorkerThread
    String zzml(String str) {
        zzzx();
        String str2 = (String) zzmk(str).first;
        if (zzal.zzfl("MD5") == null) {
            return null;
        }
        return String.format(Locale.US, "%032X", new Object[]{new BigInteger(1, zzal.zzfl("MD5").digest(str2.getBytes()))});
    }

    @WorkerThread
    void zzmm(String str) {
        zzzx();
        Editor edit = zzbxj().edit();
        edit.putString("gmp_app_id", str);
        edit.apply();
    }

    protected void zzzy() {
        this.fF = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
        this.atn = this.fF.getBoolean("has_been_opened", false);
        if (!this.atn) {
            Editor edit = this.fF.edit();
            edit.putBoolean("has_been_opened", true);
            edit.apply();
        }
    }
}
