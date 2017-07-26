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

public final class zzcjg extends zzchj {
    private final AlarmManager zzahd = ((AlarmManager) super.getContext().getSystemService("alarm"));
    private final zzcer zzbuv;
    private Integer zzbuw;

    protected zzcjg(zzcgl com_google_android_gms_internal_zzcgl) {
        super(com_google_android_gms_internal_zzcgl);
        this.zzbuv = new zzcjh(this, com_google_android_gms_internal_zzcgl);
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
        zzcem.zzxE();
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
        zzcem.zzxE();
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
        zzcem.zzxE();
        if (!zzcgc.zzj(super.getContext(), false)) {
            super.zzwF().zzyC().log("Receiver not registered/enabled");
        }
        zzcem.zzxE();
        if (!zzciw.zzk(super.getContext(), false)) {
            super.zzwF().zzyC().log("Service not registered/enabled");
        }
        cancel();
        long elapsedRealtime = super.zzkq().elapsedRealtime() + j;
        if (j < zzcem.zzxV() && !this.zzbuv.zzbo()) {
            super.zzwF().zzyD().log("Scheduling upload with DelayedRunnable");
            this.zzbuv.zzs(j);
        }
        zzcem.zzxE();
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
        this.zzahd.setInexactRepeating(2, elapsedRealtime, Math.max(zzcem.zzxW(), j), zzlD());
    }

    public final /* bridge */ /* synthetic */ zzcfj zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjl zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzcja zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgg zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfl zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfw zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwH() {
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

    public final /* bridge */ /* synthetic */ zzcec zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcej zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchl zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzcet zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcid zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchz zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfh zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcen zzwz() {
        return super.zzwz();
    }
}
