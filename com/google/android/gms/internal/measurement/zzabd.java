package com.google.android.gms.internal.measurement;

import java.io.IOException;

public abstract class zzabd<M extends zzabd<M>>
  extends zzabj
{
  protected zzabf zzbzh;
  
  protected int zza()
  {
    if (this.zzbzh != null)
    {
      int i = 0;
      for (int j = 0;; j = k + j)
      {
        k = j;
        if (i >= this.zzbzh.size()) {
          break;
        }
        k = this.zzbzh.zzaw(i).zza();
        i++;
      }
    }
    int k = 0;
    return k;
  }
  
  public void zza(zzabb paramzzabb)
    throws IOException
  {
    if (this.zzbzh == null) {}
    for (;;)
    {
      return;
      for (int i = 0; i < this.zzbzh.size(); i++) {
        this.zzbzh.zzaw(i).zza(paramzzabb);
      }
    }
  }
  
  protected final boolean zza(zzaba paramzzaba, int paramInt)
    throws IOException
  {
    int i = paramzzaba.getPosition();
    boolean bool;
    if (!paramzzaba.zzam(paramInt))
    {
      bool = false;
      return bool;
    }
    int j = paramInt >>> 3;
    zzabl localzzabl = new zzabl(paramInt, paramzzaba.zzc(i, paramzzaba.getPosition() - i));
    paramzzaba = null;
    if (this.zzbzh == null) {
      this.zzbzh = new zzabf();
    }
    for (;;)
    {
      Object localObject = paramzzaba;
      if (paramzzaba == null)
      {
        localObject = new zzabg();
        this.zzbzh.zza(j, (zzabg)localObject);
      }
      ((zzabg)localObject).zza(localzzabl);
      bool = true;
      break;
      paramzzaba = this.zzbzh.zzav(j);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzabd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */