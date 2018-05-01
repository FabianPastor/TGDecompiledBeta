package com.google.android.gms.measurement;

import android.content.BroadcastReceiver;
import android.content.BroadcastReceiver.PendingResult;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.internal.zzcid;
import com.google.android.gms.internal.zzcif;

public final class AppMeasurementInstallReferrerReceiver
  extends BroadcastReceiver
  implements zzcif
{
  private zzcid zziwp;
  
  public final BroadcastReceiver.PendingResult doGoAsync()
  {
    return goAsync();
  }
  
  public final void doStartService(Context paramContext, Intent paramIntent) {}
  
  public final void onReceive(Context paramContext, Intent paramIntent)
  {
    if (this.zziwp == null) {
      this.zziwp = new zzcid(this);
    }
    this.zziwp.onReceive(paramContext, paramIntent);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/AppMeasurementInstallReferrerReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */