package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzac;

public final class zzaum {
    private final Context mContext;
    private final Handler mHandler = new Handler();
    private final zza zzbvW;

    public interface zza {
        boolean callServiceStopSelfResult(int i);

        Context getContext();
    }

    public zzaum(zza com_google_android_gms_internal_zzaum_zza) {
        this.mContext = com_google_android_gms_internal_zzaum_zza.getContext();
        zzac.zzw(this.mContext);
        this.zzbvW = com_google_android_gms_internal_zzaum_zza;
    }

    private zzatx zzKl() {
        return zzaue.zzbM(this.mContext).zzKl();
    }

    public static boolean zzj(Context context, boolean z) {
        zzac.zzw(context);
        return zzaut.zzy(context, z ? "com.google.android.gms.measurement.PackageMeasurementService" : "com.google.android.gms.measurement.AppMeasurementService");
    }

    @MainThread
    public IBinder onBind(Intent intent) {
        if (intent == null) {
            zzKl().zzLZ().log("onBind called with null intent");
            return null;
        }
        String action = intent.getAction();
        if ("com.google.android.gms.measurement.START".equals(action)) {
            return new zzauf(zzaue.zzbM(this.mContext));
        }
        zzKl().zzMb().zzj("onBind received unknown action", action);
        return null;
    }

    @MainThread
    public void onCreate() {
        zzaue zzbM = zzaue.zzbM(this.mContext);
        zzatx zzKl = zzbM.zzKl();
        zzbM.zzKn().zzLh();
        zzKl.zzMf().log("Local AppMeasurementService is starting up");
    }

    @MainThread
    public void onDestroy() {
        zzaue zzbM = zzaue.zzbM(this.mContext);
        zzatx zzKl = zzbM.zzKl();
        zzbM.zzKn().zzLh();
        zzKl.zzMf().log("Local AppMeasurementService is shutting down");
    }

    @MainThread
    public void onRebind(Intent intent) {
        if (intent == null) {
            zzKl().zzLZ().log("onRebind called with null intent");
            return;
        }
        zzKl().zzMf().zzj("onRebind called. action", intent.getAction());
    }

    @MainThread
    public int onStartCommand(Intent intent, int i, final int i2) {
        final zzaue zzbM = zzaue.zzbM(this.mContext);
        final zzatx zzKl = zzbM.zzKl();
        if (intent == null) {
            zzKl.zzMb().log("AppMeasurementService started with null intent");
        } else {
            String action = intent.getAction();
            zzbM.zzKn().zzLh();
            zzKl.zzMf().zze("Local AppMeasurementService called. startId, action", Integer.valueOf(i2), action);
            if ("com.google.android.gms.measurement.UPLOAD".equals(action)) {
                zzbM.zzKk().zzm(new Runnable(this) {
                    final /* synthetic */ zzaum zzbvX;

                    public void run() {
                        zzbM.zzMN();
                        zzbM.zzMI();
                        this.zzbvX.mHandler.post(new Runnable(this) {
                            final /* synthetic */ AnonymousClass1 zzbvY;

                            {
                                this.zzbvY = r1;
                            }

                            public void run() {
                                if (this.zzbvY.zzbvX.zzbvW.callServiceStopSelfResult(i2)) {
                                    zzbM.zzKn().zzLh();
                                    zzKl.zzMf().log("Local AppMeasurementService processed last upload request");
                                }
                            }
                        });
                    }
                });
            }
        }
        return 2;
    }

    @MainThread
    public boolean onUnbind(Intent intent) {
        if (intent == null) {
            zzKl().zzLZ().log("onUnbind called with null intent");
        } else {
            zzKl().zzMf().zzj("onUnbind called for intent. action", intent.getAction());
        }
        return true;
    }
}
