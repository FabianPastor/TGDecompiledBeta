package com.google.android.gms.internal;

final class zzchn
  implements Runnable
{
  zzchn(zzchm paramzzchm, String paramString) {}
  
  public final void run()
  {
    zzchx localzzchx = this.zzjce.zziwf.zzawz();
    if (!localzzchx.isInitialized())
    {
      this.zzjce.zzk(6, "Persisted config not initialized. Not logging error/warn");
      return;
    }
    localzzchx.zzjcq.zzf(this.zzjcd, 1L);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzchn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */