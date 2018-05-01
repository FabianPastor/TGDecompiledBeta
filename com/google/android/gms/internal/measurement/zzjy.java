package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzjy
  extends zzabd<zzjy>
{
  private static volatile zzjy[] zzarf;
  public Integer zzarg = null;
  public zzkc[] zzarh = zzkc.zzkz();
  public zzjz[] zzari = zzjz.zzkx();
  
  public zzjy()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  public static zzjy[] zzkw()
  {
    if (zzarf == null) {}
    synchronized (zzabh.zzbzr)
    {
      if (zzarf == null) {
        zzarf = new zzjy[0];
      }
      return zzarf;
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
      if (!(paramObject instanceof zzjy))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzjy)paramObject;
        if (this.zzarg == null)
        {
          if (((zzjy)paramObject).zzarg != null) {
            bool2 = false;
          }
        }
        else if (!this.zzarg.equals(((zzjy)paramObject).zzarg))
        {
          bool2 = false;
          continue;
        }
        if (!zzabh.equals(this.zzarh, ((zzjy)paramObject).zzarh))
        {
          bool2 = false;
        }
        else if (!zzabh.equals(this.zzari, ((zzjy)paramObject).zzari))
        {
          bool2 = false;
        }
        else if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
        {
          bool2 = bool1;
          if (((zzjy)paramObject).zzbzh != null)
          {
            bool2 = bool1;
            if (!((zzjy)paramObject).zzbzh.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzbzh.equals(((zzjy)paramObject).zzbzh);
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
    int n;
    if (this.zzarg == null)
    {
      k = 0;
      m = zzabh.hashCode(this.zzarh);
      n = zzabh.hashCode(this.zzari);
      i1 = i;
      if (this.zzbzh != null) {
        if (!this.zzbzh.isEmpty()) {
          break label103;
        }
      }
    }
    label103:
    for (int i1 = i;; i1 = this.zzbzh.hashCode())
    {
      return (((k + (j + 527) * 31) * 31 + m) * 31 + n) * 31 + i1;
      k = this.zzarg.hashCode();
      break;
    }
  }
  
  protected final int zza()
  {
    int i = 0;
    int j = super.zza();
    int k = j;
    if (this.zzarg != null) {
      k = j + zzabb.zzf(1, this.zzarg.intValue());
    }
    j = k;
    Object localObject;
    if (this.zzarh != null)
    {
      j = k;
      if (this.zzarh.length > 0)
      {
        j = 0;
        while (j < this.zzarh.length)
        {
          localObject = this.zzarh[j];
          m = k;
          if (localObject != null) {
            m = k + zzabb.zzb(2, (zzabj)localObject);
          }
          j++;
          k = m;
        }
        j = k;
      }
    }
    int m = j;
    if (this.zzari != null)
    {
      m = j;
      if (this.zzari.length > 0)
      {
        k = i;
        for (;;)
        {
          m = j;
          if (k >= this.zzari.length) {
            break;
          }
          localObject = this.zzari[k];
          m = j;
          if (localObject != null) {
            m = j + zzabb.zzb(3, (zzabj)localObject);
          }
          k++;
          j = m;
        }
      }
    }
    return m;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    int i = 0;
    if (this.zzarg != null) {
      paramzzabb.zze(1, this.zzarg.intValue());
    }
    int j;
    Object localObject;
    if ((this.zzarh != null) && (this.zzarh.length > 0)) {
      for (j = 0; j < this.zzarh.length; j++)
      {
        localObject = this.zzarh[j];
        if (localObject != null) {
          paramzzabb.zza(2, (zzabj)localObject);
        }
      }
    }
    if ((this.zzari != null) && (this.zzari.length > 0)) {
      for (j = i; j < this.zzari.length; j++)
      {
        localObject = this.zzari[j];
        if (localObject != null) {
          paramzzabb.zza(3, (zzabj)localObject);
        }
      }
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzjy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */