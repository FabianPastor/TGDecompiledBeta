package com.google.android.gms.measurement.internal;

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
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.zzd;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class zzad extends zzaa {
    protected zza avd;
    private zzf ave;
    private zzf avf;
    private long avg;
    private final Map<Activity, zza> avh = new ArrayMap();
    private final CopyOnWriteArrayList<zzd> avi = new CopyOnWriteArrayList();
    private boolean avj;
    private final AtomicLong avk = new AtomicLong(0);
    private zzf avl;
    private String avm;

    static class zza extends zzf {
        public boolean avr;

        public zza(zza com_google_android_gms_measurement_internal_zzad_zza) {
            this.aqz = com_google_android_gms_measurement_internal_zzad_zza.aqz;
            this.aqA = com_google_android_gms_measurement_internal_zzad_zza.aqA;
            this.aqB = com_google_android_gms_measurement_internal_zzad_zza.aqB;
            this.avr = com_google_android_gms_measurement_internal_zzad_zza.avr;
        }

        public zza(String str, String str2, long j) {
            this.aqz = str;
            this.aqA = str2;
            this.aqB = j;
            this.avr = false;
        }
    }

    public zzad(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    @MainThread
    private void zza(Activity activity, zza com_google_android_gms_measurement_internal_zzad_zza, final boolean z) {
        int i = 1;
        zzf com_google_android_gms_measurement_AppMeasurement_zzf = this.ave != null ? this.ave : (this.avf == null || Math.abs(zzabz().elapsedRealtime() - this.avg) >= 1000) ? null : this.avf;
        com_google_android_gms_measurement_AppMeasurement_zzf = com_google_android_gms_measurement_AppMeasurement_zzf != null ? new zzf(com_google_android_gms_measurement_AppMeasurement_zzf) : null;
        this.avj = true;
        try {
            Iterator it = this.avi.iterator();
            while (it.hasNext()) {
                int zza;
                try {
                    zza = ((zzd) it.next()).zza(com_google_android_gms_measurement_AppMeasurement_zzf, com_google_android_gms_measurement_internal_zzad_zza) & i;
                } catch (Exception e) {
                    zzbwb().zzbwy().zzj("onScreenChangeCallback threw exception", e);
                    zza = i;
                }
                i = zza;
            }
        } catch (Exception e2) {
            zzbwb().zzbwy().zzj("onScreenChangeCallback loop threw exception", e2);
        } finally {
            this.avj = false;
        }
        if (i != 0) {
            if (com_google_android_gms_measurement_internal_zzad_zza.aqA == null) {
                com_google_android_gms_measurement_internal_zzad_zza.aqA = zzmt(activity.getClass().getCanonicalName());
            }
            final zzf com_google_android_gms_measurement_internal_zzad_zza2 = new zza(com_google_android_gms_measurement_internal_zzad_zza);
            this.avf = this.ave;
            this.avg = zzabz().elapsedRealtime();
            this.ave = com_google_android_gms_measurement_internal_zzad_zza2;
            zzbwa().zzm(new Runnable(this) {
                final /* synthetic */ zzad avp;

                public void run() {
                    if (z && this.avp.avd != null) {
                        this.avp.zza(this.avp.avd);
                    }
                    this.avp.avd = com_google_android_gms_measurement_internal_zzad_zza2;
                    this.avp.zzbvt().zza(com_google_android_gms_measurement_internal_zzad_zza2);
                }
            });
        }
    }

    public static void zza(zzf com_google_android_gms_measurement_AppMeasurement_zzf, Bundle bundle) {
        if (bundle != null && com_google_android_gms_measurement_AppMeasurement_zzf != null && !bundle.containsKey("_sc")) {
            if (com_google_android_gms_measurement_AppMeasurement_zzf.aqz != null) {
                bundle.putString("_sn", com_google_android_gms_measurement_AppMeasurement_zzf.aqz);
            }
            bundle.putString("_sc", com_google_android_gms_measurement_AppMeasurement_zzf.aqA);
            bundle.putLong("_si", com_google_android_gms_measurement_AppMeasurement_zzf.aqB);
        }
    }

    @WorkerThread
    private void zza(@NonNull zza com_google_android_gms_measurement_internal_zzad_zza) {
        if (zzbvz().zzck(com_google_android_gms_measurement_internal_zzad_zza.avr)) {
            com_google_android_gms_measurement_internal_zzad_zza.avr = false;
        }
    }

    @MainThread
    private long zzbyu() {
        long andIncrement = this.avk.getAndIncrement();
        if (andIncrement == 0) {
            this.avk.compareAndSet(1, 0);
            return new Random(System.nanoTime() ^ zzabz().currentTimeMillis()).nextLong();
        }
        this.avk.compareAndSet(0, 1);
        return andIncrement;
    }

    static String zzmt(String str) {
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
                zzv.aqB = bundle2.getLong(TtmlNode.ATTR_ID);
                zzv.aqz = bundle2.getString("name");
                zzv.aqA = bundle2.getString("referrer_name");
            }
        }
    }

    @MainThread
    public void onActivityDestroyed(Activity activity) {
        this.avh.remove(activity);
    }

    @MainThread
    public void onActivityPaused(Activity activity) {
        final zza zzv = zzv(activity);
        this.avf = this.ave;
        this.avg = zzabz().elapsedRealtime();
        this.ave = null;
        zzbwa().zzm(new Runnable(this) {
            final /* synthetic */ zzad avp;

            public void run() {
                this.avp.zza(zzv);
                this.avp.avd = null;
                this.avp.zzbvt().zza(null);
            }
        });
    }

    @MainThread
    public void onActivityResumed(Activity activity) {
        zza(activity, zzv(activity), false);
    }

    @MainThread
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        if (bundle != null) {
            zza com_google_android_gms_measurement_internal_zzad_zza = (zza) this.avh.get(activity);
            if (com_google_android_gms_measurement_internal_zzad_zza != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong(TtmlNode.ATTR_ID, com_google_android_gms_measurement_internal_zzad_zza.aqB);
                bundle2.putString("name", com_google_android_gms_measurement_internal_zzad_zza.aqz);
                bundle2.putString("referrer_name", com_google_android_gms_measurement_internal_zzad_zza.aqA);
                bundle.putBundle("com.google.firebase.analytics.screen_service", bundle2);
            }
        }
    }

    @MainThread
    public void registerOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        zzaby();
        if (com_google_android_gms_measurement_AppMeasurement_zzd == null) {
            zzbwb().zzbxa().log("Attempting to register null OnScreenChangeCallback");
            return;
        }
        this.avi.remove(com_google_android_gms_measurement_AppMeasurement_zzd);
        this.avi.add(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    @MainThread
    public void setCurrentScreen(@NonNull Activity activity, @Nullable @Size(max = 36, min = 1) String str, @Nullable @Size(max = 36, min = 1) String str2) {
        if (VERSION.SDK_INT < 14) {
            zzbwb().zzbxb().log("Screen engagement recording is only available at API level 14+");
        } else if (activity == null) {
            zzbwb().zzbxa().log("setCurrentScreen must be called with a non-null activity");
        } else if (!zzbwa().zzdg()) {
            zzbwb().zzbxa().log("setCurrentScreen must be called from the main thread");
        } else if (this.avj) {
            zzbwb().zzbxa().log("Cannot call setCurrentScreen from onScreenChangeCallback");
        } else if (this.ave == null) {
            zzbwb().zzbxa().log("setCurrentScreen cannot be called while no activity active");
        } else if (this.avh.get(activity) == null) {
            zzbwb().zzbxa().log("setCurrentScreen must be called with an activity in the activity lifecycle");
        } else {
            if (str2 == null) {
                str2 = zzmt(activity.getClass().getCanonicalName());
            }
            boolean equals = this.ave.aqA.equals(str2);
            boolean z = (this.ave.aqz == null && str == null) || (this.ave.aqz != null && this.ave.aqz.equals(str));
            if (equals && z) {
                zzbwb().zzbxb().log("setCurrentScreen cannot be called with the same class and name");
            } else if (str != null && (str.length() < 1 || str.length() > zzbwd().zzbug())) {
                zzbwb().zzbxa().zzj("Invalid screen name length in setCurrentScreen. Length", Integer.valueOf(str.length()));
            } else if (str2 == null || (str2.length() >= 1 && str2.length() <= zzbwd().zzbug())) {
                Object obj;
                com.google.android.gms.measurement.internal.zzq.zza zzbxe = zzbwb().zzbxe();
                String str3 = "Setting current screen to name, class";
                if (str == null) {
                    obj = "null";
                } else {
                    String str4 = str;
                }
                zzbxe.zze(str3, obj, str2);
                zza com_google_android_gms_measurement_internal_zzad_zza = new zza(str, str2, zzbyu());
                this.avh.put(activity, com_google_android_gms_measurement_internal_zzad_zza);
                zza(activity, com_google_android_gms_measurement_internal_zzad_zza, true);
            } else {
                zzbwb().zzbxa().zzj("Invalid class name length in setCurrentScreen. Length", Integer.valueOf(str2.length()));
            }
        }
    }

    @MainThread
    public void unregisterOnScreenChangeCallback(@NonNull zzd com_google_android_gms_measurement_AppMeasurement_zzd) {
        zzaby();
        this.avi.remove(com_google_android_gms_measurement_AppMeasurement_zzd);
    }

    @WorkerThread
    public void zza(String str, zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        zzzx();
        if (this.avm == null || this.avm.equals(str) || com_google_android_gms_measurement_AppMeasurement_zzf != null) {
            this.avm = str;
            this.avl = com_google_android_gms_measurement_AppMeasurement_zzf;
        }
    }

    public /* bridge */ /* synthetic */ void zzaby() {
        super.zzaby();
    }

    public /* bridge */ /* synthetic */ zze zzabz() {
        return super.zzabz();
    }

    public /* bridge */ /* synthetic */ void zzbvo() {
        super.zzbvo();
    }

    public /* bridge */ /* synthetic */ zzc zzbvp() {
        return super.zzbvp();
    }

    public /* bridge */ /* synthetic */ zzac zzbvq() {
        return super.zzbvq();
    }

    public /* bridge */ /* synthetic */ zzn zzbvr() {
        return super.zzbvr();
    }

    public /* bridge */ /* synthetic */ zzg zzbvs() {
        return super.zzbvs();
    }

    public /* bridge */ /* synthetic */ zzae zzbvt() {
        return super.zzbvt();
    }

    public /* bridge */ /* synthetic */ zzad zzbvu() {
        return super.zzbvu();
    }

    public /* bridge */ /* synthetic */ zzo zzbvv() {
        return super.zzbvv();
    }

    public /* bridge */ /* synthetic */ zze zzbvw() {
        return super.zzbvw();
    }

    public /* bridge */ /* synthetic */ zzal zzbvx() {
        return super.zzbvx();
    }

    public /* bridge */ /* synthetic */ zzv zzbvy() {
        return super.zzbvy();
    }

    public /* bridge */ /* synthetic */ zzag zzbvz() {
        return super.zzbvz();
    }

    public /* bridge */ /* synthetic */ zzw zzbwa() {
        return super.zzbwa();
    }

    public /* bridge */ /* synthetic */ zzq zzbwb() {
        return super.zzbwb();
    }

    public /* bridge */ /* synthetic */ zzt zzbwc() {
        return super.zzbwc();
    }

    public /* bridge */ /* synthetic */ zzd zzbwd() {
        return super.zzbwd();
    }

    @WorkerThread
    public zza zzbyt() {
        zzacj();
        zzzx();
        return this.avd;
    }

    @MainThread
    zza zzv(@NonNull Activity activity) {
        zzaa.zzy(activity);
        zza com_google_android_gms_measurement_internal_zzad_zza = (zza) this.avh.get(activity);
        if (com_google_android_gms_measurement_internal_zzad_zza != null) {
            return com_google_android_gms_measurement_internal_zzad_zza;
        }
        com_google_android_gms_measurement_internal_zzad_zza = new zza(null, zzmt(activity.getClass().getCanonicalName()), zzbyu());
        this.avh.put(activity, com_google_android_gms_measurement_internal_zzad_zza);
        return com_google_android_gms_measurement_internal_zzad_zza;
    }

    public /* bridge */ /* synthetic */ void zzzx() {
        super.zzzx();
    }

    protected void zzzy() {
        SecureRandom secureRandom = new SecureRandom();
        long nextLong = secureRandom.nextLong();
        if (nextLong == 0 || nextLong == 1) {
            nextLong = secureRandom.nextLong();
            if (nextLong == 0 || nextLong == 1) {
                zzbwb().zzbxa().log("ScreenService falling back to Random for screen instance id");
                nextLong = 0;
            }
        }
        this.avk.set(nextLong);
    }
}
