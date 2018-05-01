package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcly
  extends zzfjm<zzcly>
{
  public String zzixs = null;
  public Long zzjkw = null;
  private Integer zzjkx = null;
  public zzclz[] zzjky = zzclz.zzbbf();
  public zzclx[] zzjkz = zzclx.zzbbe();
  public zzclr[] zzjla = zzclr.zzbba();
  
  public zzcly()
  {
    this.zzpnc = null;
    this.zzpfd = -1;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzcly)) {
        return false;
      }
      paramObject = (zzcly)paramObject;
      if (this.zzjkw == null)
      {
        if (((zzcly)paramObject).zzjkw != null) {
          return false;
        }
      }
      else if (!this.zzjkw.equals(((zzcly)paramObject).zzjkw)) {
        return false;
      }
      if (this.zzixs == null)
      {
        if (((zzcly)paramObject).zzixs != null) {
          return false;
        }
      }
      else if (!this.zzixs.equals(((zzcly)paramObject).zzixs)) {
        return false;
      }
      if (this.zzjkx == null)
      {
        if (((zzcly)paramObject).zzjkx != null) {
          return false;
        }
      }
      else if (!this.zzjkx.equals(((zzcly)paramObject).zzjkx)) {
        return false;
      }
      if (!zzfjq.equals(this.zzjky, ((zzcly)paramObject).zzjky)) {
        return false;
      }
      if (!zzfjq.equals(this.zzjkz, ((zzcly)paramObject).zzjkz)) {
        return false;
      }
      if (!zzfjq.equals(this.zzjla, ((zzcly)paramObject).zzjla)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzcly)paramObject).zzpnc == null) || (((zzcly)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzcly)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int n = 0;
    int i1 = getClass().getName().hashCode();
    int i;
    int j;
    label33:
    int k;
    label42:
    int i2;
    int i3;
    int i4;
    if (this.zzjkw == null)
    {
      i = 0;
      if (this.zzixs != null) {
        break label151;
      }
      j = 0;
      if (this.zzjkx != null) {
        break label162;
      }
      k = 0;
      i2 = zzfjq.hashCode(this.zzjky);
      i3 = zzfjq.hashCode(this.zzjkz);
      i4 = zzfjq.hashCode(this.zzjla);
      m = n;
      if (this.zzpnc != null) {
        if (!this.zzpnc.isEmpty()) {
          break label173;
        }
      }
    }
    label151:
    label162:
    label173:
    for (int m = n;; m = this.zzpnc.hashCode())
    {
      return ((((k + (j + (i + (i1 + 527) * 31) * 31) * 31) * 31 + i2) * 31 + i3) * 31 + i4) * 31 + m;
      i = this.zzjkw.hashCode();
      break;
      j = this.zzixs.hashCode();
      break label33;
      k = this.zzjkx.hashCode();
      break label42;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    int j = 0;
    if (this.zzjkw != null) {
      paramzzfjk.zzf(1, this.zzjkw.longValue());
    }
    if (this.zzixs != null) {
      paramzzfjk.zzn(2, this.zzixs);
    }
    if (this.zzjkx != null) {
      paramzzfjk.zzaa(3, this.zzjkx.intValue());
    }
    int i;
    Object localObject;
    if ((this.zzjky != null) && (this.zzjky.length > 0))
    {
      i = 0;
      while (i < this.zzjky.length)
      {
        localObject = this.zzjky[i];
        if (localObject != null) {
          paramzzfjk.zza(4, (zzfjs)localObject);
        }
        i += 1;
      }
    }
    if ((this.zzjkz != null) && (this.zzjkz.length > 0))
    {
      i = 0;
      while (i < this.zzjkz.length)
      {
        localObject = this.zzjkz[i];
        if (localObject != null) {
          paramzzfjk.zza(5, (zzfjs)localObject);
        }
        i += 1;
      }
    }
    if ((this.zzjla != null) && (this.zzjla.length > 0))
    {
      i = j;
      while (i < this.zzjla.length)
      {
        localObject = this.zzjla[i];
        if (localObject != null) {
          paramzzfjk.zza(6, (zzfjs)localObject);
        }
        i += 1;
      }
    }
    super.zza(paramzzfjk);
  }
  
  protected final int zzq()
  {
    int m = 0;
    int j = super.zzq();
    int i = j;
    if (this.zzjkw != null) {
      i = j + zzfjk.zzc(1, this.zzjkw.longValue());
    }
    j = i;
    if (this.zzixs != null) {
      j = i + zzfjk.zzo(2, this.zzixs);
    }
    i = j;
    if (this.zzjkx != null) {
      i = j + zzfjk.zzad(3, this.zzjkx.intValue());
    }
    j = i;
    Object localObject;
    if (this.zzjky != null)
    {
      j = i;
      if (this.zzjky.length > 0)
      {
        j = 0;
        while (j < this.zzjky.length)
        {
          localObject = this.zzjky[j];
          k = i;
          if (localObject != null) {
            k = i + zzfjk.zzb(4, (zzfjs)localObject);
          }
          j += 1;
          i = k;
        }
        j = i;
      }
    }
    i = j;
    if (this.zzjkz != null)
    {
      i = j;
      if (this.zzjkz.length > 0)
      {
        i = j;
        j = 0;
        while (j < this.zzjkz.length)
        {
          localObject = this.zzjkz[j];
          k = i;
          if (localObject != null) {
            k = i + zzfjk.zzb(5, (zzfjs)localObject);
          }
          j += 1;
          i = k;
        }
      }
    }
    int k = i;
    if (this.zzjla != null)
    {
      k = i;
      if (this.zzjla.length > 0)
      {
        j = m;
        for (;;)
        {
          k = i;
          if (j >= this.zzjla.length) {
            break;
          }
          localObject = this.zzjla[j];
          k = i;
          if (localObject != null) {
            k = i + zzfjk.zzb(6, (zzfjs)localObject);
          }
          j += 1;
          i = k;
        }
      }
    }
    return k;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcly.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */