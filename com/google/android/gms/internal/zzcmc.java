package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcmc
  extends zzfjm<zzcmc>
{
  private static volatile zzcmc[] zzjlk;
  public String name = null;
  public String zzgcc = null;
  private Float zzjjk = null;
  public Double zzjjl = null;
  public Long zzjll = null;
  
  public zzcmc()
  {
    this.zzpnc = null;
    this.zzpfd = -1;
  }
  
  public static zzcmc[] zzbbi()
  {
    if (zzjlk == null) {}
    synchronized (zzfjq.zzpnk)
    {
      if (zzjlk == null) {
        zzjlk = new zzcmc[0];
      }
      return zzjlk;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzcmc)) {
        return false;
      }
      paramObject = (zzcmc)paramObject;
      if (this.name == null)
      {
        if (((zzcmc)paramObject).name != null) {
          return false;
        }
      }
      else if (!this.name.equals(((zzcmc)paramObject).name)) {
        return false;
      }
      if (this.zzgcc == null)
      {
        if (((zzcmc)paramObject).zzgcc != null) {
          return false;
        }
      }
      else if (!this.zzgcc.equals(((zzcmc)paramObject).zzgcc)) {
        return false;
      }
      if (this.zzjll == null)
      {
        if (((zzcmc)paramObject).zzjll != null) {
          return false;
        }
      }
      else if (!this.zzjll.equals(((zzcmc)paramObject).zzjll)) {
        return false;
      }
      if (this.zzjjk == null)
      {
        if (((zzcmc)paramObject).zzjjk != null) {
          return false;
        }
      }
      else if (!this.zzjjk.equals(((zzcmc)paramObject).zzjjk)) {
        return false;
      }
      if (this.zzjjl == null)
      {
        if (((zzcmc)paramObject).zzjjl != null) {
          return false;
        }
      }
      else if (!this.zzjjl.equals(((zzcmc)paramObject).zzjjl)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzcmc)paramObject).zzpnc == null) || (((zzcmc)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzcmc)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int i2 = 0;
    int i3 = getClass().getName().hashCode();
    int i;
    int j;
    label33:
    int k;
    label42:
    int m;
    label52:
    int n;
    if (this.name == null)
    {
      i = 0;
      if (this.zzgcc != null) {
        break label138;
      }
      j = 0;
      if (this.zzjll != null) {
        break label149;
      }
      k = 0;
      if (this.zzjjk != null) {
        break label160;
      }
      m = 0;
      if (this.zzjjl != null) {
        break label172;
      }
      n = 0;
      label62:
      i1 = i2;
      if (this.zzpnc != null) {
        if (!this.zzpnc.isEmpty()) {
          break label184;
        }
      }
    }
    label138:
    label149:
    label160:
    label172:
    label184:
    for (int i1 = i2;; i1 = this.zzpnc.hashCode())
    {
      return (n + (m + (k + (j + (i + (i3 + 527) * 31) * 31) * 31) * 31) * 31) * 31 + i1;
      i = this.name.hashCode();
      break;
      j = this.zzgcc.hashCode();
      break label33;
      k = this.zzjll.hashCode();
      break label42;
      m = this.zzjjk.hashCode();
      break label52;
      n = this.zzjjl.hashCode();
      break label62;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    if (this.name != null) {
      paramzzfjk.zzn(1, this.name);
    }
    if (this.zzgcc != null) {
      paramzzfjk.zzn(2, this.zzgcc);
    }
    if (this.zzjll != null) {
      paramzzfjk.zzf(3, this.zzjll.longValue());
    }
    if (this.zzjjk != null) {
      paramzzfjk.zzc(4, this.zzjjk.floatValue());
    }
    if (this.zzjjl != null) {
      paramzzfjk.zza(5, this.zzjjl.doubleValue());
    }
    super.zza(paramzzfjk);
  }
  
  protected final int zzq()
  {
    int j = super.zzq();
    int i = j;
    if (this.name != null) {
      i = j + zzfjk.zzo(1, this.name);
    }
    j = i;
    if (this.zzgcc != null) {
      j = i + zzfjk.zzo(2, this.zzgcc);
    }
    i = j;
    if (this.zzjll != null) {
      i = j + zzfjk.zzc(3, this.zzjll.longValue());
    }
    j = i;
    if (this.zzjjk != null)
    {
      this.zzjjk.floatValue();
      j = i + (zzfjk.zzlg(4) + 4);
    }
    i = j;
    if (this.zzjjl != null)
    {
      this.zzjjl.doubleValue();
      i = j + (zzfjk.zzlg(5) + 8);
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcmc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */