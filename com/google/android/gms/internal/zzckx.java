package com.google.android.gms.internal;

final class zzckx
  implements Runnable
{
  zzckx(zzcku paramzzcku, zzche paramzzche) {}
  
  public final void run()
  {
    synchronized (this.zzjit)
    {
      zzcku.zza(this.zzjit, false);
      if (!this.zzjit.zzjij.isConnected())
      {
        this.zzjit.zzjij.zzawy().zzazi().log("Connected to remote service");
        this.zzjit.zzjij.zza(this.zzjiu);
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzckx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */