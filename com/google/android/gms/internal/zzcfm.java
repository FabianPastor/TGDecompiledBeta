package com.google.android.gms.internal;

final class zzcfm
  implements Runnable
{
  zzcfm(zzcfl paramzzcfl, String paramString) {}
  
  public final void run()
  {
    zzcfw localzzcfw = this.zzbqW.zzboe.zzwG();
    if (!localzzcfw.isInitialized())
    {
      this.zzbqW.zzk(6, "Persisted config not initialized. Not logging error/warn");
      return;
    }
    localzzcfw.zzbrj.zzf(this.zzbqV, 1L);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */