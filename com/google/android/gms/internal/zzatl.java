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

class zzatl extends zzats {
    static final Pair<String, Long> zzbse = new Pair("", Long.valueOf(0));
    private SharedPreferences zzafC;
    public final zzc zzbsf = new zzc("health_monitor", zzJv().zzoZ());
    public final zzb zzbsg = new zzb(this, "last_upload", 0);
    public final zzb zzbsh = new zzb(this, "last_upload_attempt", 0);
    public final zzb zzbsi = new zzb(this, "backoff", 0);
    public final zzb zzbsj = new zzb(this, "last_delete_stale", 0);
    public final zzb zzbsk = new zzb(this, "midnight_offset", 0);
    private String zzbsl;
    private boolean zzbsm;
    private long zzbsn;
    private SecureRandom zzbso;
    public final zzb zzbsp = new zzb(this, "time_before_start", 10000);
    public final zzb zzbsq = new zzb(this, "session_timeout", 1800000);
    public final zza zzbsr = new zza(this, "start_new_session", true);
    public final zzb zzbss = new zzb(this, "last_pause_time", 0);
    public final zzb zzbst = new zzb(this, "time_active", 0);
    public boolean zzbsu;

    public final class zza {
        private final String zzAH;
        private boolean zzaxF;
        private final boolean zzbsv;
        private boolean zzbsw;
        final /* synthetic */ zzatl zzbsx;

        public zza(zzatl com_google_android_gms_internal_zzatl, String str, boolean z) {
            this.zzbsx = com_google_android_gms_internal_zzatl;
            zzac.zzdv(str);
            this.zzAH = str;
            this.zzbsv = z;
        }

        @WorkerThread
        private void zzLq() {
            if (!this.zzbsw) {
                this.zzbsw = true;
                this.zzaxF = this.zzbsx.zzafC.getBoolean(this.zzAH, this.zzbsv);
            }
        }

        @WorkerThread
        public boolean get() {
            zzLq();
            return this.zzaxF;
        }

        @WorkerThread
        public void set(boolean z) {
            Editor edit = this.zzbsx.zzafC.edit();
            edit.putBoolean(this.zzAH, z);
            edit.apply();
            this.zzaxF = z;
        }
    }

    public final class zzb {
        private final String zzAH;
        private long zzacc;
        private boolean zzbsw;
        final /* synthetic */ zzatl zzbsx;
        private final long zzbsy;

        public zzb(zzatl com_google_android_gms_internal_zzatl, String str, long j) {
            this.zzbsx = com_google_android_gms_internal_zzatl;
            zzac.zzdv(str);
            this.zzAH = str;
            this.zzbsy = j;
        }

        @WorkerThread
        private void zzLq() {
            if (!this.zzbsw) {
                this.zzbsw = true;
                this.zzacc = this.zzbsx.zzafC.getLong(this.zzAH, this.zzbsy);
            }
        }

        @WorkerThread
        public long get() {
            zzLq();
            return this.zzacc;
        }

        @WorkerThread
        public void set(long j) {
            Editor edit = this.zzbsx.zzafC.edit();
            edit.putLong(this.zzAH, j);
            edit.apply();
            this.zzacc = j;
        }
    }

    public final class zzc {
        private final long zzafG;
        private final String zzbsA;
        private final String zzbsB;
        final /* synthetic */ zzatl zzbsx;
        final String zzbsz;

        private zzc(zzatl com_google_android_gms_internal_zzatl, String str, long j) {
            this.zzbsx = com_google_android_gms_internal_zzatl;
            zzac.zzdv(str);
            zzac.zzas(j > 0);
            this.zzbsz = String.valueOf(str).concat(":start");
            this.zzbsA = String.valueOf(str).concat(":count");
            this.zzbsB = String.valueOf(str).concat(":value");
            this.zzafG = j;
        }

        @WorkerThread
        private void zzpK() {
            this.zzbsx.zzmq();
            long currentTimeMillis = this.zzbsx.zznq().currentTimeMillis();
            Editor edit = this.zzbsx.zzafC.edit();
            edit.remove(this.zzbsA);
            edit.remove(this.zzbsB);
            edit.putLong(this.zzbsz, currentTimeMillis);
            edit.apply();
        }

        @WorkerThread
        private long zzpL() {
            this.zzbsx.zzmq();
            long zzpN = zzpN();
            if (zzpN != 0) {
                return Math.abs(zzpN - this.zzbsx.zznq().currentTimeMillis());
            }
            zzpK();
            return 0;
        }

        @WorkerThread
        private long zzpN() {
            return this.zzbsx.zzLl().getLong(this.zzbsz, 0);
        }

        @WorkerThread
        public void zzcb(String str) {
            zzi(str, 1);
        }

        @WorkerThread
        public void zzi(String str, long j) {
            this.zzbsx.zzmq();
            if (zzpN() == 0) {
                zzpK();
            }
            if (str == null) {
                str = "";
            }
            long j2 = this.zzbsx.zzafC.getLong(this.zzbsA, 0);
            if (j2 <= 0) {
                Editor edit = this.zzbsx.zzafC.edit();
                edit.putString(this.zzbsB, str);
                edit.putLong(this.zzbsA, j);
                edit.apply();
                return;
            }
            Object obj = (this.zzbsx.zzLi().nextLong() & Long.MAX_VALUE) < (Long.MAX_VALUE / (j2 + j)) * j ? 1 : null;
            Editor edit2 = this.zzbsx.zzafC.edit();
            if (obj != null) {
                edit2.putString(this.zzbsB, str);
            }
            edit2.putLong(this.zzbsA, j2 + j);
            edit2.apply();
        }

        @WorkerThread
        public Pair<String, Long> zzpM() {
            this.zzbsx.zzmq();
            long zzpL = zzpL();
            if (zzpL < this.zzafG) {
                return null;
            }
            if (zzpL > this.zzafG * 2) {
                zzpK();
                return null;
            }
            String string = this.zzbsx.zzLl().getString(this.zzbsB, null);
            zzpL = this.zzbsx.zzLl().getLong(this.zzbsA, 0);
            zzpK();
            return (string == null || zzpL <= 0) ? zzatl.zzbse : new Pair(string, Long.valueOf(zzpL));
        }
    }

    zzatl(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
    }

    @WorkerThread
    private SecureRandom zzLi() {
        zzmq();
        if (this.zzbso == null) {
            this.zzbso = new SecureRandom();
        }
        return this.zzbso;
    }

    @WorkerThread
    private SharedPreferences zzLl() {
        zzmq();
        zznA();
        return this.zzafC;
    }

    @WorkerThread
    void setMeasurementEnabled(boolean z) {
        zzmq();
        zzJt().zzLg().zzj("Setting measurementEnabled", Boolean.valueOf(z));
        Editor edit = zzLl().edit();
        edit.putBoolean("measurement_enabled", z);
        edit.apply();
    }

    @WorkerThread
    String zzJy() {
        zzmq();
        try {
            return com.google.firebase.iid.zzc.zzaab().getId();
        } catch (IllegalStateException e) {
            zzJt().zzLc().log("Failed to retrieve Firebase Instance Id");
            return null;
        }
    }

    @WorkerThread
    String zzLj() {
        zzLi().nextBytes(new byte[16]);
        return String.format(Locale.US, "%032x", new Object[]{new BigInteger(1, r0)});
    }

    @WorkerThread
    long zzLk() {
        zznA();
        zzmq();
        long j = this.zzbsk.get();
        if (j != 0) {
            return j;
        }
        j = (long) (zzLi().nextInt(86400000) + 1);
        this.zzbsk.set(j);
        return j;
    }

    @WorkerThread
    String zzLm() {
        zzmq();
        return zzLl().getString("gmp_app_id", null);
    }

    @WorkerThread
    Boolean zzLn() {
        zzmq();
        return !zzLl().contains("use_service") ? null : Boolean.valueOf(zzLl().getBoolean("use_service", false));
    }

    @WorkerThread
    void zzLo() {
        boolean z = true;
        zzmq();
        zzJt().zzLg().log("Clearing collection preferences.");
        boolean contains = zzLl().contains("measurement_enabled");
        if (contains) {
            z = zzaG(true);
        }
        Editor edit = zzLl().edit();
        edit.clear();
        edit.apply();
        if (contains) {
            setMeasurementEnabled(z);
        }
    }

    @WorkerThread
    protected String zzLp() {
        zzmq();
        String string = zzLl().getString("previous_os_version", null);
        String zzKU = zzJk().zzKU();
        if (!(TextUtils.isEmpty(zzKU) || zzKU.equals(string))) {
            Editor edit = zzLl().edit();
            edit.putString("previous_os_version", zzKU);
            edit.apply();
        }
        return string;
    }

    @WorkerThread
    void zzaF(boolean z) {
        zzmq();
        zzJt().zzLg().zzj("Setting useService", Boolean.valueOf(z));
        Editor edit = zzLl().edit();
        edit.putBoolean("use_service", z);
        edit.apply();
    }

    @WorkerThread
    boolean zzaG(boolean z) {
        zzmq();
        return zzLl().getBoolean("measurement_enabled", z);
    }

    @WorkerThread
    @NonNull
    Pair<String, Boolean> zzfK(String str) {
        zzmq();
        long elapsedRealtime = zznq().elapsedRealtime();
        if (this.zzbsl != null && elapsedRealtime < this.zzbsn) {
            return new Pair(this.zzbsl, Boolean.valueOf(this.zzbsm));
        }
        this.zzbsn = elapsedRealtime + zzJv().zzfq(str);
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(true);
        try {
            Info advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(getContext());
            this.zzbsl = advertisingIdInfo.getId();
            if (this.zzbsl == null) {
                this.zzbsl = "";
            }
            this.zzbsm = advertisingIdInfo.isLimitAdTrackingEnabled();
        } catch (Throwable th) {
            zzJt().zzLf().zzj("Unable to get advertising id", th);
            this.zzbsl = "";
        }
        AdvertisingIdClient.setShouldSkipGmsCoreVersionCheck(false);
        return new Pair(this.zzbsl, Boolean.valueOf(this.zzbsm));
    }

    @WorkerThread
    String zzfL(String str) {
        zzmq();
        String str2 = (String) zzfK(str).first;
        if (zzaue.zzcg("MD5") == null) {
            return null;
        }
        return String.format(Locale.US, "%032X", new Object[]{new BigInteger(1, zzaue.zzcg("MD5").digest(str2.getBytes()))});
    }

    @WorkerThread
    void zzfM(String str) {
        zzmq();
        Editor edit = zzLl().edit();
        edit.putString("gmp_app_id", str);
        edit.apply();
    }

    protected void zzmr() {
        this.zzafC = getContext().getSharedPreferences("com.google.android.gms.measurement.prefs", 0);
        this.zzbsu = this.zzafC.getBoolean("has_been_opened", false);
        if (!this.zzbsu) {
            Editor edit = this.zzafC.edit();
            edit.putBoolean("has_been_opened", true);
            edit.apply();
        }
    }
}
