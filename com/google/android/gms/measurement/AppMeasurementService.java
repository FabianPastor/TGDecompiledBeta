package com.google.android.gms.measurement;

import android.app.Service;
import android.app.job.JobParameters;
import android.content.Intent;
import android.os.IBinder;
import com.google.android.gms.internal.zzcla;
import com.google.android.gms.internal.zzcle;

public final class AppMeasurementService
  extends Service
  implements zzcle
{
  private zzcla<AppMeasurementService> zziwq;
  
  private final zzcla<AppMeasurementService> zzawh()
  {
    if (this.zziwq == null) {
      this.zziwq = new zzcla(this);
    }
    return this.zziwq;
  }
  
  public final boolean callServiceStopSelfResult(int paramInt)
  {
    return stopSelfResult(paramInt);
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    return zzawh().onBind(paramIntent);
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
  
  public final int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    return zzawh().onStartCommand(paramIntent, paramInt1, paramInt2);
  }
  
  public final boolean onUnbind(Intent paramIntent)
  {
    return zzawh().onUnbind(paramIntent);
  }
  
  public final void zza(JobParameters paramJobParameters, boolean paramBoolean)
  {
    throw new UnsupportedOperationException();
  }
  
  public final void zzm(Intent paramIntent)
  {
    AppMeasurementReceiver.completeWakefulIntent(paramIntent);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/AppMeasurementService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */