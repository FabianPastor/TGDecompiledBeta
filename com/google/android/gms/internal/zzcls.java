package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcls
  extends zzfjm<zzcls>
{
  private static volatile zzcls[] zzjjv;
  public Integer zzjjw = null;
  public String zzjjx = null;
  public zzclt[] zzjjy = zzclt.zzbbc();
  private Boolean zzjjz = null;
  public zzclu zzjka = null;
  
  public zzcls()
  {
    this.zzpnc = null;
    this.zzpfd = -1;
  }
  
  public static zzcls[] zzbbb()
  {
    if (zzjjv == null) {}
    synchronized (zzfjq.zzpnk)
    {
      if (zzjjv == null) {
        zzjjv = new zzcls[0];
      }
      return zzjjv;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzcls)) {
        return false;
      }
      paramObject = (zzcls)paramObject;
      if (this.zzjjw == null)
      {
        if (((zzcls)paramObject).zzjjw != null) {
          return false;
        }
      }
      else if (!this.zzjjw.equals(((zzcls)paramObject).zzjjw)) {
        return false;
      }
      if (this.zzjjx == null)
      {
        if (((zzcls)paramObject).zzjjx != null) {
          return false;
        }
      }
      else if (!this.zzjjx.equals(((zzcls)paramObject).zzjjx)) {
        return false;
      }
      if (!zzfjq.equals(this.zzjjy, ((zzcls)paramObject).zzjjy)) {
        return false;
      }
      if (this.zzjjz == null)
      {
        if (((zzcls)paramObject).zzjjz != null) {
          return false;
        }
      }
      else if (!this.zzjjz.equals(((zzcls)paramObject).zzjjz)) {
        return false;
      }
      if (this.zzjka == null)
      {
        if (((zzcls)paramObject).zzjka != null) {
          return false;
        }
      }
      else if (!this.zzjka.equals(((zzcls)paramObject).zzjka)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzcls)paramObject).zzpnc == null) || (((zzcls)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzcls)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int i1 = 0;
    int i2 = getClass().getName().hashCode();
    int i;
    int j;
    label33:
    int i3;
    int k;
    label51:
    zzclu localzzclu;
    int m;
    if (this.zzjjw == null)
    {
      i = 0;
      if (this.zzjjx != null) {
        break label141;
      }
      j = 0;
      i3 = zzfjq.hashCode(this.zzjjy);
      if (this.zzjjz != null) {
        break label152;
      }
      k = 0;
      localzzclu = this.zzjka;
      if (localzzclu != null) {
        break label163;
      }
      m = 0;
      label65:
      n = i1;
      if (this.zzpnc != null) {
        if (!this.zzpnc.isEmpty()) {
          break label173;
        }
      }
    }
    label141:
    label152:
    label163:
    label173:
    for (int n = i1;; n = this.zzpnc.hashCode())
    {
      return (m + (k + ((j + (i + (i2 + 527) * 31) * 31) * 31 + i3) * 31) * 31) * 31 + n;
      i = this.zzjjw.hashCode();
      break;
      j = this.zzjjx.hashCode();
      break label33;
      k = this.zzjjz.hashCode();
      break label51;
      m = localzzclu.hashCode();
      break label65;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    if (this.zzjjw != null) {
      paramzzfjk.zzaa(1, this.zzjjw.intValue());
    }
    if (this.zzjjx != null) {
      paramzzfjk.zzn(2, this.zzjjx);
    }
    if ((this.zzjjy != null) && (this.zzjjy.length > 0))
    {
      int i = 0;
      while (i < this.zzjjy.length)
      {
        zzclt localzzclt = this.zzjjy[i];
        if (localzzclt != null) {
          paramzzfjk.zza(3, localzzclt);
        }
        i += 1;
      }
    }
    if (this.zzjjz != null) {
      paramzzfjk.zzl(4, this.zzjjz.booleanValue());
    }
    if (this.zzjka != null) {
      paramzzfjk.zza(5, this.zzjka);
    }
    super.zza(paramzzfjk);
  }
  
  protected final int zzq()
  {
    int i = super.zzq();
    int j = i;
    if (this.zzjjw != null) {
      j = i + zzfjk.zzad(1, this.zzjjw.intValue());
    }
    i = j;
    if (this.zzjjx != null) {
      i = j + zzfjk.zzo(2, this.zzjjx);
    }
    j = i;
    if (this.zzjjy != null)
    {
      j = i;
      if (this.zzjjy.length > 0)
      {
        j = 0;
        while (j < this.zzjjy.length)
        {
          zzclt localzzclt = this.zzjjy[j];
          int k = i;
          if (localzzclt != null) {
            k = i + zzfjk.zzb(3, localzzclt);
          }
          j += 1;
          i = k;
        }
        j = i;
      }
    }
    i = j;
    if (this.zzjjz != null)
    {
      this.zzjjz.booleanValue();
      i = j + (zzfjk.zzlg(4) + 1);
    }
    j = i;
    if (this.zzjka != null) {
      j = i + zzfjk.zzb(5, this.zzjka);
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcls.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */