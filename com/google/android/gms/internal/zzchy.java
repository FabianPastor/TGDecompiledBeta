package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.zza;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class zzchy extends zzchi {
    protected zzcib zzbtE;
    private volatile zzb zzbtF;
    private zzb zzbtG;
    private long zzbtH;
    private final Map<Activity, zzcib> zzbtI = new ArrayMap();
    private final CopyOnWriteArrayList<zza> zzbtJ = new CopyOnWriteArrayList();
    private boolean zzbtK;
    private zzb zzbtL;
    private String zzbtM;

    public zzchy(zzcgk com_google_android_gms_internal_zzcgk) {
        super(com_google_android_gms_internal_zzcgk);
    }

    @MainThread
    private final void zza(Activity activity, zzcib com_google_android_gms_internal_zzcib, boolean z) {
        boolean hasNext;
        boolean z2 = true;
        zzb com_google_android_gms_measurement_AppMeasurement_zzb = this.zzbtF != null ? this.zzbtF : (this.zzbtG == null || Math.abs(super.zzkq().elapsedRealtime() - this.zzbtH) >= 1000) ? null : this.zzbtG;
        com_google_android_gms_measurement_AppMeasurement_zzb = com_google_android_gms_measurement_AppMeasurement_zzb != null ? new zzb(com_google_android_gms_measurement_AppMeasurement_zzb) : null;
        this.zzbtK = true;
        try {
            Iterator it = this.zzbtJ.iterator();
            while (true) {
                hasNext = it.hasNext();
                if (!hasNext) {
                    break;
                }
                try {
                    z2 &= ((zza) it.next()).zza(com_google_android_gms_measurement_AppMeasurement_zzb, com_google_android_gms_internal_zzcib);
                } catch (Exception e) {
                    super.zzwF().zzyx().zzj("onScreenChangeCallback threw exception", e);
                }
            }
            hasNext = z2;
        } catch (Exception e2) {
            Exception exception = e2;
            hasNext = z2;
            z2 = super.zzwF().zzyx();
            z2.zzj("onScreenChangeCallback loop threw exception", exception);
        } finally {
            this.zzbtK = false;
        }
        com_google_android_gms_measurement_AppMeasurement_zzb = this.zzbtF == null ? this.zzbtG : this.zzbtF;
        if (hasNext) {
            if (com_google_android_gms_internal_zzcib.zzbok == null) {
                com_google_android_gms_internal_zzcib.zzbok = zzen(activity.getClass().getCanonicalName());
            }
            zzb com_google_android_gms_internal_zzcib2 = new zzcib(com_google_android_gms_internal_zzcib);
            this.zzbtG = this.zzbtF;
            this.zzbtH = super.zzkq().elapsedRealtime();
            this.zzbtF = com_google_android_gms_internal_zzcib2;
            super.zzwE().zzj(new zzchz(this, z, com_google_android_gms_measurement_AppMeasurement_zzb, com_google_android_gms_internal_zzcib2));
        }
    }

    @WorkerThread
    private final void zza(@NonNull zzcib com_google_android_gms_internal_zzcib) {
        super.zzwr().zzJ(super.zzkq().elapsedRealtime());
        if (super.zzwD().zzap(com_google_android_gms_internal_zzcib.zzbtS)) {
            com_google_android_gms_internal_zzcib.zzbtS = false;
        }
    }

    public static void zza(zzb com_google_android_gms_measurement_AppMeasurement_zzb, Bundle bundle) {
        if (bundle != null && com_google_android_gms_measurement_AppMeasurement_zzb != null && !bundle.containsKey("_sc")) {
            if (com_google_android_gms_measurement_AppMeasurement_zzb.zzboj != null) {
                bundle.putString("_sn", com_google_android_gms_measurement_AppMeasurement_zzb.zzboj);
            }
            bundle.putString("_sc", com_google_android_gms_measurement_AppMeasurement_zzb.zzbok);
            bundle.putLong("_si", com_google_android_gms_measurement_AppMeasurement_zzb.zzbol);
        }
    }

    private static String zzen(String str) {
        String[] split = str.split("\\.");
        if (split.length == 0) {
            return str.substring(0, 36);
        }
        String str2 = split[split.length - 1];
        return str2.length() > 36 ? str2.substring(0, 36) : str2;
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @MainThread
    public final void onActivityDestroyed(Activity activity) {
        this.zzbtI.remove(activity);
    }

    @MainThread
    public final void onActivityPaused(Activity activity) {
        zzcib zzq = zzq(activity);
        this.zzbtG = this.zzbtF;
        this.zzbtH = super.zzkq().elapsedRealtime();
        this.zzbtF = null;
        super.zzwE().zzj(new zzcia(this, zzq));
    }

    @MainThread
    public final void onActivityResumed(Activity activity) {
        zza(activity, zzq(activity), false);
        super.zzwr().zzwn();
    }

    @MainThread
    public final void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        if (bundle != null) {
            zzcib com_google_android_gms_internal_zzcib = (zzcib) this.zzbtI.get(activity);
            if (com_google_android_gms_internal_zzcib != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong(TtmlNode.ATTR_ID, com_google_android_gms_internal_zzcib.zzbol);
                bundle2.putString("name", com_google_android_gms_internal_zzcib.zzboj);
                bundle2.putString("referrer_name", com_google_android_gms_internal_zzcib.zzbok);
                bundle.putBundle("com.google.firebase.analytics.screen_service", bundle2);
            }
        }
    }

    @MainThread
    public final void registerOnScreenChangeCallback(@NonNull zza com_google_android_gms_measurement_AppMeasurement_zza) {
        super.zzwp();
        if (com_google_android_gms_measurement_AppMeasurement_zza == null) {
            super.zzwF().zzyz().log("Attempting to register null OnScreenChangeCallback");
            return;
        }
        this.zzbtJ.remove(com_google_android_gms_measurement_AppMeasurement_zza);
        this.zzbtJ.add(com_google_android_gms_measurement_AppMeasurement_zza);
    }

    @MainThread
    public final void setCurrentScreen(@NonNull Activity activity, @Nullable @Size(max = 36, min = 1) String str, @Nullable @Size(max = 36, min = 1) String str2) {
        if (activity == null) {
            super.zzwF().zzyz().log("setCurrentScreen must be called with a non-null activity");
            return;
        }
        super.zzwE();
        if (!zzcgf.zzS()) {
            super.zzwF().zzyz().log("setCurrentScreen must be called from the main thread");
        } else if (this.zzbtK) {
            super.zzwF().zzyz().log("Cannot call setCurrentScreen from onScreenChangeCallback");
        } else if (this.zzbtF == null) {
            super.zzwF().zzyz().log("setCurrentScreen cannot be called while no activity active");
        } else if (this.zzbtI.get(activity) == null) {
            super.zzwF().zzyz().log("setCurrentScreen must be called with an activity in the activity lifecycle");
        } else {
            if (str2 == null) {
                str2 = zzen(activity.getClass().getCanonicalName());
            }
            boolean equals = this.zzbtF.zzbok.equals(str2);
            boolean zzR = zzcjk.zzR(this.zzbtF.zzboj, str);
            if (equals && zzR) {
                super.zzwF().zzyA().log("setCurrentScreen cannot be called with the same class and name");
            } else if (str != null && (str.length() <= 0 || str.length() > zzcel.zzxk())) {
                super.zzwF().zzyz().zzj("Invalid screen name length in setCurrentScreen. Length", Integer.valueOf(str.length()));
            } else if (str2 == null || (str2.length() > 0 && str2.length() <= zzcel.zzxk())) {
                Object obj;
                zzcfm zzyD = super.zzwF().zzyD();
                String str3 = "Setting current screen to name, class";
                if (str == null) {
                    obj = "null";
                } else {
                    String str4 = str;
                }
                zzyD.zze(str3, obj, str2);
                zzcib com_google_android_gms_internal_zzcib = new zzcib(str, str2, super.zzwB().zzzs());
                this.zzbtI.put(activity, com_google_android_gms_internal_zzcib);
                zza(activity, com_google_android_gms_internal_zzcib, true);
            } else {
                super.zzwF().zzyz().zzj("Invalid class name length in setCurrentScreen. Length", Integer.valueOf(str2.length()));
            }
        }
    }

    @MainThread
    public final void unregisterOnScreenChangeCallback(@NonNull zza com_google_android_gms_measurement_AppMeasurement_zza) {
        super.zzwp();
        this.zzbtJ.remove(com_google_android_gms_measurement_AppMeasurement_zza);
    }

    @WorkerThread
    public final void zza(String str, zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        super.zzjC();
        synchronized (this) {
            if (this.zzbtM == null || this.zzbtM.equals(str) || com_google_android_gms_measurement_AppMeasurement_zzb != null) {
                this.zzbtM = str;
                this.zzbtL = com_google_android_gms_measurement_AppMeasurement_zzb;
            }
        }
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    @MainThread
    final zzcib zzq(@NonNull Activity activity) {
        zzbo.zzu(activity);
        zzcib com_google_android_gms_internal_zzcib = (zzcib) this.zzbtI.get(activity);
        if (com_google_android_gms_internal_zzcib != null) {
            return com_google_android_gms_internal_zzcib;
        }
        com_google_android_gms_internal_zzcib = new zzcib(null, zzen(activity.getClass().getCanonicalName()), super.zzwB().zzzs());
        this.zzbtI.put(activity, com_google_android_gms_internal_zzcib);
        return com_google_android_gms_internal_zzcib;
    }

    public final /* bridge */ /* synthetic */ zzcfi zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjk zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcge zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzciz zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfk zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfv zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcel zzwH() {
        return super.zzwH();
    }

    public final /* bridge */ /* synthetic */ void zzwo() {
        super.zzwo();
    }

    public final /* bridge */ /* synthetic */ void zzwp() {
        super.zzwp();
    }

    public final /* bridge */ /* synthetic */ void zzwq() {
        super.zzwq();
    }

    public final /* bridge */ /* synthetic */ zzceb zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcei zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcff zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzces zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcic zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchy zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwz() {
        return super.zzwz();
    }

    @WorkerThread
    public final zzcib zzzh() {
        zzkD();
        super.zzjC();
        return this.zzbtE;
    }

    public final zzb zzzi() {
        super.zzwp();
        zzb com_google_android_gms_measurement_AppMeasurement_zzb = this.zzbtF;
        return com_google_android_gms_measurement_AppMeasurement_zzb == null ? null : new zzb(com_google_android_gms_measurement_AppMeasurement_zzb);
    }
}