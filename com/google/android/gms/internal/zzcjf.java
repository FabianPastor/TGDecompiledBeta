package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobInfo.Builder;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.PersistableBundle;
import com.google.android.gms.common.util.zze;

public final class zzcjf extends zzchi {
    private final AlarmManager zzahd = ((AlarmManager) super.getContext().getSystemService("alarm"));
    private final zzceq zzbuv;
    private Integer zzbuw;

    protected zzcjf(zzcgk com_google_android_gms_internal_zzcgk) {
        super(com_google_android_gms_internal_zzcgk);
        this.zzbuv = new zzcjg(this, com_google_android_gms_internal_zzcgk);
    }

    private final int getJobId() {
        if (this.zzbuw == null) {
            String str = "measurement";
            String valueOf = String.valueOf(super.getContext().getPackageName());
            this.zzbuw = Integer.valueOf((valueOf.length() != 0 ? str.concat(valueOf) : new String(str)).hashCode());
        }
        return this.zzbuw.intValue();
    }

    private final PendingIntent zzlD() {
        Intent intent = new Intent();
        Context context = super.getContext();
        zzcel.zzxE();
        intent = intent.setClassName(context, "com.google.android.gms.measurement.AppMeasurementReceiver");
        intent.setAction("com.google.android.gms.measurement.UPLOAD");
        return PendingIntent.getBroadcast(super.getContext(), 0, intent, 0);
    }

    @TargetApi(24)
    private final void zzzq() {
        JobScheduler jobScheduler = (JobScheduler) super.getContext().getSystemService("jobscheduler");
        super.zzwF().zzyD().zzj("Cancelling job. JobID", Integer.valueOf(getJobId()));
        jobScheduler.cancel(getJobId());
    }

    private final void zzzr() {
        Intent intent = new Intent();
        Context context = super.getContext();
        zzcel.zzxE();
        intent = intent.setClassName(context, "com.google.android.gms.measurement.AppMeasurementReceiver");
        intent.setAction("com.google.android.gms.measurement.UPLOAD");
        super.getContext().sendBroadcast(intent);
    }

    public final void cancel() {
        zzkD();
        this.zzahd.cancel(zzlD());
        this.zzbuv.cancel();
        if (VERSION.SDK_INT >= 24) {
            zzzq();
        }
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
        this.zzahd.cancel(zzlD());
        if (VERSION.SDK_INT >= 24) {
            zzzq();
        }
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    public final void zzs(long j) {
        zzkD();
        zzcel.zzxE();
        if (!zzcgb.zzj(super.getContext(), false)) {
            super.zzwF().zzyC().log("Receiver not registered/enabled");
        }
        zzcel.zzxE();
        if (!zzciv.zzk(super.getContext(), false)) {
            super.zzwF().zzyC().log("Service not registered/enabled");
        }
        cancel();
        long elapsedRealtime = super.zzkq().elapsedRealtime() + j;
        if (j < zzcel.zzxV() && !this.zzbuv.zzbo()) {
            super.zzwF().zzyD().log("Scheduling upload with DelayedRunnable");
            this.zzbuv.zzs(j);
        }
        zzcel.zzxE();
        if (VERSION.SDK_INT >= 24) {
            super.zzwF().zzyD().log("Scheduling upload with JobScheduler");
            JobScheduler jobScheduler = (JobScheduler) super.getContext().getSystemService("jobscheduler");
            Builder builder = new Builder(getJobId(), new ComponentName(super.getContext(), "com.google.android.gms.measurement.AppMeasurementJobService"));
            builder.setMinimumLatency(j);
            builder.setOverrideDeadline(j << 1);
            PersistableBundle persistableBundle = new PersistableBundle();
            persistableBundle.putString("action", "com.google.android.gms.measurement.UPLOAD");
            builder.setExtras(persistableBundle);
            JobInfo build = builder.build();
            super.zzwF().zzyD().zzj("Scheduling job. JobID", Integer.valueOf(getJobId()));
            jobScheduler.schedule(build);
            return;
        }
        super.zzwF().zzyD().log("Scheduling upload with AlarmManager");
        this.zzahd.setInexactRepeating(2, elapsedRealtime, Math.max(zzcel.zzxW(), j), zzlD());
    }

    public final /* bridge */ /* synthetic */ zzcfi zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjk zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcge zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzciz zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfk zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfv zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcel zzwH() {
        return super.zzwH();
    }

    public final /* bridge */ /* synthetic */ void zzwo() {
        super.zzwo();
    }

    public final /* bridge */ /* synthetic */ void zzwp() {
        super.zzwp();
    }

    public final /* bridge */ /* synthetic */ void zzwq() {
        super.zzwq();
    }

    public final /* bridge */ /* synthetic */ zzceb zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcei zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcff zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzces zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcic zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchy zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwz() {
        return super.zzwz();
    }
}
