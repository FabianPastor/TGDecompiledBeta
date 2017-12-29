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
import com.google.android.gms.common.util.zzd;

public final class zzcll extends zzcjl {
    private final AlarmManager zzdvu = ((AlarmManager) getContext().getSystemService("alarm"));
    private Integer zzdvv;
    private final zzcgs zzjjg;

    protected zzcll(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
        this.zzjjg = new zzclm(this, com_google_android_gms_internal_zzcim);
    }

    private final int getJobId() {
        if (this.zzdvv == null) {
            String str = "measurement";
            String valueOf = String.valueOf(getContext().getPackageName());
            this.zzdvv = Integer.valueOf((valueOf.length() != 0 ? str.concat(valueOf) : new String(str)).hashCode());
        }
        return this.zzdvv.intValue();
    }

    @TargetApi(24)
    private final void zzbax() {
        JobScheduler jobScheduler = (JobScheduler) getContext().getSystemService("jobscheduler");
        zzawy().zzazj().zzj("Cancelling job. JobID", Integer.valueOf(getJobId()));
        jobScheduler.cancel(getJobId());
    }

    private final PendingIntent zzzf() {
        Intent className = new Intent().setClassName(getContext(), "com.google.android.gms.measurement.AppMeasurementReceiver");
        className.setAction("com.google.android.gms.measurement.UPLOAD");
        return PendingIntent.getBroadcast(getContext(), 0, className, 0);
    }

    public final void cancel() {
        zzxf();
        this.zzdvu.cancel(zzzf());
        this.zzjjg.cancel();
        if (VERSION.SDK_INT >= 24) {
            zzbax();
        }
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final /* bridge */ /* synthetic */ void zzawi() {
        super.zzawi();
    }

    public final /* bridge */ /* synthetic */ void zzawj() {
        super.zzawj();
    }

    public final /* bridge */ /* synthetic */ zzcgd zzawk() {
        return super.zzawk();
    }

    public final /* bridge */ /* synthetic */ zzcgk zzawl() {
        return super.zzawl();
    }

    public final /* bridge */ /* synthetic */ zzcjn zzawm() {
        return super.zzawm();
    }

    public final /* bridge */ /* synthetic */ zzchh zzawn() {
        return super.zzawn();
    }

    public final /* bridge */ /* synthetic */ zzcgu zzawo() {
        return super.zzawo();
    }

    public final /* bridge */ /* synthetic */ zzckg zzawp() {
        return super.zzawp();
    }

    public final /* bridge */ /* synthetic */ zzckc zzawq() {
        return super.zzawq();
    }

    public final /* bridge */ /* synthetic */ zzchi zzawr() {
        return super.zzawr();
    }

    public final /* bridge */ /* synthetic */ zzcgo zzaws() {
        return super.zzaws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzawt() {
        return super.zzawt();
    }

    public final /* bridge */ /* synthetic */ zzclq zzawu() {
        return super.zzawu();
    }

    public final /* bridge */ /* synthetic */ zzcig zzawv() {
        return super.zzawv();
    }

    public final /* bridge */ /* synthetic */ zzclf zzaww() {
        return super.zzaww();
    }

    public final /* bridge */ /* synthetic */ zzcih zzawx() {
        return super.zzawx();
    }

    public final /* bridge */ /* synthetic */ zzchm zzawy() {
        return super.zzawy();
    }

    public final /* bridge */ /* synthetic */ zzchx zzawz() {
        return super.zzawz();
    }

    public final /* bridge */ /* synthetic */ zzcgn zzaxa() {
        return super.zzaxa();
    }

    protected final boolean zzaxz() {
        this.zzdvu.cancel(zzzf());
        if (VERSION.SDK_INT >= 24) {
            zzbax();
        }
        return false;
    }

    public final void zzs(long j) {
        zzxf();
        if (!zzcid.zzbk(getContext())) {
            zzawy().zzazi().log("Receiver not registered/enabled");
        }
        if (!zzcla.zzk(getContext(), false)) {
            zzawy().zzazi().log("Service not registered/enabled");
        }
        cancel();
        long elapsedRealtime = zzws().elapsedRealtime() + j;
        if (j < Math.max(0, ((Long) zzchc.zzjba.get()).longValue()) && !this.zzjjg.zzdx()) {
            zzawy().zzazj().log("Scheduling upload with DelayedRunnable");
            this.zzjjg.zzs(j);
        }
        if (VERSION.SDK_INT >= 24) {
            zzawy().zzazj().log("Scheduling upload with JobScheduler");
            JobScheduler jobScheduler = (JobScheduler) getContext().getSystemService("jobscheduler");
            Builder builder = new Builder(getJobId(), new ComponentName(getContext(), "com.google.android.gms.measurement.AppMeasurementJobService"));
            builder.setMinimumLatency(j);
            builder.setOverrideDeadline(j << 1);
            PersistableBundle persistableBundle = new PersistableBundle();
            persistableBundle.putString("action", "com.google.android.gms.measurement.UPLOAD");
            builder.setExtras(persistableBundle);
            JobInfo build = builder.build();
            zzawy().zzazj().zzj("Scheduling job. JobID", Integer.valueOf(getJobId()));
            jobScheduler.schedule(build);
            return;
        }
        zzawy().zzazj().log("Scheduling upload with AlarmManager");
        this.zzdvu.setInexactRepeating(2, elapsedRealtime, Math.max(((Long) zzchc.zzjav.get()).longValue(), j), zzzf());
    }

    public final /* bridge */ /* synthetic */ void zzve() {
        super.zzve();
    }

    public final /* bridge */ /* synthetic */ zzd zzws() {
        return super.zzws();
    }
}
