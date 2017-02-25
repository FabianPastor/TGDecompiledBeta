package com.google.android.gms.internal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.common.util.zze;

public class zzaup extends zzauh {
    private boolean zzafh;
    private final AlarmManager zzafi = ((AlarmManager) getContext().getSystemService("alarm"));
    private final zzatk zzbwe;

    protected zzaup(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
        this.zzbwe = new zzatk(this, com_google_android_gms_internal_zzaue) {
            final /* synthetic */ zzaup zzbwf;

            public void run() {
                this.zzbwf.zzNg();
            }
        };
    }

    private void zzNg() {
        Intent intent = new Intent();
        Context context = getContext();
        zzKm().zzLf();
        intent = intent.setClassName(context, "com.google.android.gms.measurement.AppMeasurementReceiver");
        intent.setAction("com.google.android.gms.measurement.UPLOAD");
        getContext().sendBroadcast(intent);
    }

    private PendingIntent zzpE() {
        Intent intent = new Intent();
        Context context = getContext();
        zzKm().zzLf();
        intent = intent.setClassName(context, "com.google.android.gms.measurement.AppMeasurementReceiver");
        intent.setAction("com.google.android.gms.measurement.UPLOAD");
        return PendingIntent.getBroadcast(getContext(), 0, intent, 0);
    }

    public void cancel() {
        zzob();
        this.zzafh = false;
        this.zzafi.cancel(zzpE());
        this.zzbwe.cancel();
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzJU() {
        super.zzJU();
    }

    public /* bridge */ /* synthetic */ void zzJV() {
        super.zzJV();
    }

    public /* bridge */ /* synthetic */ void zzJW() {
        super.zzJW();
    }

    public /* bridge */ /* synthetic */ zzatb zzJX() {
        return super.zzJX();
    }

    public /* bridge */ /* synthetic */ zzatf zzJY() {
        return super.zzJY();
    }

    public /* bridge */ /* synthetic */ zzauj zzJZ() {
        return super.zzJZ();
    }

    public /* bridge */ /* synthetic */ zzatu zzKa() {
        return super.zzKa();
    }

    public /* bridge */ /* synthetic */ zzatl zzKb() {
        return super.zzKb();
    }

    public /* bridge */ /* synthetic */ zzaul zzKc() {
        return super.zzKc();
    }

    public /* bridge */ /* synthetic */ zzauk zzKd() {
        return super.zzKd();
    }

    public /* bridge */ /* synthetic */ zzatv zzKe() {
        return super.zzKe();
    }

    public /* bridge */ /* synthetic */ zzatj zzKf() {
        return super.zzKf();
    }

    public /* bridge */ /* synthetic */ zzaut zzKg() {
        return super.zzKg();
    }

    public /* bridge */ /* synthetic */ zzauc zzKh() {
        return super.zzKh();
    }

    public /* bridge */ /* synthetic */ zzaun zzKi() {
        return super.zzKi();
    }

    public /* bridge */ /* synthetic */ zzaud zzKj() {
        return super.zzKj();
    }

    public /* bridge */ /* synthetic */ zzatx zzKk() {
        return super.zzKk();
    }

    public /* bridge */ /* synthetic */ zzaua zzKl() {
        return super.zzKl();
    }

    public /* bridge */ /* synthetic */ zzati zzKm() {
        return super.zzKm();
    }

    public /* bridge */ /* synthetic */ void zzmR() {
        super.zzmR();
    }

    protected void zzmS() {
        this.zzafi.cancel(zzpE());
    }

    public /* bridge */ /* synthetic */ zze zznR() {
        return super.zznR();
    }

    public void zzy(long j) {
        zzob();
        zzKm().zzLf();
        if (!zzaub.zzi(getContext(), false)) {
            zzKk().zzMc().log("Receiver not registered/enabled");
        }
        zzKm().zzLf();
        if (!zzaum.zzj(getContext(), false)) {
            zzKk().zzMc().log("Service not registered/enabled");
        }
        cancel();
        long elapsedRealtime = zznR().elapsedRealtime() + j;
        this.zzafh = true;
        if (j < zzKm().zzLw() && !this.zzbwe.zzcy()) {
            this.zzbwe.zzy(j);
        }
        this.zzafi.setInexactRepeating(2, elapsedRealtime, Math.max(zzKm().zzLx(), j), zzpE());
    }
}
