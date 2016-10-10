package com.google.android.gms.measurement;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.MainThread;
import com.google.android.gms.measurement.internal.zzae;
import com.google.android.gms.measurement.internal.zzae.zza;

public final class AppMeasurementService
  extends Service
  implements zzae.zza
{
  private zzae anv;
  
  private zzae zzbsp()
  {
    if (this.anv == null) {
      this.anv = new zzae(this);
    }
    return this.anv;
  }
  
  public boolean callServiceStopSelfResult(int paramInt)
  {
    return stopSelfResult(paramInt);
  }
  
  public Context getContext()
  {
    return this;
  }
  
  @MainThread
  public IBinder onBind(Intent paramIntent)
  {
    return zzbsp().onBind(paramIntent);
  }
  
  @MainThread
  public void onCreate()
  {
    super.onCreate();
    zzbsp().onCreate();
  }
  
  @MainThread
  public void onDestroy()
  {
    zzbsp().onDestroy();
    super.onDestroy();
  }
  
  @MainThread
  public void onRebind(Intent paramIntent)
  {
    zzbsp().onRebind(paramIntent);
  }
  
  @MainThread
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    paramInt1 = zzbsp().onStartCommand(paramIntent, paramInt1, paramInt2);
    AppMeasurementReceiver.completeWakefulIntent(paramIntent);
    return paramInt1;
  }
  
  @MainThread
  public boolean onUnbind(Intent paramIntent)
  {
    return zzbsp().onUnbind(paramIntent);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/AppMeasurementService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */