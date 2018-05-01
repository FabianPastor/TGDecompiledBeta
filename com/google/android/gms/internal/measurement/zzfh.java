package com.google.android.gms.internal.measurement;

final class zzfh
  implements Runnable
{
  zzfh(zzfg paramzzfg, int paramInt, String paramString, Object paramObject1, Object paramObject2, Object paramObject3) {}
  
  public final void run()
  {
    zzfr localzzfr = this.zzajc.zzacr.zzgh();
    if (!localzzfr.isInitialized())
    {
      this.zzajc.zza(6, "Persisted config not initialized. Not logging error/warn");
      return;
    }
    if (zzfg.zza(this.zzajc) == 0)
    {
      if (!this.zzajc.zzgi().zzds()) {
        break label227;
      }
      zzfg.zza(this.zzajc, 'C');
    }
    for (;;)
    {
      if (zzfg.zzb(this.zzajc) < 0L) {
        zzfg.zza(this.zzajc, 12451L);
      }
      char c1 = "01VDIWEA?".charAt(this.zzaix);
      char c2 = zzfg.zza(this.zzajc);
      long l = zzfg.zzb(this.zzajc);
      Object localObject = zzfg.zza(true, this.zzaiy, this.zzaiz, this.zzaja, this.zzajb);
      String str = String.valueOf(localObject).length() + 24 + "2" + c1 + c2 + l + ":" + (String)localObject;
      localObject = str;
      if (str.length() > 1024) {
        localObject = this.zzaiy.substring(0, 1024);
      }
      localzzfr.zzajs.zzc((String)localObject, 1L);
      break;
      label227:
      zzfg.zza(this.zzajc, 'c');
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzfh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */