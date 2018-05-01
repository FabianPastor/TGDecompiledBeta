package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcmg
  extends zzfjm<zzcmg>
{
  private static volatile zzcmg[] zzjmr;
  public String name = null;
  public String zzgcc = null;
  private Float zzjjk = null;
  public Double zzjjl = null;
  public Long zzjll = null;
  public Long zzjms = null;
  
  public zzcmg()
  {
    this.zzpnc = null;
    this.zzpfd = -1;
  }
  
  public static zzcmg[] zzbbk()
  {
    if (zzjmr == null) {}
    synchronized (zzfjq.zzpnk)
    {
      if (zzjmr == null) {
        zzjmr = new zzcmg[0];
      }
      return zzjmr;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzcmg)) {
        return false;
      }
      paramObject = (zzcmg)paramObject;
      if (this.zzjms == null)
      {
        if (((zzcmg)paramObject).zzjms != null) {
          return false;
        }
      }
      else if (!this.zzjms.equals(((zzcmg)paramObject).zzjms)) {
        return false;
      }
      if (this.name == null)
      {
        if (((zzcmg)paramObject).name != null) {
          return false;
        }
      }
      else if (!this.name.equals(((zzcmg)paramObject).name)) {
        return false;
      }
      if (this.zzgcc == null)
      {
        if (((zzcmg)paramObject).zzgcc != null) {
          return false;
        }
      }
      else if (!this.zzgcc.equals(((zzcmg)paramObject).zzgcc)) {
        return false;
      }
      if (this.zzjll == null)
      {
        if (((zzcmg)paramObject).zzjll != null) {
          return false;
        }
      }
      else if (!this.zzjll.equals(((zzcmg)paramObject).zzjll)) {
        return false;
      }
      if (this.zzjjk == null)
      {
        if (((zzcmg)paramObject).zzjjk != null) {
          return false;
        }
      }
      else if (!this.zzjjk.equals(((zzcmg)paramObject).zzjjk)) {
        return false;
      }
      if (this.zzjjl == null)
      {
        if (((zzcmg)paramObject).zzjjl != null) {
          return false;
        }
      }
      else if (!this.zzjjl.equals(((zzcmg)paramObject).zzjjl)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzcmg)paramObject).zzpnc == null) || (((zzcmg)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzcmg)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int i3 = 0;
    int i4 = getClass().getName().hashCode();
    int i;
    int j;
    label33:
    int k;
    label42:
    int m;
    label52:
    int n;
    label62:
    int i1;
    if (this.zzjms == null)
    {
      i = 0;
      if (this.name != null) {
        break label154;
      }
      j = 0;
      if (this.zzgcc != null) {
        break label165;
      }
      k = 0;
      if (this.zzjll != null) {
        break label176;
      }
      m = 0;
      if (this.zzjjk != null) {
        break label188;
      }
      n = 0;
      if (this.zzjjl != null) {
        break label200;
      }
      i1 = 0;
      label72:
      i2 = i3;
      if (this.zzpnc != null) {
        if (!this.zzpnc.isEmpty()) {
          break label212;
        }
      }
    }
    label154:
    label165:
    label176:
    label188:
    label200:
    label212:
    for (int i2 = i3;; i2 = this.zzpnc.hashCode())
    {
      return (i1 + (n + (m + (k + (j + (i + (i4 + 527) * 31) * 31) * 31) * 31) * 31) * 31) * 31 + i2;
      i = this.zzjms.hashCode();
      break;
      j = this.name.hashCode();
      break label33;
      k = this.zzgcc.hashCode();
      break label42;
      m = this.zzjll.hashCode();
      break label52;
      n = this.zzjjk.hashCode();
      break label62;
      i1 = this.zzjjl.hashCode();
      break label72;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    if (this.zzjms != null) {
      paramzzfjk.zzf(1, this.zzjms.longValue());
    }
    if (this.name != null) {
      paramzzfjk.zzn(2, this.name);
    }
    if (this.zzgcc != null) {
      paramzzfjk.zzn(3, this.zzgcc);
    }
    if (this.zzjll != null) {
      paramzzfjk.zzf(4, this.zzjll.longValue());
    }
    if (this.zzjjk != null) {
      paramzzfjk.zzc(5, this.zzjjk.floatValue());
    }
    if (this.zzjjl != null) {
      paramzzfjk.zza(6, this.zzjjl.doubleValue());
    }
    super.zza(paramzzfjk);
  }
  
  protected final int zzq()
  {
    int j = super.zzq();
    int i = j;
    if (this.zzjms != null) {
      i = j + zzfjk.zzc(1, this.zzjms.longValue());
    }
    j = i;
    if (this.name != null) {
      j = i + zzfjk.zzo(2, this.name);
    }
    i = j;
    if (this.zzgcc != null) {
      i = j + zzfjk.zzo(3, this.zzgcc);
    }
    j = i;
    if (this.zzjll != null) {
      j = i + zzfjk.zzc(4, this.zzjll.longValue());
    }
    i = j;
    if (this.zzjjk != null)
    {
      this.zzjjk.floatValue();
      i = j + (zzfjk.zzlg(5) + 4);
    }
    j = i;
    if (this.zzjjl != null)
    {
      this.zzjjl.doubleValue();
      j = i + (zzfjk.zzlg(6) + 8);
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcmg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */