package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcma
  extends zzfjm<zzcma>
{
  private static volatile zzcma[] zzjlc;
  public Integer zzjjs = null;
  public zzcmf zzjld = null;
  public zzcmf zzjle = null;
  public Boolean zzjlf = null;
  
  public zzcma()
  {
    this.zzpnc = null;
    this.zzpfd = -1;
  }
  
  public static zzcma[] zzbbg()
  {
    if (zzjlc == null) {}
    synchronized (zzfjq.zzpnk)
    {
      if (zzjlc == null) {
        zzjlc = new zzcma[0];
      }
      return zzjlc;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzcma)) {
        return false;
      }
      paramObject = (zzcma)paramObject;
      if (this.zzjjs == null)
      {
        if (((zzcma)paramObject).zzjjs != null) {
          return false;
        }
      }
      else if (!this.zzjjs.equals(((zzcma)paramObject).zzjjs)) {
        return false;
      }
      if (this.zzjld == null)
      {
        if (((zzcma)paramObject).zzjld != null) {
          return false;
        }
      }
      else if (!this.zzjld.equals(((zzcma)paramObject).zzjld)) {
        return false;
      }
      if (this.zzjle == null)
      {
        if (((zzcma)paramObject).zzjle != null) {
          return false;
        }
      }
      else if (!this.zzjle.equals(((zzcma)paramObject).zzjle)) {
        return false;
      }
      if (this.zzjlf == null)
      {
        if (((zzcma)paramObject).zzjlf != null) {
          return false;
        }
      }
      else if (!this.zzjlf.equals(((zzcma)paramObject).zzjlf)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzcma)paramObject).zzpnc == null) || (((zzcma)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzcma)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int i1 = 0;
    int i2 = getClass().getName().hashCode();
    int i;
    zzcmf localzzcmf;
    int j;
    label37:
    int k;
    label50:
    int m;
    if (this.zzjjs == null)
    {
      i = 0;
      localzzcmf = this.zzjld;
      if (localzzcmf != null) {
        break label130;
      }
      j = 0;
      localzzcmf = this.zzjle;
      if (localzzcmf != null) {
        break label139;
      }
      k = 0;
      if (this.zzjlf != null) {
        break label148;
      }
      m = 0;
      label60:
      n = i1;
      if (this.zzpnc != null) {
        if (!this.zzpnc.isEmpty()) {
          break label160;
        }
      }
    }
    label130:
    label139:
    label148:
    label160:
    for (int n = i1;; n = this.zzpnc.hashCode())
    {
      return (m + (k + (j + (i + (i2 + 527) * 31) * 31) * 31) * 31) * 31 + n;
      i = this.zzjjs.hashCode();
      break;
      j = localzzcmf.hashCode();
      break label37;
      k = localzzcmf.hashCode();
      break label50;
      m = this.zzjlf.hashCode();
      break label60;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    if (this.zzjjs != null) {
      paramzzfjk.zzaa(1, this.zzjjs.intValue());
    }
    if (this.zzjld != null) {
      paramzzfjk.zza(2, this.zzjld);
    }
    if (this.zzjle != null) {
      paramzzfjk.zza(3, this.zzjle);
    }
    if (this.zzjlf != null) {
      paramzzfjk.zzl(4, this.zzjlf.booleanValue());
    }
    super.zza(paramzzfjk);
  }
  
  protected final int zzq()
  {
    int j = super.zzq();
    int i = j;
    if (this.zzjjs != null) {
      i = j + zzfjk.zzad(1, this.zzjjs.intValue());
    }
    j = i;
    if (this.zzjld != null) {
      j = i + zzfjk.zzb(2, this.zzjld);
    }
    i = j;
    if (this.zzjle != null) {
      i = j + zzfjk.zzb(3, this.zzjle);
    }
    j = i;
    if (this.zzjlf != null)
    {
      this.zzjlf.booleanValue();
      j = i + (zzfjk.zzlg(4) + 1);
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcma.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */