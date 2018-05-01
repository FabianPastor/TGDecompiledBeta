package com.google.android.gms.internal.measurement;

final class zzjc
  implements Runnable
{
  zzjc(zziz paramzziz, zzey paramzzey) {}
  
  public final void run()
  {
    synchronized (this.zzaqi)
    {
      zziz.zza(this.zzaqi, false);
      if (!this.zzaqi.zzapy.isConnected())
      {
        this.zzaqi.zzapy.zzgg().zziq().log("Connected to remote service");
        this.zzaqi.zzapy.zza(this.zzaqj);
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzjc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */