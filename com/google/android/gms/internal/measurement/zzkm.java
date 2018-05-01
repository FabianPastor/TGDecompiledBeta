package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzkm
  extends zzabd<zzkm>
{
  public long[] zzauf = zzabm.zzbzy;
  public long[] zzaug = zzabm.zzbzy;
  
  public zzkm()
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
      if (!(paramObject instanceof zzkm))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzkm)paramObject;
        if (!zzabh.equals(this.zzauf, ((zzkm)paramObject).zzauf))
        {
          bool2 = false;
        }
        else if (!zzabh.equals(this.zzaug, ((zzkm)paramObject).zzaug))
        {
          bool2 = false;
        }
        else if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
        {
          bool2 = bool1;
          if (((zzkm)paramObject).zzbzh != null)
          {
            bool2 = bool1;
            if (!((zzkm)paramObject).zzbzh.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzbzh.equals(((zzkm)paramObject).zzbzh);
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = getClass().getName().hashCode();
    int j = zzabh.hashCode(this.zzauf);
    int k = zzabh.hashCode(this.zzaug);
    if ((this.zzbzh == null) || (this.zzbzh.isEmpty())) {}
    for (int m = 0;; m = this.zzbzh.hashCode()) {
      return m + (((i + 527) * 31 + j) * 31 + k) * 31;
    }
  }
  
  protected final int zza()
  {
    int i = super.zza();
    int j;
    if ((this.zzauf != null) && (this.zzauf.length > 0))
    {
      j = 0;
      k = 0;
      while (j < this.zzauf.length)
      {
        k += zzabb.zzap(this.zzauf[j]);
        j++;
      }
    }
    for (int k = i + k + this.zzauf.length * 1;; k = i)
    {
      j = k;
      if (this.zzaug != null)
      {
        j = k;
        if (this.zzaug.length > 0)
        {
          i = 0;
          j = 0;
          while (i < this.zzaug.length)
          {
            j += zzabb.zzap(this.zzaug[i]);
            i++;
          }
          j = k + j + this.zzaug.length * 1;
        }
      }
      return j;
    }
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    int i = 0;
    int j;
    if ((this.zzauf != null) && (this.zzauf.length > 0)) {
      for (j = 0; j < this.zzauf.length; j++) {
        paramzzabb.zza(1, this.zzauf[j]);
      }
    }
    if ((this.zzaug != null) && (this.zzaug.length > 0)) {
      for (j = i; j < this.zzaug.length; j++) {
        paramzzabb.zza(2, this.zzaug[j]);
      }
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzkm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */