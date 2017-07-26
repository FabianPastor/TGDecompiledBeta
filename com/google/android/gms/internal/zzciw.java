package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzbo;

public final class zzciw {
    private final Context mContext;
    private final zzciz zzbum;

    public zzciw(zzciz com_google_android_gms_internal_zzciz) {
        this.mContext = com_google_android_gms_internal_zzciz.getContext();
        zzbo.zzu(this.mContext);
        this.zzbum = com_google_android_gms_internal_zzciz;
    }

    private final void zza(Integer num, JobParameters jobParameters) {
        zzcgl zzbj = zzcgl.zzbj(this.mContext);
        zzbj.zzwE().zzj(new zzcix(this, zzbj, num, zzbj.zzwF(), jobParameters));
    }

    public static boolean zzk(Context context, boolean z) {
        zzbo.zzu(context);
        return VERSION.SDK_INT >= 24 ? zzcjl.zzw(context, "com.google.android.gms.measurement.AppMeasurementJobService") : zzcjl.zzw(context, "com.google.android.gms.measurement.AppMeasurementService");
    }

    private final zzcfl zzwF() {
        return zzcgl.zzbj(this.mContext).zzwF();
    }

    @MainThread
    public final IBinder onBind(Intent intent) {
        if (intent == null) {
            zzwF().zzyx().log("onBind called with null intent");
            return null;
        }
        String action = intent.getAction();
        if ("com.google.android.gms.measurement.START".equals(action)) {
            return new zzcgq(zzcgl.zzbj(this.mContext));
        }
        zzwF().zzyz().zzj("onBind received unknown action", action);
        return null;
    }

    @MainThread
    public final void onCreate() {
        zzcfl zzwF = zzcgl.zzbj(this.mContext).zzwF();
        zzcem.zzxE();
        zzwF.zzyD().log("Local AppMeasurementService is starting up");
    }

    @MainThread
    public final void onDestroy() {
        zzcfl zzwF = zzcgl.zzbj(this.mContext).zzwF();
        zzcem.zzxE();
        zzwF.zzyD().log("Local AppMeasurementService is shutting down");
    }

    @MainThread
    public final void onRebind(Intent intent) {
        if (intent == null) {
            zzwF().zzyx().log("onRebind called with null intent");
            return;
        }
        zzwF().zzyD().zzj("onRebind called. action", intent.getAction());
    }

    @MainThread
    public final int onStartCommand(Intent intent, int i, int i2) {
        zzcfl zzwF = zzcgl.zzbj(this.mContext).zzwF();
        if (intent == null) {
            zzwF.zzyz().log("AppMeasurementService started with null intent");
        } else {
            String action = intent.getAction();
            zzcem.zzxE();
            zzwF.zzyD().zze("Local AppMeasurementService called. startId, action", Integer.valueOf(i2), action);
            if ("com.google.android.gms.measurement.UPLOAD".equals(action)) {
                zza(Integer.valueOf(i2), null);
            }
        }
        return 2;
    }

    @TargetApi(24)
    @MainThread
    public final boolean onStartJob(JobParameters jobParameters) {
        zzcfl zzwF = zzcgl.zzbj(this.mContext).zzwF();
        String string = jobParameters.getExtras().getString("action");
        zzcem.zzxE();
        zzwF.zzyD().zzj("Local AppMeasurementJobService called. action", string);
        if ("com.google.android.gms.measurement.UPLOAD".equals(string)) {
            zza(null, jobParameters);
        }
        return true;
    }

    @MainThread
    public final boolean onUnbind(Intent intent) {
        if (intent == null) {
            zzwF().zzyx().log("onUnbind called with null intent");
        } else {
            zzwF().zzyD().zzj("onUnbind called for intent. action", intent.getAction());
        }
        return true;
    }
}
