package com.google.android.gms.internal.firebase_abt;

import java.io.IOException;

public abstract class zzd<M extends zzd<M>>
  extends zzj
{
  protected zzf zzs;
  
  protected final boolean zza(zza paramzza, int paramInt)
    throws IOException
  {
    int i = paramzza.getPosition();
    boolean bool;
    if (!paramzza.zzb(paramInt))
    {
      bool = false;
      return bool;
    }
    int j = paramInt >>> 3;
    zzl localzzl = new zzl(paramInt, paramzza.zza(i, paramzza.getPosition() - i));
    paramzza = null;
    if (this.zzs == null) {
      this.zzs = new zzf();
    }
    for (;;)
    {
      Object localObject = paramzza;
      if (paramzza == null)
      {
        localObject = new zzg();
        this.zzs.zza(j, (zzg)localObject);
      }
      ((zzg)localObject).zza(localzzl);
      bool = true;
      break;
      paramzza = this.zzs.zzg(j);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/firebase_abt/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */