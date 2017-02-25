package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.zzd;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class zzauk extends zzauh {
    protected zza zzbvq;
    private volatile zzf zzbvr;
    private zzf zzbvs;
    private long zzbvt;
    private final Map<Activity, zza> zzbvu = new ArrayMap();
    private final CopyOnWriteArrayList<zzd> zzbvv = new CopyOnWriteArrayList();
    private boolean zzbvw;
    private zzf zzbvx;
    private String zzbvy;

    static class zza extends zzf {
        public boolean zzbvD;

        public zza(zza com_google_android_gms_internal_zzauk_zza) {
            this.zzbqj = com_google_android_gms_internal_zzauk_zza.zzbqj;
            this.zzbqk = com_google_android_gms_internal_zzauk_zza.zzbqk;
            this.zzbql = com_google_android_gms_internal_zzauk_zza.zzbql;
            this.zzbvD = com_google_android_gms_internal_zzauk_zza.zzbvD;
        }

        public zza(String str, String str2, long j) {
            this.zzbqj = str;
            this.zzbqk = str2;
            this.zzbql = j;
            this.zzbvD = false;
        }
    }

    public zzauk(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    @MainThread
    private void zza(Activity activity, zza com_google_android_gms_internal_zzauk_zza, final boolean z) {
        int i = 1;
        zzf com_google_android_gms_measurement_AppMeasurement_zzf = this.zzbvr != null ? this.zzbvr : (this.zzbvs == null || Math.abs(zznR().elapsedRealtime() - this.zzbvt) >= 1000) ? null : this.zzbvs;
        com_google_android_gms_measurement_AppMeasurement_zzf = com_google_android_gms_measurement_AppMeasurement_zzf != null ? new zzf(com_google_android_gms_measurement_AppMeasurement_zzf) : null;
        this.zzbvw = true;
        try {
            Iterator it = this.zzbvv.iterator();
            while (it.hasNext()) {
                int zza;
                try {
                    zza = ((zzd) it.next()).zza(com_google_android_gms_measurement_AppMeasurement_zzf, com_google_android_gms_internal_zzauk_zza) & i;
                } catch (Exception e) {
                    zzKk().zzLX().zzj("onScreenChangeCallback threw exception", e);
                    zza = i;
                }
                i = zza;
            }
        } catch (Exception e2) {
            zzKk().zzLX().zzj("onScreenChangeCallback loop threw exception", e2);
        } finally {
            this.zzbvw = false;
        }
        if (i != 0) {
            if (com_google_android_gms_internal_zzauk_zza.zzbqk == null) {
                com_google_android_gms_internal_zzauk_zza.zzbqk = zzfS(activity.getClass().getCanonicalName());
            }
            final zzf com_google_android_gms_internal_zzauk_zza2 = new zza(com_google_android_gms_internal_zzauk_zza);
            this.zzbvs = this.zzbvr;
            this.zzbvt = zznR().elapsedRealtime();
            this.zzbvr = com_google_android_gms_internal_zzauk_zza2;
            zzKj().zzm(new Runnable(this) {
                final /* synthetic */ zzauk zzbvB;

                public void run() {
                    if (z && this.zzbvB.zzbvq != null) {
                        this.zzbvB.zza(this.zzbvB.zzbvq);
                    }
                    this.zzbvB.zzbvq = com_google_android_gms_internal_zzauk_zza2;
                    this.zzbvB.zzKc().zza(com_google_android_gms_internal_zzauk_zza2);
                }
            });
        }
    }

    @WorkerThread
    private void zza(@NonNull zza com_google_android_gms_internal_zzauk_zza) {
        zzJX().zzW(zznR().elapsedRealtime());
        if (zzKi().zzaO(com_google_android_gms_internal_zzauk_zza.zzbvD)) {
            com_google_android_gms_internal_zzauk_zza.zzbvD = false;
        }
    }

    public static void zza(zzf com_google_android_gms_measurement_AppMeasurement_zzf, Bundle bundle) {
        if (bundle != null && com_google_android_gms_measurement_AppMeasurement_zzf != null && !bundle.containsKey("_sc")) {
            if (com_google_android_gms_measurement_AppMeasurement_zzf.zzbqj != null) {
                bundle.putString("_sn", com_google_android_gms_measurement_AppMeasurement_zzf.zzbqj);
            }
            bundle.putString("_sc", com_google_android_gms_measurement_AppMeasurement_zzf.zzbqk);
            bundle.putLong("_si", com_google_android_gms_measurement_AppMeasurement_zzf.zzbql);
        }
    }

    static String zzfS(String str) {
        String[] split = str.split("\\.");
        if (split.length == 0) {
            return str.substring(0, 36);
        }
        String str2 = split[split.length - 1];
        return str2.length() > 36 ? str2.substring(0, 36) : str2;
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @MainThread
    public void onActivityCreated(Activity activity, Bundle bundle) {
        if (bundle != null) {
            Bundle bundle2 = bundle.getBundle("com.google.firebase.analytics.screen_service");
            if (bundle2 != null) {
                zza zzv = zzv(activity);
                zzv.zzbql = bundle2.getLong(TtmlNode.ATTR_ID);
                zzv.zzbqj = bundle2.getString("name");
                zzv.zzbqk = bundle2.getString("referrer_name");
            }
        }
    }

    @MainThread
    public void onActivityDestroyed(Activity activity) {
        this.zzbvu.remove(activity);
    }

    @MainThread
    public void onActivityPaused(Activity activity) {
        final zza zzv = zzv(activity);
        this.zzbvs = this.zzbvr;
        this.zzbvt = zznR().elapsedRealtime();
        this.zzbvr = null;
        zzKj().zzm(new Runnable(this) {
            final /* synthetic */ zzauk zzbvB;

            public void run() {
                this.zzbvB.zza(zzv);
                this.zzbvB.zzbvq = null;
                this.zzbvB.zzKc().zza(null);
            }
        });
    }

    @MainThread
    public void onActivityResumed(Activity activity) {
        zza(activity, zzv(activity), false);
        zzJX().zzJT();
    }

    @MainThread
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        if (bundle != null) {
            zza com_google_android_gms_internal_zzauk_zza = (zza) this.zzbvu.get(activity);
            if (com_google_android_gms_internal_zzauk_zza != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong(TtmlNode.ATTR_ID, com_google_android_gms_internal_zzauk_zza.zzbql);
                bundle2.putString("name", com_google_android_gms_internal_zzauk_zza.zzbqj);
                bundle2.putString("referrer_name", com_google_android_gms_internal_zzauk_zza.zzbqk);
                bundle.putBundle("com.google.firebase.analytics.screen_service", bundle2);
            }
        }
    }

    @MainThread
    public void registerOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        zzJV();
        if (com_google_android_gms_measurement_AppMeasurement_zzd == null) {
            zzKk().zzLZ().log("Attempting to register null OnScreenChangeCallback");
            return;
        }
        this.zzbvv.remove(com_google_android_gms_measurement_AppMeasurement_zzd);
        this.zzbvv.add(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    @MainThread
    public void setCurrentScreen(@NonNull Activity activity, @Nullable @Size(max = 36, min = 1) String str, @Nullable @Size(max = 36, min = 1) String str2) {
        int i = VERSION.SDK_INT;
        if (activity == null) {
            zzKk().zzLZ().log("setCurrentScreen must be called with a non-null activity");
        } else if (!zzKj().zzbc()) {
            zzKk().zzLZ().log("setCurrentScreen must be called from the main thread");
        } else if (this.zzbvw) {
            zzKk().zzLZ().log("Cannot call setCurrentScreen from onScreenChangeCallback");
        } else if (this.zzbvr == null) {
            zzKk().zzLZ().log("setCurrentScreen cannot be called while no activity active");
        } else if (this.zzbvu.get(activity) == null) {
            zzKk().zzLZ().log("setCurrentScreen must be called with an activity in the activity lifecycle");
        } else {
            if (str2 == null) {
                str2 = zzfS(activity.getClass().getCanonicalName());
            }
            boolean equals = this.zzbvr.zzbqk.equals(str2);
            boolean z = (this.zzbvr.zzbqj == null && str == null) || (this.zzbvr.zzbqj != null && this.zzbvr.zzbqj.equals(str));
            if (equals && z) {
                zzKk().zzMa().log("setCurrentScreen cannot be called with the same class and name");
            } else if (str != null && (str.length() < 1 || str.length() > zzKm().zzKO())) {
                zzKk().zzLZ().zzj("Invalid screen name length in setCurrentScreen. Length", Integer.valueOf(str.length()));
            } else if (str2 == null || (str2.length() >= 1 && str2.length() <= zzKm().zzKO())) {
                Object obj;
                com.google.android.gms.internal.zzatx.zza zzMd = zzKk().zzMd();
                String str3 = "Setting current screen to name, class";
                if (str == null) {
                    obj = "null";
                } else {
                    String str4 = str;
                }
                zzMd.zze(str3, obj, str2);
                zza com_google_android_gms_internal_zzauk_zza = new zza(str, str2, zzKg().zzNh());
                this.zzbvu.put(activity, com_google_android_gms_internal_zzauk_zza);
                zza(activity, com_google_android_gms_internal_zzauk_zza, true);
            } else {
                zzKk().zzLZ().zzj("Invalid class name length in setCurrentScreen. Length", Integer.valueOf(str2.length()));
            }
        }
    }

    @MainThread
    public void unregisterOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        zzJV();
        this.zzbvv.remove(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    public /* bridge */ /* synthetic */ void zzJU() {
        super.zzJU();
    }

    public /* bridge */ /* synthetic */ void zzJV() {
        super.zzJV();
    }

    public /* bridge */ /* synthetic */ void zzJW() {
        super.zzJW();
    }

    public /* bridge */ /* synthetic */ zzatb zzJX() {
        return super.zzJX();
    }

    public /* bridge */ /* synthetic */ zzatf zzJY() {
        return super.zzJY();
    }

    public /* bridge */ /* synthetic */ zzauj zzJZ() {
        return super.zzJZ();
    }

    public /* bridge */ /* synthetic */ zzatu zzKa() {
        return super.zzKa();
    }

    public /* bridge */ /* synthetic */ zzatl zzKb() {
        return super.zzKb();
    }

    public /* bridge */ /* synthetic */ zzaul zzKc() {
        return super.zzKc();
    }

    public /* bridge */ /* synthetic */ zzauk zzKd() {
        return super.zzKd();
    }

    public /* bridge */ /* synthetic */ zzatv zzKe() {
        return super.zzKe();
    }

    public /* bridge */ /* synthetic */ zzatj zzKf() {
        return super.zzKf();
    }

    public /* bridge */ /* synthetic */ zzaut zzKg() {
        return super.zzKg();
    }

    public /* bridge */ /* synthetic */ zzauc zzKh() {
        return super.zzKh();
    }

    public /* bridge */ /* synthetic */ zzaun zzKi() {
        return super.zzKi();
    }

    public /* bridge */ /* synthetic */ zzaud zzKj() {
        return super.zzKj();
    }

    public /* bridge */ /* synthetic */ zzatx zzKk() {
        return super.zzKk();
    }

    public /* bridge */ /* synthetic */ zzaua zzKl() {
        return super.zzKl();
    }

    public /* bridge */ /* synthetic */ zzati zzKm() {
        return super.zzKm();
    }

    @WorkerThread
    public zza zzMT() {
        zzob();
        zzmR();
        return this.zzbvq;
    }

    public zzf zzMU() {
        zzJV();
        zzf com_google_android_gms_measurement_AppMeasurement_zzf = this.zzbvr;
        return com_google_android_gms_measurement_AppMeasurement_zzf == null ? null : new zzf(com_google_android_gms_measurement_AppMeasurement_zzf);
    }

    @WorkerThread
    public void zza(String str, zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        zzmR();
        synchronized (this) {
            if (this.zzbvy == null || this.zzbvy.equals(str) || com_google_android_gms_measurement_AppMeasurement_zzf != null) {
                this.zzbvy = str;
                this.zzbvx = com_google_android_gms_measurement_AppMeasurement_zzf;
            }
        }
    }

    public /* bridge */ /* synthetic */ void zzmR() {
        super.zzmR();
    }

    protected void zzmS() {
    }

    public /* bridge */ /* synthetic */ zze zznR() {
        return super.zznR();
    }

    @MainThread
    zza zzv(@NonNull Activity activity) {
        zzac.zzw(activity);
        zza com_google_android_gms_internal_zzauk_zza = (zza) this.zzbvu.get(activity);
        if (com_google_android_gms_internal_zzauk_zza != null) {
            return com_google_android_gms_internal_zzauk_zza;
        }
        com_google_android_gms_internal_zzauk_zza = new zza(null, zzfS(activity.getClass().getCanonicalName()), zzKg().zzNh());
        this.zzbvu.put(activity, com_google_android_gms_internal_zzauk_zza);
        return com_google_android_gms_internal_zzauk_zza;
    }
}
