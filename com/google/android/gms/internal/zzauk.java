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
    protected zza zzbvp;
    private volatile zzf zzbvq;
    private zzf zzbvr;
    private long zzbvs;
    private final Map<Activity, zza> zzbvt = new ArrayMap();
    private final CopyOnWriteArrayList<zzd> zzbvu = new CopyOnWriteArrayList();
    private boolean zzbvv;
    private zzf zzbvw;
    private String zzbvx;

    static class zza extends zzf {
        public boolean zzbvC;

        public zza(zza com_google_android_gms_internal_zzauk_zza) {
            this.zzbqe = com_google_android_gms_internal_zzauk_zza.zzbqe;
            this.zzbqf = com_google_android_gms_internal_zzauk_zza.zzbqf;
            this.zzbqg = com_google_android_gms_internal_zzauk_zza.zzbqg;
            this.zzbvC = com_google_android_gms_internal_zzauk_zza.zzbvC;
        }

        public zza(String str, String str2, long j) {
            this.zzbqe = str;
            this.zzbqf = str2;
            this.zzbqg = j;
            this.zzbvC = false;
        }
    }

    public zzauk(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    @MainThread
    private void zza(Activity activity, zza com_google_android_gms_internal_zzauk_zza, final boolean z) {
        int i = 1;
        zzf com_google_android_gms_measurement_AppMeasurement_zzf = this.zzbvq != null ? this.zzbvq : (this.zzbvr == null || Math.abs(zznR().elapsedRealtime() - this.zzbvs) >= 1000) ? null : this.zzbvr;
        com_google_android_gms_measurement_AppMeasurement_zzf = com_google_android_gms_measurement_AppMeasurement_zzf != null ? new zzf(com_google_android_gms_measurement_AppMeasurement_zzf) : null;
        this.zzbvv = true;
        try {
            Iterator it = this.zzbvu.iterator();
            while (it.hasNext()) {
                int zza;
                try {
                    zza = ((zzd) it.next()).zza(com_google_android_gms_measurement_AppMeasurement_zzf, com_google_android_gms_internal_zzauk_zza) & i;
                } catch (Exception e) {
                    zzKl().zzLZ().zzj("onScreenChangeCallback threw exception", e);
                    zza = i;
                }
                i = zza;
            }
        } catch (Exception e2) {
            zzKl().zzLZ().zzj("onScreenChangeCallback loop threw exception", e2);
        } finally {
            this.zzbvv = false;
        }
        if (i != 0) {
            if (com_google_android_gms_internal_zzauk_zza.zzbqf == null) {
                com_google_android_gms_internal_zzauk_zza.zzbqf = zzfS(activity.getClass().getCanonicalName());
            }
            final zzf com_google_android_gms_internal_zzauk_zza2 = new zza(com_google_android_gms_internal_zzauk_zza);
            this.zzbvr = this.zzbvq;
            this.zzbvs = zznR().elapsedRealtime();
            this.zzbvq = com_google_android_gms_internal_zzauk_zza2;
            zzKk().zzm(new Runnable(this) {
                final /* synthetic */ zzauk zzbvA;

                public void run() {
                    if (z && this.zzbvA.zzbvp != null) {
                        this.zzbvA.zza(this.zzbvA.zzbvp);
                    }
                    this.zzbvA.zzbvp = com_google_android_gms_internal_zzauk_zza2;
                    this.zzbvA.zzKd().zza(com_google_android_gms_internal_zzauk_zza2);
                }
            });
        }
    }

    @WorkerThread
    private void zza(@NonNull zza com_google_android_gms_internal_zzauk_zza) {
        zzJY().zzW(zznR().elapsedRealtime());
        if (zzKj().zzaN(com_google_android_gms_internal_zzauk_zza.zzbvC)) {
            com_google_android_gms_internal_zzauk_zza.zzbvC = false;
        }
    }

    public static void zza(zzf com_google_android_gms_measurement_AppMeasurement_zzf, Bundle bundle) {
        if (bundle != null && com_google_android_gms_measurement_AppMeasurement_zzf != null && !bundle.containsKey("_sc")) {
            if (com_google_android_gms_measurement_AppMeasurement_zzf.zzbqe != null) {
                bundle.putString("_sn", com_google_android_gms_measurement_AppMeasurement_zzf.zzbqe);
            }
            bundle.putString("_sc", com_google_android_gms_measurement_AppMeasurement_zzf.zzbqf);
            bundle.putLong("_si", com_google_android_gms_measurement_AppMeasurement_zzf.zzbqg);
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
                zzv.zzbqg = bundle2.getLong(TtmlNode.ATTR_ID);
                zzv.zzbqe = bundle2.getString("name");
                zzv.zzbqf = bundle2.getString("referrer_name");
            }
        }
    }

    @MainThread
    public void onActivityDestroyed(Activity activity) {
        this.zzbvt.remove(activity);
    }

    @MainThread
    public void onActivityPaused(Activity activity) {
        final zza zzv = zzv(activity);
        this.zzbvr = this.zzbvq;
        this.zzbvs = zznR().elapsedRealtime();
        this.zzbvq = null;
        zzKk().zzm(new Runnable(this) {
            final /* synthetic */ zzauk zzbvA;

            public void run() {
                this.zzbvA.zza(zzv);
                this.zzbvA.zzbvp = null;
                this.zzbvA.zzKd().zza(null);
            }
        });
    }

    @MainThread
    public void onActivityResumed(Activity activity) {
        zza(activity, zzv(activity), false);
        zzJY().zzJU();
    }

    @MainThread
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        if (bundle != null) {
            zza com_google_android_gms_internal_zzauk_zza = (zza) this.zzbvt.get(activity);
            if (com_google_android_gms_internal_zzauk_zza != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong(TtmlNode.ATTR_ID, com_google_android_gms_internal_zzauk_zza.zzbqg);
                bundle2.putString("name", com_google_android_gms_internal_zzauk_zza.zzbqe);
                bundle2.putString("referrer_name", com_google_android_gms_internal_zzauk_zza.zzbqf);
                bundle.putBundle("com.google.firebase.analytics.screen_service", bundle2);
            }
        }
    }

    @MainThread
    public void registerOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        zzJW();
        if (com_google_android_gms_measurement_AppMeasurement_zzd == null) {
            zzKl().zzMb().log("Attempting to register null OnScreenChangeCallback");
            return;
        }
        this.zzbvu.remove(com_google_android_gms_measurement_AppMeasurement_zzd);
        this.zzbvu.add(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    @MainThread
    public void setCurrentScreen(@NonNull Activity activity, @Nullable @Size(max = 36, min = 1) String str, @Nullable @Size(max = 36, min = 1) String str2) {
        int i = VERSION.SDK_INT;
        if (activity == null) {
            zzKl().zzMb().log("setCurrentScreen must be called with a non-null activity");
        } else if (!zzKk().zzbc()) {
            zzKl().zzMb().log("setCurrentScreen must be called from the main thread");
        } else if (this.zzbvv) {
            zzKl().zzMb().log("Cannot call setCurrentScreen from onScreenChangeCallback");
        } else if (this.zzbvq == null) {
            zzKl().zzMb().log("setCurrentScreen cannot be called while no activity active");
        } else if (this.zzbvt.get(activity) == null) {
            zzKl().zzMb().log("setCurrentScreen must be called with an activity in the activity lifecycle");
        } else {
            if (str2 == null) {
                str2 = zzfS(activity.getClass().getCanonicalName());
            }
            boolean equals = this.zzbvq.zzbqf.equals(str2);
            boolean z = (this.zzbvq.zzbqe == null && str == null) || (this.zzbvq.zzbqe != null && this.zzbvq.zzbqe.equals(str));
            if (equals && z) {
                zzKl().zzMc().log("setCurrentScreen cannot be called with the same class and name");
            } else if (str != null && (str.length() < 1 || str.length() > zzKn().zzKP())) {
                zzKl().zzMb().zzj("Invalid screen name length in setCurrentScreen. Length", Integer.valueOf(str.length()));
            } else if (str2 == null || (str2.length() >= 1 && str2.length() <= zzKn().zzKP())) {
                Object obj;
                com.google.android.gms.internal.zzatx.zza zzMf = zzKl().zzMf();
                String str3 = "Setting current screen to name, class";
                if (str == null) {
                    obj = "null";
                } else {
                    String str4 = str;
                }
                zzMf.zze(str3, obj, str2);
                zza com_google_android_gms_internal_zzauk_zza = new zza(str, str2, zzKh().zzNk());
                this.zzbvt.put(activity, com_google_android_gms_internal_zzauk_zza);
                zza(activity, com_google_android_gms_internal_zzauk_zza, true);
            } else {
                zzKl().zzMb().zzj("Invalid class name length in setCurrentScreen. Length", Integer.valueOf(str2.length()));
            }
        }
    }

    @MainThread
    public void unregisterOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        zzJW();
        this.zzbvu.remove(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    public /* bridge */ /* synthetic */ void zzJV() {
        super.zzJV();
    }

    public /* bridge */ /* synthetic */ void zzJW() {
        super.zzJW();
    }

    public /* bridge */ /* synthetic */ void zzJX() {
        super.zzJX();
    }

    public /* bridge */ /* synthetic */ zzatb zzJY() {
        return super.zzJY();
    }

    public /* bridge */ /* synthetic */ zzatf zzJZ() {
        return super.zzJZ();
    }

    public /* bridge */ /* synthetic */ zzauj zzKa() {
        return super.zzKa();
    }

    public /* bridge */ /* synthetic */ zzatu zzKb() {
        return super.zzKb();
    }

    public /* bridge */ /* synthetic */ zzatl zzKc() {
        return super.zzKc();
    }

    public /* bridge */ /* synthetic */ zzaul zzKd() {
        return super.zzKd();
    }

    public /* bridge */ /* synthetic */ zzauk zzKe() {
        return super.zzKe();
    }

    public /* bridge */ /* synthetic */ zzatv zzKf() {
        return super.zzKf();
    }

    public /* bridge */ /* synthetic */ zzatj zzKg() {
        return super.zzKg();
    }

    public /* bridge */ /* synthetic */ zzaut zzKh() {
        return super.zzKh();
    }

    public /* bridge */ /* synthetic */ zzauc zzKi() {
        return super.zzKi();
    }

    public /* bridge */ /* synthetic */ zzaun zzKj() {
        return super.zzKj();
    }

    public /* bridge */ /* synthetic */ zzaud zzKk() {
        return super.zzKk();
    }

    public /* bridge */ /* synthetic */ zzatx zzKl() {
        return super.zzKl();
    }

    public /* bridge */ /* synthetic */ zzaua zzKm() {
        return super.zzKm();
    }

    public /* bridge */ /* synthetic */ zzati zzKn() {
        return super.zzKn();
    }

    @WorkerThread
    public zza zzMW() {
        zzob();
        zzmR();
        return this.zzbvp;
    }

    public zzf zzMX() {
        zzJW();
        zzf com_google_android_gms_measurement_AppMeasurement_zzf = this.zzbvq;
        return com_google_android_gms_measurement_AppMeasurement_zzf == null ? null : new zzf(com_google_android_gms_measurement_AppMeasurement_zzf);
    }

    @WorkerThread
    public void zza(String str, zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        zzmR();
        synchronized (this) {
            if (this.zzbvx == null || this.zzbvx.equals(str) || com_google_android_gms_measurement_AppMeasurement_zzf != null) {
                this.zzbvx = str;
                this.zzbvw = com_google_android_gms_measurement_AppMeasurement_zzf;
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
        zza com_google_android_gms_internal_zzauk_zza = (zza) this.zzbvt.get(activity);
        if (com_google_android_gms_internal_zzauk_zza != null) {
            return com_google_android_gms_internal_zzauk_zza;
        }
        com_google_android_gms_internal_zzauk_zza = new zza(null, zzfS(activity.getClass().getCanonicalName()), zzKh().zzNk());
        this.zzbvt.put(activity, com_google_android_gms_internal_zzauk_zza);
        return com_google_android_gms_internal_zzauk_zza;
    }
}
