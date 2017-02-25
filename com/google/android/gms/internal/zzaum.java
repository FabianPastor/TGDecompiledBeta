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
    private final zza zzbvX;

    public interface zza {
        boolean callServiceStopSelfResult(int i);

        Context getContext();
    }

    public zzaum(zza com_google_android_gms_internal_zzaum_zza) {
        this.mContext = com_google_android_gms_internal_zzaum_zza.getContext();
        zzac.zzw(this.mContext);
        this.zzbvX = com_google_android_gms_internal_zzaum_zza;
    }

    private zzatx zzKk() {
        return zzaue.zzbM(this.mContext).zzKk();
    }

    public static boolean zzj(Context context, boolean z) {
        zzac.zzw(context);
        return zzaut.zzy(context, z ? "com.google.android.gms.measurement.PackageMeasurementService" : "com.google.android.gms.measurement.AppMeasurementService");
    }

    @MainThread
    public IBinder onBind(Intent intent) {
        if (intent == null) {
            zzKk().zzLX().log("onBind called with null intent");
            return null;
        }
        String action = intent.getAction();
        if ("com.google.android.gms.measurement.START".equals(action)) {
            return new zzauf(zzaue.zzbM(this.mContext));
        }
        zzKk().zzLZ().zzj("onBind received unknown action", action);
        return null;
    }

    @MainThread
    public void onCreate() {
        zzaue zzbM = zzaue.zzbM(this.mContext);
        zzatx zzKk = zzbM.zzKk();
        zzbM.zzKm().zzLf();
        zzKk.zzMd().log("Local AppMeasurementService is starting up");
    }

    @MainThread
    public void onDestroy() {
        zzaue zzbM = zzaue.zzbM(this.mContext);
        zzatx zzKk = zzbM.zzKk();
        zzbM.zzKm().zzLf();
        zzKk.zzMd().log("Local AppMeasurementService is shutting down");
    }

    @MainThread
    public void onRebind(Intent intent) {
        if (intent == null) {
            zzKk().zzLX().log("onRebind called with null intent");
            return;
        }
        zzKk().zzMd().zzj("onRebind called. action", intent.getAction());
    }

    @MainThread
    public int onStartCommand(Intent intent, int i, final int i2) {
        final zzaue zzbM = zzaue.zzbM(this.mContext);
        final zzatx zzKk = zzbM.zzKk();
        if (intent == null) {
            zzKk.zzLZ().log("AppMeasurementService started with null intent");
        } else {
            String action = intent.getAction();
            zzbM.zzKm().zzLf();
            zzKk.zzMd().zze("Local AppMeasurementService called. startId, action", Integer.valueOf(i2), action);
            if ("com.google.android.gms.measurement.UPLOAD".equals(action)) {
                zzbM.zzKj().zzm(new Runnable(this) {
                    final /* synthetic */ zzaum zzbvY;

                    public void run() {
                        zzbM.zzMK();
                        zzbM.zzMF();
                        this.zzbvY.mHandler.post(new Runnable(this) {
                            final /* synthetic */ AnonymousClass1 zzbvZ;

                            {
                                this.zzbvZ = r1;
                            }

                            public void run() {
                                if (this.zzbvZ.zzbvY.zzbvX.callServiceStopSelfResult(i2)) {
                                    zzbM.zzKm().zzLf();
                                    zzKk.zzMd().log("Local AppMeasurementService processed last upload request");
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
            zzKk().zzLX().log("onUnbind called with null intent");
        } else {
            zzKk().zzMd().zzj("onUnbind called for intent. action", intent.getAction());
        }
        return true;
    }
}
