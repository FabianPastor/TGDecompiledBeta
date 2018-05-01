package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;

final class zzclm
  extends zzcgs
{
  zzclm(zzcll paramzzcll, zzcim paramzzcim)
  {
    super(paramzzcim);
  }
  
  public final void run()
  {
    this.zzjjh.cancel();
    this.zzjjh.zzawy().zzazj().log("Sending upload intent from DelayedRunnable");
    Intent localIntent = new Intent().setClassName(this.zzjjh.getContext(), "com.google.android.gms.measurement.AppMeasurementReceiver");
    localIntent.setAction("com.google.android.gms.measurement.UPLOAD");
    this.zzjjh.getContext().sendBroadcast(localIntent);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzclm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */