package com.google.android.gms.measurement;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.google.android.gms.measurement.internal.zzu;
import com.google.android.gms.measurement.internal.zzu.zza;

public final class AppMeasurementReceiver
  extends WakefulBroadcastReceiver
  implements zzu.zza
{
  private zzu anu;
  
  private zzu zzbso()
  {
    if (this.anu == null) {
      this.anu = new zzu(this);
    }
    return this.anu;
  }
  
  @MainThread
  public void doStartService(Context paramContext, Intent paramIntent)
  {
    startWakefulService(paramContext, paramIntent);
  }
  
  @MainThread
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    zzbso().onReceive(paramContext, paramIntent);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/AppMeasurementReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */