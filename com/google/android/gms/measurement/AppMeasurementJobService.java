package com.google.android.gms.measurement;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import com.google.android.gms.internal.zzcla;
import com.google.android.gms.internal.zzcle;

@TargetApi(24)
public final class AppMeasurementJobService
  extends JobService
  implements zzcle
{
  private zzcla<AppMeasurementJobService> zziwq;
  
  private final zzcla<AppMeasurementJobService> zzawh()
  {
    if (this.zziwq == null) {
      this.zziwq = new zzcla(this);
    }
    return this.zziwq;
  }
  
  public final boolean callServiceStopSelfResult(int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  public final void onCreate()
  {
    super.onCreate();
    zzawh().onCreate();
  }
  
  public final void onDestroy()
  {
    zzawh().onDestroy();
    super.onDestroy();
  }
  
  public final void onRebind(Intent paramIntent)
  {
    zzawh().onRebind(paramIntent);
  }
  
  public final boolean onStartJob(JobParameters paramJobParameters)
  {
    return zzawh().onStartJob(paramJobParameters);
  }
  
  public final boolean onStopJob(JobParameters paramJobParameters)
  {
    return false;
  }
  
  public final boolean onUnbind(Intent paramIntent)
  {
    return zzawh().onUnbind(paramIntent);
  }
  
  @TargetApi(24)
  public final void zza(JobParameters paramJobParameters, boolean paramBoolean)
  {
    jobFinished(paramJobParameters, false);
  }
  
  public final void zzm(Intent paramIntent) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/AppMeasurementJobService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */