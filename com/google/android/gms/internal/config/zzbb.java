package com.google.android.gms.internal.config;

import java.io.IOException;

public abstract class zzbb<M extends zzbb<M>>
  extends zzbh
{
  protected zzbd zzci;
  
  public void zza(zzaz paramzzaz)
    throws IOException
  {
    if (this.zzci == null) {}
    for (;;)
    {
      return;
      for (int i = 0; i < this.zzci.size(); i++) {
        this.zzci.zzp(i).zza(paramzzaz);
      }
    }
  }
  
  protected final boolean zza(zzay paramzzay, int paramInt)
    throws IOException
  {
    int i = paramzzay.getPosition();
    boolean bool;
    if (!paramzzay.zzh(paramInt))
    {
      bool = false;
      return bool;
    }
    int j = paramInt >>> 3;
    zzbj localzzbj = new zzbj(paramInt, paramzzay.zza(i, paramzzay.getPosition() - i));
    paramzzay = null;
    if (this.zzci == null) {
      this.zzci = new zzbd();
    }
    for (;;)
    {
      Object localObject = paramzzay;
      if (paramzzay == null)
      {
        localObject = new zzbe();
        this.zzci.zza(j, (zzbe)localObject);
      }
      ((zzbe)localObject).zza(localzzbj);
      bool = true;
      break;
      paramzzay = this.zzci.zzo(j);
    }
  }
  
  protected int zzu()
  {
    if (this.zzci != null)
    {
      int i = 0;
      for (int j = 0;; j = k + j)
      {
        k = j;
        if (i >= this.zzci.size()) {
          break;
        }
        k = this.zzci.zzp(i).zzu();
        i++;
      }
    }
    int k = 0;
    return k;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzbb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */