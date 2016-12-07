package com.google.android.gms.measurement.internal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.common.util.zze;

public class zzai extends zzaa {
    private final zzf avR;
    private boolean ej;
    private final AlarmManager ek = ((AlarmManager) getContext().getSystemService("alarm"));

    protected zzai(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
        this.avR = new zzf(this, com_google_android_gms_measurement_internal_zzx) {
            final /* synthetic */ zzai avS;

            public void run() {
                this.avS.zzbzg();
            }
        };
    }

    private PendingIntent zzafo() {
        Intent intent = new Intent();
        Context context = getContext();
        zzbwd().zzayi();
        intent = intent.setClassName(context, "com.google.android.gms.measurement.AppMeasurementReceiver");
        intent.setAction("com.google.android.gms.measurement.UPLOAD");
        return PendingIntent.getBroadcast(getContext(), 0, intent, 0);
    }

    private void zzbzg() {
        Intent intent = new Intent();
        Context context = getContext();
        zzbwd().zzayi();
        intent = intent.setClassName(context, "com.google.android.gms.measurement.AppMeasurementReceiver");
        intent.setAction("com.google.android.gms.measurement.UPLOAD");
        getContext().sendBroadcast(intent);
    }

    public void cancel() {
        zzacj();
        this.ej = false;
        this.ek.cancel(zzafo());
        this.avR.cancel();
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
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

    public void zzx(long j) {
        zzacj();
        if (!(zzbwd().zzayi() || zzu.zzh(getContext(), false))) {
            zzbwb().zzbxd().log("Receiver not registered/enabled");
        }
        if (!(zzbwd().zzayi() || zzaf.zzi(getContext(), false))) {
            zzbwb().zzbxd().log("Service not registered/enabled");
        }
        cancel();
        long elapsedRealtime = zzabz().elapsedRealtime() + j;
        this.ej = true;
        if (j < zzbwd().zzbvi() && !this.avR.zzfy()) {
            this.avR.zzx(j);
        }
        this.ek.setInexactRepeating(2, elapsedRealtime, Math.max(zzbwd().zzbvj(), j), zzafo());
    }

    public /* bridge */ /* synthetic */ void zzzx() {
        super.zzzx();
    }

    protected void zzzy() {
        this.ek.cancel(zzafo());
    }
}
