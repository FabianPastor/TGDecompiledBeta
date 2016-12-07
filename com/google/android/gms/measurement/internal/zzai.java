package com.google.android.gms.measurement.internal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;

public class zzai extends zzaa {
    private final zzf ass;
    private boolean cc;
    private final AlarmManager cd = ((AlarmManager) getContext().getSystemService("alarm"));

    protected zzai(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
        this.ass = new zzf(this, com_google_android_gms_measurement_internal_zzx) {
            final /* synthetic */ zzai ast;

            public void run() {
                this.ast.zzbyl();
            }
        };
    }

    private PendingIntent zzaee() {
        Intent intent = new Intent();
        Context context = getContext();
        String str = (!zzbvi().zzact() || this.anq.zzbxg()) ? "com.google.android.gms.measurement.AppMeasurementReceiver" : "com.google.android.gms.measurement.PackageMeasurementReceiver";
        Intent className = intent.setClassName(context, str);
        className.setAction("com.google.android.gms.measurement.UPLOAD");
        return PendingIntent.getBroadcast(getContext(), 0, className, 0);
    }

    private void zzbyl() {
        Intent intent = new Intent();
        Context context = getContext();
        String str = (!zzbvi().zzact() || this.anq.zzbxg()) ? "com.google.android.gms.measurement.AppMeasurementReceiver" : "com.google.android.gms.measurement.PackageMeasurementReceiver";
        Intent className = intent.setClassName(context, str);
        className.setAction("com.google.android.gms.measurement.UPLOAD");
        getContext().sendBroadcast(className);
    }

    public void cancel() {
        zzaax();
        this.cc = false;
        this.cd.cancel(zzaee());
        this.ass.cancel();
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzaam() {
        super.zzaam();
    }

    public /* bridge */ /* synthetic */ zze zzaan() {
        return super.zzaan();
    }

    public /* bridge */ /* synthetic */ void zzbuv() {
        super.zzbuv();
    }

    public /* bridge */ /* synthetic */ zzc zzbuw() {
        return super.zzbuw();
    }

    public /* bridge */ /* synthetic */ zzac zzbux() {
        return super.zzbux();
    }

    public /* bridge */ /* synthetic */ zzn zzbuy() {
        return super.zzbuy();
    }

    public /* bridge */ /* synthetic */ zzg zzbuz() {
        return super.zzbuz();
    }

    public /* bridge */ /* synthetic */ zzad zzbva() {
        return super.zzbva();
    }

    public /* bridge */ /* synthetic */ zze zzbvb() {
        return super.zzbvb();
    }

    public /* bridge */ /* synthetic */ zzal zzbvc() {
        return super.zzbvc();
    }

    public /* bridge */ /* synthetic */ zzv zzbvd() {
        return super.zzbvd();
    }

    public /* bridge */ /* synthetic */ zzaf zzbve() {
        return super.zzbve();
    }

    public /* bridge */ /* synthetic */ zzw zzbvf() {
        return super.zzbvf();
    }

    public /* bridge */ /* synthetic */ zzp zzbvg() {
        return super.zzbvg();
    }

    public /* bridge */ /* synthetic */ zzt zzbvh() {
        return super.zzbvh();
    }

    public /* bridge */ /* synthetic */ zzd zzbvi() {
        return super.zzbvi();
    }

    public void zzx(long j) {
        boolean z = false;
        zzaax();
        boolean z2 = zzbvi().zzact() || zzu.zzh(getContext(), false);
        zzac.zza(z2, (Object) "Receiver not registered/enabled");
        if (zzbvi().zzact() || zzae.zzi(getContext(), false)) {
            z = true;
        }
        zzac.zza(z, (Object) "Service not registered/enabled");
        cancel();
        long elapsedRealtime = zzaan().elapsedRealtime() + j;
        this.cc = true;
        if (j < zzbvi().zzbup() && !this.ass.zzfl()) {
            this.ass.zzx(j);
        }
        this.cd.setInexactRepeating(2, elapsedRealtime, Math.max(zzbvi().zzbuq(), j), zzaee());
    }

    public /* bridge */ /* synthetic */ void zzyl() {
        super.zzyl();
    }

    protected void zzym() {
        this.cd.cancel(zzaee());
    }
}
