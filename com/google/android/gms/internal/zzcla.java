package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.IBinder;
import com.google.android.gms.common.internal.zzbq;

public final class zzcla<T extends Context & zzcle> {
    private final T zzdyu;

    public zzcla(T t) {
        zzbq.checkNotNull(t);
        this.zzdyu = t;
    }

    private final zzchm zzawy() {
        return zzcim.zzdx(this.zzdyu).zzawy();
    }

    private final void zzk(Runnable runnable) {
        zzcim zzdx = zzcim.zzdx(this.zzdyu);
        zzdx.zzawy();
        zzdx.zzawx().zzg(new zzcld(this, zzdx, runnable));
    }

    public static boolean zzk(Context context, boolean z) {
        zzbq.checkNotNull(context);
        return VERSION.SDK_INT >= 24 ? zzclq.zzt(context, "com.google.android.gms.measurement.AppMeasurementJobService") : zzclq.zzt(context, "com.google.android.gms.measurement.AppMeasurementService");
    }

    public final IBinder onBind(Intent intent) {
        if (intent == null) {
            zzawy().zzazd().log("onBind called with null intent");
            return null;
        }
        String action = intent.getAction();
        if ("com.google.android.gms.measurement.START".equals(action)) {
            return new zzcir(zzcim.zzdx(this.zzdyu));
        }
        zzawy().zzazf().zzj("onBind received unknown action", action);
        return null;
    }

    public final void onCreate() {
        zzcim.zzdx(this.zzdyu).zzawy().zzazj().log("Local AppMeasurementService is starting up");
    }

    public final void onDestroy() {
        zzcim.zzdx(this.zzdyu).zzawy().zzazj().log("Local AppMeasurementService is shutting down");
    }

    public final void onRebind(Intent intent) {
        if (intent == null) {
            zzawy().zzazd().log("onRebind called with null intent");
            return;
        }
        zzawy().zzazj().zzj("onRebind called. action", intent.getAction());
    }

    public final int onStartCommand(Intent intent, int i, int i2) {
        zzchm zzawy = zzcim.zzdx(this.zzdyu).zzawy();
        if (intent == null) {
            zzawy.zzazf().log("AppMeasurementService started with null intent");
        } else {
            String action = intent.getAction();
            zzawy.zzazj().zze("Local AppMeasurementService called. startId, action", Integer.valueOf(i2), action);
            if ("com.google.android.gms.measurement.UPLOAD".equals(action)) {
                zzk(new zzclb(this, i2, zzawy, intent));
            }
        }
        return 2;
    }

    @TargetApi(24)
    public final boolean onStartJob(JobParameters jobParameters) {
        zzchm zzawy = zzcim.zzdx(this.zzdyu).zzawy();
        String string = jobParameters.getExtras().getString("action");
        zzawy.zzazj().zzj("Local AppMeasurementJobService called. action", string);
        if ("com.google.android.gms.measurement.UPLOAD".equals(string)) {
            zzk(new zzclc(this, zzawy, jobParameters));
        }
        return true;
    }

    public final boolean onUnbind(Intent intent) {
        if (intent == null) {
            zzawy().zzazd().log("onUnbind called with null intent");
        } else {
            zzawy().zzazj().zzj("onUnbind called for intent. action", intent.getAction());
        }
        return true;
    }

    final /* synthetic */ void zza(int i, zzchm com_google_android_gms_internal_zzchm, Intent intent) {
        if (((zzcle) this.zzdyu).callServiceStopSelfResult(i)) {
            com_google_android_gms_internal_zzchm.zzazj().zzj("Local AppMeasurementService processed last upload request. StartId", Integer.valueOf(i));
            zzawy().zzazj().log("Completed wakeful intent.");
            ((zzcle) this.zzdyu).zzm(intent);
        }
    }

    final /* synthetic */ void zza(zzchm com_google_android_gms_internal_zzchm, JobParameters jobParameters) {
        com_google_android_gms_internal_zzchm.zzazj().log("AppMeasurementJobService processed last upload request.");
        ((zzcle) this.zzdyu).zza(jobParameters, false);
    }
}
