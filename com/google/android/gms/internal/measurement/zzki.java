package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzki
  extends zzabd<zzki>
{
  private static volatile zzki[] zzasu;
  public Integer count = null;
  public String name = null;
  public zzkj[] zzasv = zzkj.zzle();
  public Long zzasw = null;
  public Long zzasx = null;
  
  public zzki()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  public static zzki[] zzld()
  {
    if (zzasu == null) {}
    synchronized (zzabh.zzbzr)
    {
      if (zzasu == null) {
        zzasu = new zzki[0];
      }
      return zzasu;
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
      if (!(paramObject instanceof zzki))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzki)paramObject;
        if (!zzabh.equals(this.zzasv, ((zzki)paramObject).zzasv))
        {
          bool2 = false;
        }
        else
        {
          if (this.name == null)
          {
            if (((zzki)paramObject).name != null) {
              bool2 = false;
            }
          }
          else if (!this.name.equals(((zzki)paramObject).name))
          {
            bool2 = false;
            continue;
          }
          if (this.zzasw == null)
          {
            if (((zzki)paramObject).zzasw != null) {
              bool2 = false;
            }
          }
          else if (!this.zzasw.equals(((zzki)paramObject).zzasw))
          {
            bool2 = false;
            continue;
          }
          if (this.zzasx == null)
          {
            if (((zzki)paramObject).zzasx != null) {
              bool2 = false;
            }
          }
          else if (!this.zzasx.equals(((zzki)paramObject).zzasx))
          {
            bool2 = false;
            continue;
          }
          if (this.count == null)
          {
            if (((zzki)paramObject).count != null) {
              bool2 = false;
            }
          }
          else if (!this.count.equals(((zzki)paramObject).count))
          {
            bool2 = false;
            continue;
          }
          if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
          {
            bool2 = bool1;
            if (((zzki)paramObject).zzbzh != null)
            {
              bool2 = bool1;
              if (!((zzki)paramObject).zzbzh.isEmpty()) {
                bool2 = false;
              }
            }
          }
          else
          {
            bool2 = this.zzbzh.equals(((zzki)paramObject).zzbzh);
          }
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = 0;
    int j = getClass().getName().hashCode();
    int k = zzabh.hashCode(this.zzasv);
    int m;
    int n;
    label41:
    int i1;
    label51:
    int i2;
    if (this.name == null)
    {
      m = 0;
      if (this.zzasw != null) {
        break label137;
      }
      n = 0;
      if (this.zzasx != null) {
        break label149;
      }
      i1 = 0;
      if (this.count != null) {
        break label161;
      }
      i2 = 0;
      label61:
      i3 = i;
      if (this.zzbzh != null) {
        if (!this.zzbzh.isEmpty()) {
          break label173;
        }
      }
    }
    label137:
    label149:
    label161:
    label173:
    for (int i3 = i;; i3 = this.zzbzh.hashCode())
    {
      return (i2 + (i1 + (n + (m + ((j + 527) * 31 + k) * 31) * 31) * 31) * 31) * 31 + i3;
      m = this.name.hashCode();
      break;
      n = this.zzasw.hashCode();
      break label41;
      i1 = this.zzasx.hashCode();
      break label51;
      i2 = this.count.hashCode();
      break label61;
    }
  }
  
  protected final int zza()
  {
    int i = super.zza();
    int j = i;
    if (this.zzasv != null)
    {
      j = i;
      if (this.zzasv.length > 0)
      {
        k = 0;
        for (;;)
        {
          j = i;
          if (k >= this.zzasv.length) {
            break;
          }
          zzkj localzzkj = this.zzasv[k];
          j = i;
          if (localzzkj != null) {
            j = i + zzabb.zzb(1, localzzkj);
          }
          k++;
          i = j;
        }
      }
    }
    int k = j;
    if (this.name != null) {
      k = j + zzabb.zzd(2, this.name);
    }
    i = k;
    if (this.zzasw != null) {
      i = k + zzabb.zzc(3, this.zzasw.longValue());
    }
    j = i;
    if (this.zzasx != null) {
      j = i + zzabb.zzc(4, this.zzasx.longValue());
    }
    i = j;
    if (this.count != null) {
      i = j + zzabb.zzf(5, this.count.intValue());
    }
    return i;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    if ((this.zzasv != null) && (this.zzasv.length > 0)) {
      for (int i = 0; i < this.zzasv.length; i++)
      {
        zzkj localzzkj = this.zzasv[i];
        if (localzzkj != null) {
          paramzzabb.zza(1, localzzkj);
        }
      }
    }
    if (this.name != null) {
      paramzzabb.zzc(2, this.name);
    }
    if (this.zzasw != null) {
      paramzzabb.zzb(3, this.zzasw.longValue());
    }
    if (this.zzasx != null) {
      paramzzabb.zzb(4, this.zzasx.longValue());
    }
    if (this.count != null) {
      paramzzabb.zze(5, this.count.intValue());
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzki.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */