package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzka
  extends zzabd<zzka>
{
  private static volatile zzka[] zzarp;
  public zzkd zzarq = null;
  public zzkb zzarr = null;
  public Boolean zzars = null;
  public String zzart = null;
  
  public zzka()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  public static zzka[] zzky()
  {
    if (zzarp == null) {}
    synchronized (zzabh.zzbzr)
    {
      if (zzarp == null) {
        zzarp = new zzka[0];
      }
      return zzarp;
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
      if (!(paramObject instanceof zzka))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzka)paramObject;
        if (this.zzarq == null)
        {
          if (((zzka)paramObject).zzarq != null) {
            bool2 = false;
          }
        }
        else if (!this.zzarq.equals(((zzka)paramObject).zzarq))
        {
          bool2 = false;
          continue;
        }
        if (this.zzarr == null)
        {
          if (((zzka)paramObject).zzarr != null) {
            bool2 = false;
          }
        }
        else if (!this.zzarr.equals(((zzka)paramObject).zzarr))
        {
          bool2 = false;
          continue;
        }
        if (this.zzars == null)
        {
          if (((zzka)paramObject).zzars != null) {
            bool2 = false;
          }
        }
        else if (!this.zzars.equals(((zzka)paramObject).zzars))
        {
          bool2 = false;
          continue;
        }
        if (this.zzart == null)
        {
          if (((zzka)paramObject).zzart != null) {
            bool2 = false;
          }
        }
        else if (!this.zzart.equals(((zzka)paramObject).zzart))
        {
          bool2 = false;
          continue;
        }
        if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
        {
          bool2 = bool1;
          if (((zzka)paramObject).zzbzh != null)
          {
            bool2 = bool1;
            if (!((zzka)paramObject).zzbzh.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzbzh.equals(((zzka)paramObject).zzbzh);
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = 0;
    int j = getClass().getName().hashCode();
    Object localObject = this.zzarq;
    int k;
    int m;
    label37:
    int n;
    label47:
    int i1;
    if (localObject == null)
    {
      k = 0;
      localObject = this.zzarr;
      if (localObject != null) {
        break label125;
      }
      m = 0;
      if (this.zzars != null) {
        break label134;
      }
      n = 0;
      if (this.zzart != null) {
        break label146;
      }
      i1 = 0;
      label57:
      i2 = i;
      if (this.zzbzh != null) {
        if (!this.zzbzh.isEmpty()) {
          break label158;
        }
      }
    }
    label125:
    label134:
    label146:
    label158:
    for (int i2 = i;; i2 = this.zzbzh.hashCode())
    {
      return (i1 + (n + (m + (k + (j + 527) * 31) * 31) * 31) * 31) * 31 + i2;
      k = ((zzkd)localObject).hashCode();
      break;
      m = ((zzkb)localObject).hashCode();
      break label37;
      n = this.zzars.hashCode();
      break label47;
      i1 = this.zzart.hashCode();
      break label57;
    }
  }
  
  protected final int zza()
  {
    int i = super.zza();
    int j = i;
    if (this.zzarq != null) {
      j = i + zzabb.zzb(1, this.zzarq);
    }
    i = j;
    if (this.zzarr != null) {
      i = j + zzabb.zzb(2, this.zzarr);
    }
    j = i;
    if (this.zzars != null)
    {
      this.zzars.booleanValue();
      j = i + (zzabb.zzas(3) + 1);
    }
    i = j;
    if (this.zzart != null) {
      i = j + zzabb.zzd(4, this.zzart);
    }
    return i;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    if (this.zzarq != null) {
      paramzzabb.zza(1, this.zzarq);
    }
    if (this.zzarr != null) {
      paramzzabb.zza(2, this.zzarr);
    }
    if (this.zzars != null) {
      paramzzabb.zza(3, this.zzars.booleanValue());
    }
    if (this.zzart != null) {
      paramzzabb.zzc(4, this.zzart);
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzka.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */