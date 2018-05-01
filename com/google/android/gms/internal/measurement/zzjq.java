package com.google.android.gms.internal.measurement;

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
import com.google.android.gms.common.util.Clock;

public final class zzjq
  extends zzhk
{
  private final zzem zzaqs;
  private final AlarmManager zzyd = (AlarmManager)getContext().getSystemService("alarm");
  private Integer zzye;
  
  protected zzjq(zzgl paramzzgl)
  {
    super(paramzzgl);
    this.zzaqs = new zzjr(this, paramzzgl, paramzzgl);
  }
  
  private final int getJobId()
  {
    if (this.zzye == null)
    {
      str = String.valueOf(getContext().getPackageName());
      if (str.length() == 0) {
        break label51;
      }
    }
    label51:
    for (String str = "measurement".concat(str);; str = new String("measurement"))
    {
      this.zzye = Integer.valueOf(str.hashCode());
      return this.zzye.intValue();
    }
  }
  
  private final PendingIntent zzek()
  {
    Intent localIntent = new Intent().setClassName(getContext(), "com.google.android.gms.measurement.AppMeasurementReceiver");
    localIntent.setAction("com.google.android.gms.measurement.UPLOAD");
    return PendingIntent.getBroadcast(getContext(), 0, localIntent, 0);
  }
  
  @TargetApi(24)
  private final void zzks()
  {
    JobScheduler localJobScheduler = (JobScheduler)getContext().getSystemService("jobscheduler");
    zzgg().zzir().zzg("Cancelling job. JobID", Integer.valueOf(getJobId()));
    localJobScheduler.cancel(getJobId());
  }
  
  public final void cancel()
  {
    zzch();
    this.zzyd.cancel(zzek());
    this.zzaqs.cancel();
    if (Build.VERSION.SDK_INT >= 24) {
      zzks();
    }
  }
  
  public final void zzh(long paramLong)
  {
    zzch();
    if (!zzgb.zza(getContext())) {
      zzgg().zziq().log("Receiver not registered/enabled");
    }
    if (!zzjf.zza(getContext(), false)) {
      zzgg().zziq().log("Service not registered/enabled");
    }
    cancel();
    long l = zzbt().elapsedRealtime();
    if ((paramLong < Math.max(0L, ((Long)zzew.zzahi.get()).longValue())) && (!this.zzaqs.zzef()))
    {
      zzgg().zzir().log("Scheduling upload with DelayedRunnable");
      this.zzaqs.zzh(paramLong);
    }
    if (Build.VERSION.SDK_INT >= 24)
    {
      zzgg().zzir().log("Scheduling upload with JobScheduler");
      Object localObject = new ComponentName(getContext(), "com.google.android.gms.measurement.AppMeasurementJobService");
      JobScheduler localJobScheduler = (JobScheduler)getContext().getSystemService("jobscheduler");
      localObject = new JobInfo.Builder(getJobId(), (ComponentName)localObject);
      ((JobInfo.Builder)localObject).setMinimumLatency(paramLong);
      ((JobInfo.Builder)localObject).setOverrideDeadline(paramLong << 1);
      PersistableBundle localPersistableBundle = new PersistableBundle();
      localPersistableBundle.putString("action", "com.google.android.gms.measurement.UPLOAD");
      ((JobInfo.Builder)localObject).setExtras(localPersistableBundle);
      localObject = ((JobInfo.Builder)localObject).build();
      zzgg().zzir().zzg("Scheduling job. JobID", Integer.valueOf(getJobId()));
      localJobScheduler.schedule((JobInfo)localObject);
    }
    for (;;)
    {
      return;
      zzgg().zzir().log("Scheduling upload with AlarmManager");
      this.zzyd.setInexactRepeating(2, l + paramLong, Math.max(((Long)zzew.zzahd.get()).longValue(), paramLong), zzek());
    }
  }
  
  protected final boolean zzhh()
  {
    this.zzyd.cancel(zzek());
    if (Build.VERSION.SDK_INT >= 24) {
      zzks();
    }
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzjq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */