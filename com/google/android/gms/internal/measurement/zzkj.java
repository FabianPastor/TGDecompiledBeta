package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzkj
  extends zzabd<zzkj>
{
  private static volatile zzkj[] zzasy;
  public String name = null;
  public String zzajf = null;
  private Float zzaqw = null;
  public Double zzaqx = null;
  public Long zzasz = null;
  
  public zzkj()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  public static zzkj[] zzle()
  {
    if (zzasy == null) {}
    synchronized (zzabh.zzbzr)
    {
      if (zzasy == null) {
        zzasy = new zzkj[0];
      }
      return zzasy;
    }
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
      if (!(paramObject instanceof zzkj))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzkj)paramObject;
        if (this.name == null)
        {
          if (((zzkj)paramObject).name != null) {
            bool2 = false;
          }
        }
        else if (!this.name.equals(((zzkj)paramObject).name))
        {
          bool2 = false;
          continue;
        }
        if (this.zzajf == null)
        {
          if (((zzkj)paramObject).zzajf != null) {
            bool2 = false;
          }
        }
        else if (!this.zzajf.equals(((zzkj)paramObject).zzajf))
        {
          bool2 = false;
          continue;
        }
        if (this.zzasz == null)
        {
          if (((zzkj)paramObject).zzasz != null) {
            bool2 = false;
          }
        }
        else if (!this.zzasz.equals(((zzkj)paramObject).zzasz))
        {
          bool2 = false;
          continue;
        }
        if (this.zzaqw == null)
        {
          if (((zzkj)paramObject).zzaqw != null) {
            bool2 = false;
          }
        }
        else if (!this.zzaqw.equals(((zzkj)paramObject).zzaqw))
        {
          bool2 = false;
          continue;
        }
        if (this.zzaqx == null)
        {
          if (((zzkj)paramObject).zzaqx != null) {
            bool2 = false;
          }
        }
        else if (!this.zzaqx.equals(((zzkj)paramObject).zzaqx))
        {
          bool2 = false;
          continue;
        }
        if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
        {
          bool2 = bool1;
          if (((zzkj)paramObject).zzbzh != null)
          {
            bool2 = bool1;
            if (!((zzkj)paramObject).zzbzh.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzbzh.equals(((zzkj)paramObject).zzbzh);
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = 0;
    int j = getClass().getName().hashCode();
    int k;
    int m;
    label32:
    int n;
    label42:
    int i1;
    label52:
    int i2;
    if (this.name == null)
    {
      k = 0;
      if (this.zzajf != null) {
        break label137;
      }
      m = 0;
      if (this.zzasz != null) {
        break label149;
      }
      n = 0;
      if (this.zzaqw != null) {
        break label161;
      }
      i1 = 0;
      if (this.zzaqx != null) {
        break label173;
      }
      i2 = 0;
      label62:
      i3 = i;
      if (this.zzbzh != null) {
        if (!this.zzbzh.isEmpty()) {
          break label185;
        }
      }
    }
    label137:
    label149:
    label161:
    label173:
    label185:
    for (int i3 = i;; i3 = this.zzbzh.hashCode())
    {
      return (i2 + (i1 + (n + (m + (k + (j + 527) * 31) * 31) * 31) * 31) * 31) * 31 + i3;
      k = this.name.hashCode();
      break;
      m = this.zzajf.hashCode();
      break label32;
      n = this.zzasz.hashCode();
      break label42;
      i1 = this.zzaqw.hashCode();
      break label52;
      i2 = this.zzaqx.hashCode();
      break label62;
    }
  }
  
  protected final int zza()
  {
    int i = super.zza();
    int j = i;
    if (this.name != null) {
      j = i + zzabb.zzd(1, this.name);
    }
    i = j;
    if (this.zzajf != null) {
      i = j + zzabb.zzd(2, this.zzajf);
    }
    j = i;
    if (this.zzasz != null) {
      j = i + zzabb.zzc(3, this.zzasz.longValue());
    }
    i = j;
    if (this.zzaqw != null)
    {
      this.zzaqw.floatValue();
      i = j + (zzabb.zzas(4) + 4);
    }
    j = i;
    if (this.zzaqx != null)
    {
      this.zzaqx.doubleValue();
      j = i + (zzabb.zzas(5) + 8);
    }
    return j;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    if (this.name != null) {
      paramzzabb.zzc(1, this.name);
    }
    if (this.zzajf != null) {
      paramzzabb.zzc(2, this.zzajf);
    }
    if (this.zzasz != null) {
      paramzzabb.zzb(3, this.zzasz.longValue());
    }
    if (this.zzaqw != null) {
      paramzzabb.zza(4, this.zzaqw.floatValue());
    }
    if (this.zzaqx != null) {
      paramzzabb.zza(5, this.zzaqx.doubleValue());
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzkj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */