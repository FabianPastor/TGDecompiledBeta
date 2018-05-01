package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzjz
  extends zzabd<zzjz>
{
  private static volatile zzjz[] zzarj;
  public Integer zzark = null;
  public String zzarl = null;
  public zzka[] zzarm = zzka.zzky();
  private Boolean zzarn = null;
  public zzkb zzaro = null;
  
  public zzjz()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  public static zzjz[] zzkx()
  {
    if (zzarj == null) {}
    synchronized (zzabh.zzbzr)
    {
      if (zzarj == null) {
        zzarj = new zzjz[0];
      }
      return zzarj;
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
      if (!(paramObject instanceof zzjz))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzjz)paramObject;
        if (this.zzark == null)
        {
          if (((zzjz)paramObject).zzark != null) {
            bool2 = false;
          }
        }
        else if (!this.zzark.equals(((zzjz)paramObject).zzark))
        {
          bool2 = false;
          continue;
        }
        if (this.zzarl == null)
        {
          if (((zzjz)paramObject).zzarl != null) {
            bool2 = false;
          }
        }
        else if (!this.zzarl.equals(((zzjz)paramObject).zzarl))
        {
          bool2 = false;
          continue;
        }
        if (!zzabh.equals(this.zzarm, ((zzjz)paramObject).zzarm))
        {
          bool2 = false;
        }
        else
        {
          if (this.zzarn == null)
          {
            if (((zzjz)paramObject).zzarn != null) {
              bool2 = false;
            }
          }
          else if (!this.zzarn.equals(((zzjz)paramObject).zzarn))
          {
            bool2 = false;
            continue;
          }
          if (this.zzaro == null)
          {
            if (((zzjz)paramObject).zzaro != null) {
              bool2 = false;
            }
          }
          else if (!this.zzaro.equals(((zzjz)paramObject).zzaro))
          {
            bool2 = false;
            continue;
          }
          if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
          {
            bool2 = bool1;
            if (((zzjz)paramObject).zzbzh != null)
            {
              bool2 = bool1;
              if (!((zzjz)paramObject).zzbzh.isEmpty()) {
                bool2 = false;
              }
            }
          }
          else
          {
            bool2 = this.zzbzh.equals(((zzjz)paramObject).zzbzh);
          }
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
    int i1;
    label51:
    zzkb localzzkb;
    int i2;
    if (this.zzark == null)
    {
      k = 0;
      if (this.zzarl != null) {
        break label140;
      }
      m = 0;
      n = zzabh.hashCode(this.zzarm);
      if (this.zzarn != null) {
        break label152;
      }
      i1 = 0;
      localzzkb = this.zzaro;
      if (localzzkb != null) {
        break label164;
      }
      i2 = 0;
      label65:
      i3 = i;
      if (this.zzbzh != null) {
        if (!this.zzbzh.isEmpty()) {
          break label174;
        }
      }
    }
    label140:
    label152:
    label164:
    label174:
    for (int i3 = i;; i3 = this.zzbzh.hashCode())
    {
      return (i2 + (i1 + ((m + (k + (j + 527) * 31) * 31) * 31 + n) * 31) * 31) * 31 + i3;
      k = this.zzark.hashCode();
      break;
      m = this.zzarl.hashCode();
      break label32;
      i1 = this.zzarn.hashCode();
      break label51;
      i2 = localzzkb.hashCode();
      break label65;
    }
  }
  
  protected final int zza()
  {
    int i = super.zza();
    int j = i;
    if (this.zzark != null) {
      j = i + zzabb.zzf(1, this.zzark.intValue());
    }
    i = j;
    if (this.zzarl != null) {
      i = j + zzabb.zzd(2, this.zzarl);
    }
    j = i;
    if (this.zzarm != null)
    {
      j = i;
      if (this.zzarm.length > 0)
      {
        int k = 0;
        while (k < this.zzarm.length)
        {
          zzka localzzka = this.zzarm[k];
          j = i;
          if (localzzka != null) {
            j = i + zzabb.zzb(3, localzzka);
          }
          k++;
          i = j;
        }
        j = i;
      }
    }
    i = j;
    if (this.zzarn != null)
    {
      this.zzarn.booleanValue();
      i = j + (zzabb.zzas(4) + 1);
    }
    j = i;
    if (this.zzaro != null) {
      j = i + zzabb.zzb(5, this.zzaro);
    }
    return j;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    if (this.zzark != null) {
      paramzzabb.zze(1, this.zzark.intValue());
    }
    if (this.zzarl != null) {
      paramzzabb.zzc(2, this.zzarl);
    }
    if ((this.zzarm != null) && (this.zzarm.length > 0)) {
      for (int i = 0; i < this.zzarm.length; i++)
      {
        zzka localzzka = this.zzarm[i];
        if (localzzka != null) {
          paramzzabb.zza(3, localzzka);
        }
      }
    }
    if (this.zzarn != null) {
      paramzzabb.zza(4, this.zzarn.booleanValue());
    }
    if (this.zzaro != null) {
      paramzzabb.zza(5, this.zzaro);
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzjz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */