package com.google.android.gms.internal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.common.util.zze;

public class zzaua extends zzats {
    private boolean zzaeg;
    private final AlarmManager zzaeh = ((AlarmManager) getContext().getSystemService("alarm"));
    private final zzasv zzbuX;

    protected zzaua(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
        this.zzbuX = new zzasv(this, com_google_android_gms_internal_zzatp) {
            final /* synthetic */ zzaua zzbuY;

            public void run() {
                this.zzbuY.zzMh();
            }
        };
    }

    private void zzMh() {
        Intent intent = new Intent();
        Context context = getContext();
        zzJv().zzKk();
        intent = intent.setClassName(context, "com.google.android.gms.measurement.AppMeasurementReceiver");
        intent.setAction("com.google.android.gms.measurement.UPLOAD");
        getContext().sendBroadcast(intent);
    }

    private PendingIntent zzpe() {
        Intent intent = new Intent();
        Context context = getContext();
        zzJv().zzKk();
        intent = intent.setClassName(context, "com.google.android.gms.measurement.AppMeasurementReceiver");
        intent.setAction("com.google.android.gms.measurement.UPLOAD");
        return PendingIntent.getBroadcast(getContext(), 0, intent, 0);
    }

    public void cancel() {
        zznA();
        this.zzaeg = false;
        this.zzaeh.cancel(zzpe());
        this.zzbuX.cancel();
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzJd() {
        super.zzJd();
    }

    public /* bridge */ /* synthetic */ void zzJe() {
        super.zzJe();
    }

    public /* bridge */ /* synthetic */ void zzJf() {
        super.zzJf();
    }

    public /* bridge */ /* synthetic */ zzaso zzJg() {
        return super.zzJg();
    }

    public /* bridge */ /* synthetic */ zzass zzJh() {
        return super.zzJh();
    }

    public /* bridge */ /* synthetic */ zzatu zzJi() {
        return super.zzJi();
    }

    public /* bridge */ /* synthetic */ zzatf zzJj() {
        return super.zzJj();
    }

    public /* bridge */ /* synthetic */ zzasw zzJk() {
        return super.zzJk();
    }

    public /* bridge */ /* synthetic */ zzatw zzJl() {
        return super.zzJl();
    }

    public /* bridge */ /* synthetic */ zzatv zzJm() {
        return super.zzJm();
    }

    public /* bridge */ /* synthetic */ zzatg zzJn() {
        return super.zzJn();
    }

    public /* bridge */ /* synthetic */ zzasu zzJo() {
        return super.zzJo();
    }

    public /* bridge */ /* synthetic */ zzaue zzJp() {
        return super.zzJp();
    }

    public /* bridge */ /* synthetic */ zzatn zzJq() {
        return super.zzJq();
    }

    public /* bridge */ /* synthetic */ zzaty zzJr() {
        return super.zzJr();
    }

    public /* bridge */ /* synthetic */ zzato zzJs() {
        return super.zzJs();
    }

    public /* bridge */ /* synthetic */ zzati zzJt() {
        return super.zzJt();
    }

    public /* bridge */ /* synthetic */ zzatl zzJu() {
        return super.zzJu();
    }

    public /* bridge */ /* synthetic */ zzast zzJv() {
        return super.zzJv();
    }

    public /* bridge */ /* synthetic */ void zzmq() {
        super.zzmq();
    }

    protected void zzmr() {
        this.zzaeh.cancel(zzpe());
    }

    public /* bridge */ /* synthetic */ zze zznq() {
        return super.zznq();
    }

    public void zzx(long j) {
        zznA();
        zzJv().zzKk();
        if (!zzatm.zzi(getContext(), false)) {
            zzJt().zzLf().log("Receiver not registered/enabled");
        }
        zzJv().zzKk();
        if (!zzatx.zzj(getContext(), false)) {
            zzJt().zzLf().log("Service not registered/enabled");
        }
        cancel();
        long elapsedRealtime = zznq().elapsedRealtime() + j;
        this.zzaeg = true;
        if (j < zzJv().zzKA() && !this.zzbuX.zzcv()) {
            this.zzbuX.zzx(j);
        }
        this.zzaeh.setInexactRepeating(2, elapsedRealtime, Math.max(zzJv().zzKB(), j), zzpe());
    }
}
