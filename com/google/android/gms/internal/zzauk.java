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
    protected zza zzbvm;
    private volatile zzf zzbvn;
    private zzf zzbvo;
    private long zzbvp;
    private final Map<Activity, zza> zzbvq = new ArrayMap();
    private final CopyOnWriteArrayList<zzd> zzbvr = new CopyOnWriteArrayList();
    private boolean zzbvs;
    private zzf zzbvt;
    private String zzbvu;

    static class zza extends zzf {
        public boolean zzbvz;

        public zza(zza com_google_android_gms_internal_zzauk_zza) {
            this.zzbqf = com_google_android_gms_internal_zzauk_zza.zzbqf;
            this.zzbqg = com_google_android_gms_internal_zzauk_zza.zzbqg;
            this.zzbqh = com_google_android_gms_internal_zzauk_zza.zzbqh;
            this.zzbvz = com_google_android_gms_internal_zzauk_zza.zzbvz;
        }

        public zza(String str, String str2, long j) {
            this.zzbqf = str;
            this.zzbqg = str2;
            this.zzbqh = j;
            this.zzbvz = false;
        }
    }

    public zzauk(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    @MainThread
    private void zza(Activity activity, zza com_google_android_gms_internal_zzauk_zza, final boolean z) {
        int i = 1;
        zzf com_google_android_gms_measurement_AppMeasurement_zzf = this.zzbvn != null ? this.zzbvn : (this.zzbvo == null || Math.abs(zznR().elapsedRealtime() - this.zzbvp) >= 1000) ? null : this.zzbvo;
        com_google_android_gms_measurement_AppMeasurement_zzf = com_google_android_gms_measurement_AppMeasurement_zzf != null ? new zzf(com_google_android_gms_measurement_AppMeasurement_zzf) : null;
        this.zzbvs = true;
        try {
            Iterator it = this.zzbvr.iterator();
            while (it.hasNext()) {
                int zza;
                try {
                    zza = ((zzd) it.next()).zza(com_google_android_gms_measurement_AppMeasurement_zzf, com_google_android_gms_internal_zzauk_zza) & i;
                } catch (Exception e) {
                    zzKl().zzLY().zzj("onScreenChangeCallback threw exception", e);
                    zza = i;
                }
                i = zza;
            }
        } catch (Exception e2) {
            zzKl().zzLY().zzj("onScreenChangeCallback loop threw exception", e2);
        } finally {
            this.zzbvs = false;
        }
        if (i != 0) {
            if (com_google_android_gms_internal_zzauk_zza.zzbqg == null) {
                com_google_android_gms_internal_zzauk_zza.zzbqg = zzfS(activity.getClass().getCanonicalName());
            }
            final zzf com_google_android_gms_internal_zzauk_zza2 = new zza(com_google_android_gms_internal_zzauk_zza);
            this.zzbvo = this.zzbvn;
            this.zzbvp = zznR().elapsedRealtime();
            this.zzbvn = com_google_android_gms_internal_zzauk_zza2;
            zzKk().zzm(new Runnable(this) {
                final /* synthetic */ zzauk zzbvx;

                public void run() {
                    if (z && this.zzbvx.zzbvm != null) {
                        this.zzbvx.zza(this.zzbvx.zzbvm);
                    }
                    this.zzbvx.zzbvm = com_google_android_gms_internal_zzauk_zza2;
                    this.zzbvx.zzKd().zza(com_google_android_gms_internal_zzauk_zza2);
                }
            });
        }
    }

    @WorkerThread
    private void zza(@NonNull zza com_google_android_gms_internal_zzauk_zza) {
        zzJY().zzW(zznR().elapsedRealtime());
        if (zzKj().zzaO(com_google_android_gms_internal_zzauk_zza.zzbvz)) {
            com_google_android_gms_internal_zzauk_zza.zzbvz = false;
        }
    }

    public static void zza(zzf com_google_android_gms_measurement_AppMeasurement_zzf, Bundle bundle) {
        if (bundle != null && com_google_android_gms_measurement_AppMeasurement_zzf != null && !bundle.containsKey("_sc")) {
            if (com_google_android_gms_measurement_AppMeasurement_zzf.zzbqf != null) {
                bundle.putString("_sn", com_google_android_gms_measurement_AppMeasurement_zzf.zzbqf);
            }
            bundle.putString("_sc", com_google_android_gms_measurement_AppMeasurement_zzf.zzbqg);
            bundle.putLong("_si", com_google_android_gms_measurement_AppMeasurement_zzf.zzbqh);
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
                zzv.zzbqh = bundle2.getLong(TtmlNode.ATTR_ID);
                zzv.zzbqf = bundle2.getString("name");
                zzv.zzbqg = bundle2.getString("referrer_name");
            }
        }
    }

    @MainThread
    public void onActivityDestroyed(Activity activity) {
        this.zzbvq.remove(activity);
    }

    @MainThread
    public void onActivityPaused(Activity activity) {
        final zza zzv = zzv(activity);
        this.zzbvo = this.zzbvn;
        this.zzbvp = zznR().elapsedRealtime();
        this.zzbvn = null;
        zzKk().zzm(new Runnable(this) {
            final /* synthetic */ zzauk zzbvx;

            public void run() {
                this.zzbvx.zza(zzv);
                this.zzbvx.zzbvm = null;
                this.zzbvx.zzKd().zza(null);
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
            zza com_google_android_gms_internal_zzauk_zza = (zza) this.zzbvq.get(activity);
            if (com_google_android_gms_internal_zzauk_zza != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong(TtmlNode.ATTR_ID, com_google_android_gms_internal_zzauk_zza.zzbqh);
                bundle2.putString("name", com_google_android_gms_internal_zzauk_zza.zzbqf);
                bundle2.putString("referrer_name", com_google_android_gms_internal_zzauk_zza.zzbqg);
                bundle.putBundle("com.google.firebase.analytics.screen_service", bundle2);
            }
        }
    }

    @MainThread
    public void registerOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        zzJW();
        if (com_google_android_gms_measurement_AppMeasurement_zzd == null) {
            zzKl().zzMa().log("Attempting to register null OnScreenChangeCallback");
            return;
        }
        this.zzbvr.remove(com_google_android_gms_measurement_AppMeasurement_zzd);
        this.zzbvr.add(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    @MainThread
    public void setCurrentScreen(@NonNull Activity activity, @Nullable @Size(max = 36, min = 1) String str, @Nullable @Size(max = 36, min = 1) String str2) {
        int i = VERSION.SDK_INT;
        if (activity == null) {
            zzKl().zzMa().log("setCurrentScreen must be called with a non-null activity");
        } else if (!zzKk().zzbc()) {
            zzKl().zzMa().log("setCurrentScreen must be called from the main thread");
        } else if (this.zzbvs) {
            zzKl().zzMa().log("Cannot call setCurrentScreen from onScreenChangeCallback");
        } else if (this.zzbvn == null) {
            zzKl().zzMa().log("setCurrentScreen cannot be called while no activity active");
        } else if (this.zzbvq.get(activity) == null) {
            zzKl().zzMa().log("setCurrentScreen must be called with an activity in the activity lifecycle");
        } else {
            if (str2 == null) {
                str2 = zzfS(activity.getClass().getCanonicalName());
            }
            boolean equals = this.zzbvn.zzbqg.equals(str2);
            boolean z = (this.zzbvn.zzbqf == null && str == null) || (this.zzbvn.zzbqf != null && this.zzbvn.zzbqf.equals(str));
            if (equals && z) {
                zzKl().zzMb().log("setCurrentScreen cannot be called with the same class and name");
            } else if (str != null && (str.length() < 1 || str.length() > zzKn().zzKP())) {
                zzKl().zzMa().zzj("Invalid screen name length in setCurrentScreen. Length", Integer.valueOf(str.length()));
            } else if (str2 == null || (str2.length() >= 1 && str2.length() <= zzKn().zzKP())) {
                Object obj;
                com.google.android.gms.internal.zzatx.zza zzMe = zzKl().zzMe();
                String str3 = "Setting current screen to name, class";
                if (str == null) {
                    obj = "null";
                } else {
                    String str4 = str;
                }
                zzMe.zze(str3, obj, str2);
                zza com_google_android_gms_internal_zzauk_zza = new zza(str, str2, zzKh().zzNi());
                this.zzbvq.put(activity, com_google_android_gms_internal_zzauk_zza);
                zza(activity, com_google_android_gms_internal_zzauk_zza, true);
            } else {
                zzKl().zzMa().zzj("Invalid class name length in setCurrentScreen. Length", Integer.valueOf(str2.length()));
            }
        }
    }

    @MainThread
    public void unregisterOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        zzJW();
        this.zzbvr.remove(com_google_android_gms_measurement_AppMeasurement_zzd);
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
    public zza zzMU() {
        zzob();
        zzmR();
        return this.zzbvm;
    }

    public zzf zzMV() {
        zzJW();
        zzf com_google_android_gms_measurement_AppMeasurement_zzf = this.zzbvn;
        return com_google_android_gms_measurement_AppMeasurement_zzf == null ? null : new zzf(com_google_android_gms_measurement_AppMeasurement_zzf);
    }

    @WorkerThread
    public void zza(String str, zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        zzmR();
        synchronized (this) {
            if (this.zzbvu == null || this.zzbvu.equals(str) || com_google_android_gms_measurement_AppMeasurement_zzf != null) {
                this.zzbvu = str;
                this.zzbvt = com_google_android_gms_measurement_AppMeasurement_zzf;
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
        zza com_google_android_gms_internal_zzauk_zza = (zza) this.zzbvq.get(activity);
        if (com_google_android_gms_internal_zzauk_zza != null) {
            return com_google_android_gms_internal_zzauk_zza;
        }
        com_google_android_gms_internal_zzauk_zza = new zza(null, zzfS(activity.getClass().getCanonicalName()), zzKh().zzNi());
        this.zzbvq.put(activity, com_google_android_gms_internal_zzauk_zza);
        return com_google_android_gms_internal_zzauk_zza;
    }
}
