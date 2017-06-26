package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzbo;

public final class zzciv {
    private final Context mContext;
    private final zzciy zzbum;

    public zzciv(zzciy com_google_android_gms_internal_zzciy) {
        this.mContext = com_google_android_gms_internal_zzciy.getContext();
        zzbo.zzu(this.mContext);
        this.zzbum = com_google_android_gms_internal_zzciy;
    }

    private final void zza(Integer num, JobParameters jobParameters) {
        zzcgk zzbj = zzcgk.zzbj(this.mContext);
        zzbj.zzwE().zzj(new zzciw(this, zzbj, num, zzbj.zzwF(), jobParameters));
    }

    public static boolean zzk(Context context, boolean z) {
        zzbo.zzu(context);
        return VERSION.SDK_INT >= 24 ? zzcjk.zzw(context, "com.google.android.gms.measurement.AppMeasurementJobService") : zzcjk.zzw(context, "com.google.android.gms.measurement.AppMeasurementService");
    }

    private final zzcfk zzwF() {
        return zzcgk.zzbj(this.mContext).zzwF();
    }

    @MainThread
    public final IBinder onBind(Intent intent) {
        if (intent == null) {
            zzwF().zzyx().log("onBind called with null intent");
            return null;
        }
        String action = intent.getAction();
        if ("com.google.android.gms.measurement.START".equals(action)) {
            return new zzcgp(zzcgk.zzbj(this.mContext));
        }
        zzwF().zzyz().zzj("onBind received unknown action", action);
        return null;
    }

    @MainThread
    public final void onCreate() {
        zzcfk zzwF = zzcgk.zzbj(this.mContext).zzwF();
        zzcel.zzxE();
        zzwF.zzyD().log("Local AppMeasurementService is starting up");
    }

    @MainThread
    public final void onDestroy() {
        zzcfk zzwF = zzcgk.zzbj(this.mContext).zzwF();
        zzcel.zzxE();
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
        zzcfk zzwF = zzcgk.zzbj(this.mContext).zzwF();
        if (intent == null) {
            zzwF.zzyz().log("AppMeasurementService started with null intent");
        } else {
            String action = intent.getAction();
            zzcel.zzxE();
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
        zzcfk zzwF = zzcgk.zzbj(this.mContext).zzwF();
        String string = jobParameters.getExtras().getString("action");
        zzcel.zzxE();
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
