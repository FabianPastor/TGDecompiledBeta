package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzac;

public final class zzae {
    private static Boolean aqm;
    private static Boolean aqn;
    private final zza asd;
    private final Context mContext;
    private final Handler mHandler = new Handler();

    public interface zza {
        boolean callServiceStopSelfResult(int i);

        Context getContext();
    }

    public zzae(zza com_google_android_gms_measurement_internal_zzae_zza) {
        this.mContext = com_google_android_gms_measurement_internal_zzae_zza.getContext();
        zzac.zzy(this.mContext);
        this.asd = com_google_android_gms_measurement_internal_zzae_zza;
    }

    private zzp zzbvg() {
        return zzx.zzdt(this.mContext).zzbvg();
    }

    public static boolean zzi(Context context, boolean z) {
        zzac.zzy(context);
        if (aqm != null && !z) {
            return aqm.booleanValue();
        }
        if (aqn != null && z) {
            return aqn.booleanValue();
        }
        boolean zzq = zzal.zzq(context, z ? "com.google.android.gms.measurement.PackageMeasurementService" : "com.google.android.gms.measurement.AppMeasurementService");
        if (z) {
            aqn = Boolean.valueOf(zzq);
            return zzq;
        }
        aqm = Boolean.valueOf(zzq);
        return zzq;
    }

    @MainThread
    public IBinder onBind(Intent intent) {
        if (intent == null) {
            zzbvg().zzbwc().log("onBind called with null intent");
            return null;
        }
        String action = intent.getAction();
        if ("com.google.android.gms.measurement.START".equals(action)) {
            return new zzy(zzx.zzdt(this.mContext));
        }
        zzbvg().zzbwe().zzj("onBind received unknown action", action);
        return null;
    }

    @MainThread
    public void onCreate() {
        zzx zzdt = zzx.zzdt(this.mContext);
        zzp zzbvg = zzdt.zzbvg();
        if (zzdt.zzbvi().zzact()) {
            zzbvg.zzbwj().log("Device PackageMeasurementService is starting up");
        } else {
            zzbvg.zzbwj().log("Local AppMeasurementService is starting up");
        }
    }

    @MainThread
    public void onDestroy() {
        zzx zzdt = zzx.zzdt(this.mContext);
        zzp zzbvg = zzdt.zzbvg();
        if (zzdt.zzbvi().zzact()) {
            zzbvg.zzbwj().log("Device PackageMeasurementService is shutting down");
        } else {
            zzbvg.zzbwj().log("Local AppMeasurementService is shutting down");
        }
    }

    @MainThread
    public void onRebind(Intent intent) {
        if (intent == null) {
            zzbvg().zzbwc().log("onRebind called with null intent");
            return;
        }
        zzbvg().zzbwj().zzj("onRebind called. action", intent.getAction());
    }

    @MainThread
    public int onStartCommand(Intent intent, int i, final int i2) {
        final zzx zzdt = zzx.zzdt(this.mContext);
        final zzp zzbvg = zzdt.zzbvg();
        if (intent == null) {
            zzbvg.zzbwe().log("AppMeasurementService started with null intent");
        } else {
            String action = intent.getAction();
            if (zzdt.zzbvi().zzact()) {
                zzbvg.zzbwj().zze("Device PackageMeasurementService called. startId, action", Integer.valueOf(i2), action);
            } else {
                zzbvg.zzbwj().zze("Local AppMeasurementService called. startId, action", Integer.valueOf(i2), action);
            }
            if ("com.google.android.gms.measurement.UPLOAD".equals(action)) {
                zzdt.zzbvf().zzm(new Runnable(this) {
                    final /* synthetic */ zzae ase;

                    public void run() {
                        zzdt.zzbxp();
                        zzdt.zzbxk();
                        this.ase.mHandler.post(new Runnable(this) {
                            final /* synthetic */ AnonymousClass1 asf;

                            {
                                this.asf = r1;
                            }

                            public void run() {
                                if (!this.asf.ase.asd.callServiceStopSelfResult(i2)) {
                                    return;
                                }
                                if (zzdt.zzbvi().zzact()) {
                                    zzbvg.zzbwj().log("Device PackageMeasurementService processed last upload request");
                                } else {
                                    zzbvg.zzbwj().log("Local AppMeasurementService processed last upload request");
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
            zzbvg().zzbwc().log("onUnbind called with null intent");
        } else {
            zzbvg().zzbwj().zzj("onUnbind called for intent. action", intent.getAction());
        }
        return true;
    }
}
