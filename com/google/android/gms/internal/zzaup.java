package com.google.android.gms.internal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.common.util.zze;

public class zzaup extends zzauh {
    private boolean zzafh;
    private final AlarmManager zzafi = ((AlarmManager) getContext().getSystemService("alarm"));
    private final zzatk zzbwa;

    protected zzaup(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
        this.zzbwa = new zzatk(this, com_google_android_gms_internal_zzaue) {
            final /* synthetic */ zzaup zzbwb;

            public void run() {
                this.zzbwb.zzNh();
            }
        };
    }

    private void zzNh() {
        Intent intent = new Intent();
        Context context = getContext();
        zzKn().zzLg();
        intent = intent.setClassName(context, "com.google.android.gms.measurement.AppMeasurementReceiver");
        intent.setAction("com.google.android.gms.measurement.UPLOAD");
        getContext().sendBroadcast(intent);
    }

    private PendingIntent zzpE() {
        Intent intent = new Intent();
        Context context = getContext();
        zzKn().zzLg();
        intent = intent.setClassName(context, "com.google.android.gms.measurement.AppMeasurementReceiver");
        intent.setAction("com.google.android.gms.measurement.UPLOAD");
        return PendingIntent.getBroadcast(getContext(), 0, intent, 0);
    }

    public void cancel() {
        zzob();
        this.zzafh = false;
        this.zzafi.cancel(zzpE());
        this.zzbwa.cancel();
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzJV() {
        super.zzJV();
    }

    public /* bridge */ /* synthetic */ void zzJW() {
        super.zzJW();
    }

    public /* bridge */ /* synthetic */ void zzJX() {
        super.zzJX();
    }

    public /* bridge */ /* synthetic */ zzatb zzJY() {
        return super.zzJY();
    }

    public /* bridge */ /* synthetic */ zzatf zzJZ() {
        return super.zzJZ();
    }

    public /* bridge */ /* synthetic */ zzauj zzKa() {
        return super.zzKa();
    }

    public /* bridge */ /* synthetic */ zzatu zzKb() {
        return super.zzKb();
    }

    public /* bridge */ /* synthetic */ zzatl zzKc() {
        return super.zzKc();
    }

    public /* bridge */ /* synthetic */ zzaul zzKd() {
        return super.zzKd();
    }

    public /* bridge */ /* synthetic */ zzauk zzKe() {
        return super.zzKe();
    }

    public /* bridge */ /* synthetic */ zzatv zzKf() {
        return super.zzKf();
    }

    public /* bridge */ /* synthetic */ zzatj zzKg() {
        return super.zzKg();
    }

    public /* bridge */ /* synthetic */ zzaut zzKh() {
        return super.zzKh();
    }

    public /* bridge */ /* synthetic */ zzauc zzKi() {
        return super.zzKi();
    }

    public /* bridge */ /* synthetic */ zzaun zzKj() {
        return super.zzKj();
    }

    public /* bridge */ /* synthetic */ zzaud zzKk() {
        return super.zzKk();
    }

    public /* bridge */ /* synthetic */ zzatx zzKl() {
        return super.zzKl();
    }

    public /* bridge */ /* synthetic */ zzaua zzKm() {
        return super.zzKm();
    }

    public /* bridge */ /* synthetic */ zzati zzKn() {
        return super.zzKn();
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
        zzKn().zzLg();
        if (!zzaub.zzi(getContext(), false)) {
            zzKl().zzMd().log("Receiver not registered/enabled");
        }
        zzKn().zzLg();
        if (!zzaum.zzj(getContext(), false)) {
            zzKl().zzMd().log("Service not registered/enabled");
        }
        cancel();
        long elapsedRealtime = zznR().elapsedRealtime() + j;
        this.zzafh = true;
        if (j < zzKn().zzLx() && !this.zzbwa.zzcy()) {
            this.zzbwa.zzy(j);
        }
        this.zzafi.setInexactRepeating(2, elapsedRealtime, Math.max(zzKn().zzLy(), j), zzpE());
    }
}
