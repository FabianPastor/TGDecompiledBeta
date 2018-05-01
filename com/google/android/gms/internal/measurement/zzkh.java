package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzkh
  extends zzabd<zzkh>
{
  private static volatile zzkh[] zzasq;
  public Integer zzarg = null;
  public zzkm zzasr = null;
  public zzkm zzass = null;
  public Boolean zzast = null;
  
  public zzkh()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  public static zzkh[] zzlc()
  {
    if (zzasq == null) {}
    synchronized (zzabh.zzbzr)
    {
      if (zzasq == null) {
        zzasq = new zzkh[0];
      }
      return zzasq;
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
      if (!(paramObject instanceof zzkh))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzkh)paramObject;
        if (this.zzarg == null)
        {
          if (((zzkh)paramObject).zzarg != null) {
            bool2 = false;
          }
        }
        else if (!this.zzarg.equals(((zzkh)paramObject).zzarg))
        {
          bool2 = false;
          continue;
        }
        if (this.zzasr == null)
        {
          if (((zzkh)paramObject).zzasr != null) {
            bool2 = false;
          }
        }
        else if (!this.zzasr.equals(((zzkh)paramObject).zzasr))
        {
          bool2 = false;
          continue;
        }
        if (this.zzass == null)
        {
          if (((zzkh)paramObject).zzass != null) {
            bool2 = false;
          }
        }
        else if (!this.zzass.equals(((zzkh)paramObject).zzass))
        {
          bool2 = false;
          continue;
        }
        if (this.zzast == null)
        {
          if (((zzkh)paramObject).zzast != null) {
            bool2 = false;
          }
        }
        else if (!this.zzast.equals(((zzkh)paramObject).zzast))
        {
          bool2 = false;
          continue;
        }
        if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
        {
          bool2 = bool1;
          if (((zzkh)paramObject).zzbzh != null)
          {
            bool2 = bool1;
            if (!((zzkh)paramObject).zzbzh.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzbzh.equals(((zzkh)paramObject).zzbzh);
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = 0;
    int j = getClass().getName().hashCode();
    int k;
    zzkm localzzkm;
    int m;
    label36:
    int n;
    label50:
    int i1;
    if (this.zzarg == null)
    {
      k = 0;
      localzzkm = this.zzasr;
      if (localzzkm != null) {
        break label129;
      }
      m = 0;
      localzzkm = this.zzass;
      if (localzzkm != null) {
        break label139;
      }
      n = 0;
      if (this.zzast != null) {
        break label149;
      }
      i1 = 0;
      label60:
      i2 = i;
      if (this.zzbzh != null) {
        if (!this.zzbzh.isEmpty()) {
          break label161;
        }
      }
    }
    label129:
    label139:
    label149:
    label161:
    for (int i2 = i;; i2 = this.zzbzh.hashCode())
    {
      return (i1 + (n + (m + (k + (j + 527) * 31) * 31) * 31) * 31) * 31 + i2;
      k = this.zzarg.hashCode();
      break;
      m = localzzkm.hashCode();
      break label36;
      n = localzzkm.hashCode();
      break label50;
      i1 = this.zzast.hashCode();
      break label60;
    }
  }
  
  protected final int zza()
  {
    int i = super.zza();
    int j = i;
    if (this.zzarg != null) {
      j = i + zzabb.zzf(1, this.zzarg.intValue());
    }
    i = j;
    if (this.zzasr != null) {
      i = j + zzabb.zzb(2, this.zzasr);
    }
    j = i;
    if (this.zzass != null) {
      j = i + zzabb.zzb(3, this.zzass);
    }
    i = j;
    if (this.zzast != null)
    {
      this.zzast.booleanValue();
      i = j + (zzabb.zzas(4) + 1);
    }
    return i;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    if (this.zzarg != null) {
      paramzzabb.zze(1, this.zzarg.intValue());
    }
    if (this.zzasr != null) {
      paramzzabb.zza(2, this.zzasr);
    }
    if (this.zzass != null) {
      paramzzabb.zza(3, this.zzass);
    }
    if (this.zzast != null) {
      paramzzabb.zza(4, this.zzast.booleanValue());
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzkh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */