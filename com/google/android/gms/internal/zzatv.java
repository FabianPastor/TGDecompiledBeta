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

public class zzatv extends zzats {
    protected zza zzbul;
    private volatile zzf zzbum;
    private zzf zzbun;
    private long zzbuo;
    private final Map<Activity, zza> zzbup = new ArrayMap();
    private final CopyOnWriteArrayList<zzd> zzbuq = new CopyOnWriteArrayList();
    private boolean zzbur;
    private zzf zzbus;
    private String zzbut;

    static class zza extends zzf {
        public boolean zzbuy;

        public zza(zza com_google_android_gms_internal_zzatv_zza) {
            this.zzbpz = com_google_android_gms_internal_zzatv_zza.zzbpz;
            this.zzbpA = com_google_android_gms_internal_zzatv_zza.zzbpA;
            this.zzbpB = com_google_android_gms_internal_zzatv_zza.zzbpB;
            this.zzbuy = com_google_android_gms_internal_zzatv_zza.zzbuy;
        }

        public zza(String str, String str2, long j) {
            this.zzbpz = str;
            this.zzbpA = str2;
            this.zzbpB = j;
            this.zzbuy = false;
        }
    }

    public zzatv(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
    }

    @MainThread
    private void zza(Activity activity, zza com_google_android_gms_internal_zzatv_zza, final boolean z) {
        int i = 1;
        zzf com_google_android_gms_measurement_AppMeasurement_zzf = this.zzbum != null ? this.zzbum : (this.zzbun == null || Math.abs(zznq().elapsedRealtime() - this.zzbuo) >= 1000) ? null : this.zzbun;
        com_google_android_gms_measurement_AppMeasurement_zzf = com_google_android_gms_measurement_AppMeasurement_zzf != null ? new zzf(com_google_android_gms_measurement_AppMeasurement_zzf) : null;
        this.zzbur = true;
        try {
            Iterator it = this.zzbuq.iterator();
            while (it.hasNext()) {
                int zza;
                try {
                    zza = ((zzd) it.next()).zza(com_google_android_gms_measurement_AppMeasurement_zzf, com_google_android_gms_internal_zzatv_zza) & i;
                } catch (Exception e) {
                    zzJt().zzLa().zzj("onScreenChangeCallback threw exception", e);
                    zza = i;
                }
                i = zza;
            }
        } catch (Exception e2) {
            zzJt().zzLa().zzj("onScreenChangeCallback loop threw exception", e2);
        } finally {
            this.zzbur = false;
        }
        if (i != 0) {
            if (com_google_android_gms_internal_zzatv_zza.zzbpA == null) {
                com_google_android_gms_internal_zzatv_zza.zzbpA = zzfV(activity.getClass().getCanonicalName());
            }
            final zzf com_google_android_gms_internal_zzatv_zza2 = new zza(com_google_android_gms_internal_zzatv_zza);
            this.zzbun = this.zzbum;
            this.zzbuo = zznq().elapsedRealtime();
            this.zzbum = com_google_android_gms_internal_zzatv_zza2;
            zzJs().zzm(new Runnable(this) {
                final /* synthetic */ zzatv zzbuw;

                public void run() {
                    if (z && this.zzbuw.zzbul != null) {
                        this.zzbuw.zza(this.zzbuw.zzbul);
                    }
                    this.zzbuw.zzbul = com_google_android_gms_internal_zzatv_zza2;
                    this.zzbuw.zzJl().zza(com_google_android_gms_internal_zzatv_zza2);
                }
            });
        }
    }

    @WorkerThread
    private void zza(@NonNull zza com_google_android_gms_internal_zzatv_zza) {
        zzJg().zzV(zznq().elapsedRealtime());
        if (zzJr().zzaJ(com_google_android_gms_internal_zzatv_zza.zzbuy)) {
            com_google_android_gms_internal_zzatv_zza.zzbuy = false;
        }
    }

    public static void zza(zzf com_google_android_gms_measurement_AppMeasurement_zzf, Bundle bundle) {
        if (bundle != null && com_google_android_gms_measurement_AppMeasurement_zzf != null && !bundle.containsKey("_sc")) {
            if (com_google_android_gms_measurement_AppMeasurement_zzf.zzbpz != null) {
                bundle.putString("_sn", com_google_android_gms_measurement_AppMeasurement_zzf.zzbpz);
            }
            bundle.putString("_sc", com_google_android_gms_measurement_AppMeasurement_zzf.zzbpA);
            bundle.putLong("_si", com_google_android_gms_measurement_AppMeasurement_zzf.zzbpB);
        }
    }

    static String zzfV(String str) {
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
                zzv.zzbpB = bundle2.getLong(TtmlNode.ATTR_ID);
                zzv.zzbpz = bundle2.getString("name");
                zzv.zzbpA = bundle2.getString("referrer_name");
            }
        }
    }

    @MainThread
    public void onActivityDestroyed(Activity activity) {
        this.zzbup.remove(activity);
    }

    @MainThread
    public void onActivityPaused(Activity activity) {
        final zza zzv = zzv(activity);
        this.zzbun = this.zzbum;
        this.zzbuo = zznq().elapsedRealtime();
        this.zzbum = null;
        zzJs().zzm(new Runnable(this) {
            final /* synthetic */ zzatv zzbuw;

            public void run() {
                this.zzbuw.zza(zzv);
                this.zzbuw.zzbul = null;
                this.zzbuw.zzJl().zza(null);
            }
        });
    }

    @MainThread
    public void onActivityResumed(Activity activity) {
        zza(activity, zzv(activity), false);
        zzJg().zzJc();
    }

    @MainThread
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        if (bundle != null) {
            zza com_google_android_gms_internal_zzatv_zza = (zza) this.zzbup.get(activity);
            if (com_google_android_gms_internal_zzatv_zza != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong(TtmlNode.ATTR_ID, com_google_android_gms_internal_zzatv_zza.zzbpB);
                bundle2.putString("name", com_google_android_gms_internal_zzatv_zza.zzbpz);
                bundle2.putString("referrer_name", com_google_android_gms_internal_zzatv_zza.zzbpA);
                bundle.putBundle("com.google.firebase.analytics.screen_service", bundle2);
            }
        }
    }

    @MainThread
    public void registerOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        zzJe();
        if (com_google_android_gms_measurement_AppMeasurement_zzd == null) {
            zzJt().zzLc().log("Attempting to register null OnScreenChangeCallback");
            return;
        }
        this.zzbuq.remove(com_google_android_gms_measurement_AppMeasurement_zzd);
        this.zzbuq.add(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    @MainThread
    public void setCurrentScreen(@NonNull Activity activity, @Nullable @Size(max = 36, min = 1) String str, @Nullable @Size(max = 36, min = 1) String str2) {
        if (VERSION.SDK_INT < 14) {
            zzJt().zzLd().log("Screen engagement recording is only available at API level 14+");
        } else if (activity == null) {
            zzJt().zzLc().log("setCurrentScreen must be called with a non-null activity");
        } else if (!zzJs().zzbd()) {
            zzJt().zzLc().log("setCurrentScreen must be called from the main thread");
        } else if (this.zzbur) {
            zzJt().zzLc().log("Cannot call setCurrentScreen from onScreenChangeCallback");
        } else if (this.zzbum == null) {
            zzJt().zzLc().log("setCurrentScreen cannot be called while no activity active");
        } else if (this.zzbup.get(activity) == null) {
            zzJt().zzLc().log("setCurrentScreen must be called with an activity in the activity lifecycle");
        } else {
            if (str2 == null) {
                str2 = zzfV(activity.getClass().getCanonicalName());
            }
            boolean equals = this.zzbum.zzbpA.equals(str2);
            boolean z = (this.zzbum.zzbpz == null && str == null) || (this.zzbum.zzbpz != null && this.zzbum.zzbpz.equals(str));
            if (equals && z) {
                zzJt().zzLd().log("setCurrentScreen cannot be called with the same class and name");
            } else if (str != null && (str.length() < 1 || str.length() > zzJv().zzJX())) {
                zzJt().zzLc().zzj("Invalid screen name length in setCurrentScreen. Length", Integer.valueOf(str.length()));
            } else if (str2 == null || (str2.length() >= 1 && str2.length() <= zzJv().zzJX())) {
                Object obj;
                com.google.android.gms.internal.zzati.zza zzLg = zzJt().zzLg();
                String str3 = "Setting current screen to name, class";
                if (str == null) {
                    obj = "null";
                } else {
                    String str4 = str;
                }
                zzLg.zze(str3, obj, str2);
                zza com_google_android_gms_internal_zzatv_zza = new zza(str, str2, zzJp().zzMi());
                this.zzbup.put(activity, com_google_android_gms_internal_zzatv_zza);
                zza(activity, com_google_android_gms_internal_zzatv_zza, true);
            } else {
                zzJt().zzLc().zzj("Invalid class name length in setCurrentScreen. Length", Integer.valueOf(str2.length()));
            }
        }
    }

    @MainThread
    public void unregisterOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        zzJe();
        this.zzbuq.remove(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    public /* bridge */ /* synthetic */ void zzJd() {
        super.zzJd();
    }

    public /* bridge */ /* synthetic */ void zzJe() {
        super.zzJe();
    }

    public /* bridge */ /* synthetic */ void zzJf() {
        super.zzJf();
    }

    public /* bridge */ /* synthetic */ zzaso zzJg() {
        return super.zzJg();
    }

    public /* bridge */ /* synthetic */ zzass zzJh() {
        return super.zzJh();
    }

    public /* bridge */ /* synthetic */ zzatu zzJi() {
        return super.zzJi();
    }

    public /* bridge */ /* synthetic */ zzatf zzJj() {
        return super.zzJj();
    }

    public /* bridge */ /* synthetic */ zzasw zzJk() {
        return super.zzJk();
    }

    public /* bridge */ /* synthetic */ zzatw zzJl() {
        return super.zzJl();
    }

    public /* bridge */ /* synthetic */ zzatv zzJm() {
        return super.zzJm();
    }

    public /* bridge */ /* synthetic */ zzatg zzJn() {
        return super.zzJn();
    }

    public /* bridge */ /* synthetic */ zzasu zzJo() {
        return super.zzJo();
    }

    public /* bridge */ /* synthetic */ zzaue zzJp() {
        return super.zzJp();
    }

    public /* bridge */ /* synthetic */ zzatn zzJq() {
        return super.zzJq();
    }

    public /* bridge */ /* synthetic */ zzaty zzJr() {
        return super.zzJr();
    }

    public /* bridge */ /* synthetic */ zzato zzJs() {
        return super.zzJs();
    }

    public /* bridge */ /* synthetic */ zzati zzJt() {
        return super.zzJt();
    }

    public /* bridge */ /* synthetic */ zzatl zzJu() {
        return super.zzJu();
    }

    public /* bridge */ /* synthetic */ zzast zzJv() {
        return super.zzJv();
    }

    @WorkerThread
    public zza zzLU() {
        zznA();
        zzmq();
        return this.zzbul;
    }

    public zzf zzLV() {
        zzJe();
        zzf com_google_android_gms_measurement_AppMeasurement_zzf = this.zzbum;
        return com_google_android_gms_measurement_AppMeasurement_zzf == null ? null : new zzf(com_google_android_gms_measurement_AppMeasurement_zzf);
    }

    @WorkerThread
    public void zza(String str, zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        zzmq();
        synchronized (this) {
            if (this.zzbut == null || this.zzbut.equals(str) || com_google_android_gms_measurement_AppMeasurement_zzf != null) {
                this.zzbut = str;
                this.zzbus = com_google_android_gms_measurement_AppMeasurement_zzf;
            }
        }
    }

    public zzf zzfU(String str) {
        zzf com_google_android_gms_measurement_AppMeasurement_zzf;
        synchronized (this) {
            if (this.zzbus == null || this.zzbut == null || !this.zzbut.equals(str)) {
                com_google_android_gms_measurement_AppMeasurement_zzf = null;
            } else {
                com_google_android_gms_measurement_AppMeasurement_zzf = this.zzbus;
            }
        }
        return com_google_android_gms_measurement_AppMeasurement_zzf;
    }

    public /* bridge */ /* synthetic */ void zzmq() {
        super.zzmq();
    }

    protected void zzmr() {
    }

    public /* bridge */ /* synthetic */ zze zznq() {
        return super.zznq();
    }

    @MainThread
    zza zzv(@NonNull Activity activity) {
        zzac.zzw(activity);
        zza com_google_android_gms_internal_zzatv_zza = (zza) this.zzbup.get(activity);
        if (com_google_android_gms_internal_zzatv_zza != null) {
            return com_google_android_gms_internal_zzatv_zza;
        }
        com_google_android_gms_internal_zzatv_zza = new zza(null, zzfV(activity.getClass().getCanonicalName()), zzJp().zzMi());
        this.zzbup.put(activity, com_google_android_gms_internal_zzatv_zza);
        return com_google_android_gms_internal_zzatv_zza;
    }
}
