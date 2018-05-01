package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzkn
  extends zzabd<zzkn>
{
  private static volatile zzkn[] zzauh;
  public String name = null;
  public String zzajf = null;
  private Float zzaqw = null;
  public Double zzaqx = null;
  public Long zzasz = null;
  public Long zzaui = null;
  
  public zzkn()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  public static zzkn[] zzlg()
  {
    if (zzauh == null) {}
    synchronized (zzabh.zzbzr)
    {
      if (zzauh == null) {
        zzauh = new zzkn[0];
      }
      return zzauh;
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
      if (!(paramObject instanceof zzkn))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzkn)paramObject;
        if (this.zzaui == null)
        {
          if (((zzkn)paramObject).zzaui != null) {
            bool2 = false;
          }
        }
        else if (!this.zzaui.equals(((zzkn)paramObject).zzaui))
        {
          bool2 = false;
          continue;
        }
        if (this.name == null)
        {
          if (((zzkn)paramObject).name != null) {
            bool2 = false;
          }
        }
        else if (!this.name.equals(((zzkn)paramObject).name))
        {
          bool2 = false;
          continue;
        }
        if (this.zzajf == null)
        {
          if (((zzkn)paramObject).zzajf != null) {
            bool2 = false;
          }
        }
        else if (!this.zzajf.equals(((zzkn)paramObject).zzajf))
        {
          bool2 = false;
          continue;
        }
        if (this.zzasz == null)
        {
          if (((zzkn)paramObject).zzasz != null) {
            bool2 = false;
          }
        }
        else if (!this.zzasz.equals(((zzkn)paramObject).zzasz))
        {
          bool2 = false;
          continue;
        }
        if (this.zzaqw == null)
        {
          if (((zzkn)paramObject).zzaqw != null) {
            bool2 = false;
          }
        }
        else if (!this.zzaqw.equals(((zzkn)paramObject).zzaqw))
        {
          bool2 = false;
          continue;
        }
        if (this.zzaqx == null)
        {
          if (((zzkn)paramObject).zzaqx != null) {
            bool2 = false;
          }
        }
        else if (!this.zzaqx.equals(((zzkn)paramObject).zzaqx))
        {
          bool2 = false;
          continue;
        }
        if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
        {
          bool2 = bool1;
          if (((zzkn)paramObject).zzbzh != null)
          {
            bool2 = bool1;
            if (!((zzkn)paramObject).zzbzh.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzbzh.equals(((zzkn)paramObject).zzbzh);
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
    label62:
    int i3;
    if (this.zzaui == null)
    {
      k = 0;
      if (this.name != null) {
        break label153;
      }
      m = 0;
      if (this.zzajf != null) {
        break label165;
      }
      n = 0;
      if (this.zzasz != null) {
        break label177;
      }
      i1 = 0;
      if (this.zzaqw != null) {
        break label189;
      }
      i2 = 0;
      if (this.zzaqx != null) {
        break label201;
      }
      i3 = 0;
      label72:
      i4 = i;
      if (this.zzbzh != null) {
        if (!this.zzbzh.isEmpty()) {
          break label213;
        }
      }
    }
    label153:
    label165:
    label177:
    label189:
    label201:
    label213:
    for (int i4 = i;; i4 = this.zzbzh.hashCode())
    {
      return (i3 + (i2 + (i1 + (n + (m + (k + (j + 527) * 31) * 31) * 31) * 31) * 31) * 31) * 31 + i4;
      k = this.zzaui.hashCode();
      break;
      m = this.name.hashCode();
      break label32;
      n = this.zzajf.hashCode();
      break label42;
      i1 = this.zzasz.hashCode();
      break label52;
      i2 = this.zzaqw.hashCode();
      break label62;
      i3 = this.zzaqx.hashCode();
      break label72;
    }
  }
  
  protected final int zza()
  {
    int i = super.zza();
    int j = i;
    if (this.zzaui != null) {
      j = i + zzabb.zzc(1, this.zzaui.longValue());
    }
    int k = j;
    if (this.name != null) {
      k = j + zzabb.zzd(2, this.name);
    }
    i = k;
    if (this.zzajf != null) {
      i = k + zzabb.zzd(3, this.zzajf);
    }
    j = i;
    if (this.zzasz != null) {
      j = i + zzabb.zzc(4, this.zzasz.longValue());
    }
    i = j;
    if (this.zzaqw != null)
    {
      this.zzaqw.floatValue();
      i = j + (zzabb.zzas(5) + 4);
    }
    j = i;
    if (this.zzaqx != null)
    {
      this.zzaqx.doubleValue();
      j = i + (zzabb.zzas(6) + 8);
    }
    return j;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    if (this.zzaui != null) {
      paramzzabb.zzb(1, this.zzaui.longValue());
    }
    if (this.name != null) {
      paramzzabb.zzc(2, this.name);
    }
    if (this.zzajf != null) {
      paramzzabb.zzc(3, this.zzajf);
    }
    if (this.zzasz != null) {
      paramzzabb.zzb(4, this.zzasz.longValue());
    }
    if (this.zzaqw != null) {
      paramzzabb.zza(5, this.zzaqw.floatValue());
    }
    if (this.zzaqx != null) {
      paramzzabb.zza(6, this.zzaqx.doubleValue());
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzkn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */