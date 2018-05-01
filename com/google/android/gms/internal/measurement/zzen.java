package com.google.android.gms.internal.measurement;

final class zzen
  implements Runnable
{
  zzen(zzem paramzzem, zzgl paramzzgl) {}
  
  public final void run()
  {
    this.zzafi.zzgf();
    if (zzgg.isMainThread()) {
      zzem.zza(this.zzafj).zzgf().zzc(this);
    }
    for (;;)
    {
      return;
      boolean bool = this.zzafj.zzef();
      zzem.zza(this.zzafj, 0L);
      if ((bool) && (zzem.zzb(this.zzafj))) {
        this.zzafj.run();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzen.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */