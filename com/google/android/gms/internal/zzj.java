package com.google.android.gms.internal;

final class zzj
  implements Runnable
{
  private final zzp zzt;
  private final zzt zzu;
  private final Runnable zzv;
  
  public zzj(zzh paramzzh, zzp paramzzp, zzt paramzzt, Runnable paramRunnable)
  {
    this.zzt = paramzzp;
    this.zzu = paramzzt;
    this.zzv = paramRunnable;
  }
  
  public final void run()
  {
    int i;
    if (this.zzu.zzaf == null)
    {
      i = 1;
      if (i == 0) {
        break label71;
      }
      this.zzt.zza(this.zzu.result);
      label30:
      if (!this.zzu.zzag) {
        break label88;
      }
      this.zzt.zzb("intermediate-response");
    }
    for (;;)
    {
      if (this.zzv != null) {
        this.zzv.run();
      }
      return;
      i = 0;
      break;
      label71:
      this.zzt.zzb(this.zzu.zzaf);
      break label30;
      label88:
      this.zzt.zzc("done");
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */