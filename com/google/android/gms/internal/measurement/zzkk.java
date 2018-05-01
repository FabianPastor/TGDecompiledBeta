package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzkk
  extends zzabd<zzkk>
{
  public zzkl[] zzata = zzkl.zzlf();
  
  public zzkk()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramObject == this) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      if (!(paramObject instanceof zzkk))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzkk)paramObject;
        if (!zzabh.equals(this.zzata, ((zzkk)paramObject).zzata))
        {
          bool2 = false;
        }
        else if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
        {
          bool2 = bool1;
          if (((zzkk)paramObject).zzbzh != null)
          {
            bool2 = bool1;
            if (!((zzkk)paramObject).zzbzh.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzbzh.equals(((zzkk)paramObject).zzbzh);
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = getClass().getName().hashCode();
    int j = zzabh.hashCode(this.zzata);
    if ((this.zzbzh == null) || (this.zzbzh.isEmpty())) {}
    for (int k = 0;; k = this.zzbzh.hashCode()) {
      return k + ((i + 527) * 31 + j) * 31;
    }
  }
  
  protected final int zza()
  {
    int i = super.zza();
    int j = i;
    if (this.zzata != null)
    {
      j = i;
      if (this.zzata.length > 0)
      {
        int k = 0;
        for (;;)
        {
          j = i;
          if (k >= this.zzata.length) {
            break;
          }
          zzkl localzzkl = this.zzata[k];
          j = i;
          if (localzzkl != null) {
            j = i + zzabb.zzb(1, localzzkl);
          }
          k++;
          i = j;
        }
      }
    }
    return j;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    if ((this.zzata != null) && (this.zzata.length > 0)) {
      for (int i = 0; i < this.zzata.length; i++)
      {
        zzkl localzzkl = this.zzata[i];
        if (localzzkl != null) {
          paramzzabb.zza(1, localzzkl);
        }
      }
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzkk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */