package com.google.android.gms.internal;

import java.io.IOException;

public final class zzclz
  extends zzfjm<zzclz>
{
  private static volatile zzclz[] zzjlb;
  public String key = null;
  public String value = null;
  
  public zzclz()
  {
    this.zzpnc = null;
    this.zzpfd = -1;
  }
  
  public static zzclz[] zzbbf()
  {
    if (zzjlb == null) {}
    synchronized (zzfjq.zzpnk)
    {
      if (zzjlb == null) {
        zzjlb = new zzclz[0];
      }
      return zzjlb;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzclz)) {
        return false;
      }
      paramObject = (zzclz)paramObject;
      if (this.key == null)
      {
        if (((zzclz)paramObject).key != null) {
          return false;
        }
      }
      else if (!this.key.equals(((zzclz)paramObject).key)) {
        return false;
      }
      if (this.value == null)
      {
        if (((zzclz)paramObject).value != null) {
          return false;
        }
      }
      else if (!this.value.equals(((zzclz)paramObject).value)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzclz)paramObject).zzpnc == null) || (((zzclz)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzclz)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int m = 0;
    int n = getClass().getName().hashCode();
    int i;
    int j;
    if (this.key == null)
    {
      i = 0;
      if (this.value != null) {
        break label89;
      }
      j = 0;
      label33:
      k = m;
      if (this.zzpnc != null) {
        if (!this.zzpnc.isEmpty()) {
          break label100;
        }
      }
    }
    label89:
    label100:
    for (int k = m;; k = this.zzpnc.hashCode())
    {
      return (j + (i + (n + 527) * 31) * 31) * 31 + k;
      i = this.key.hashCode();
      break;
      j = this.value.hashCode();
      break label33;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    if (this.key != null) {
      paramzzfjk.zzn(1, this.key);
    }
    if (this.value != null) {
      paramzzfjk.zzn(2, this.value);
    }
    super.zza(paramzzfjk);
  }
  
  protected final int zzq()
  {
    int j = super.zzq();
    int i = j;
    if (this.key != null) {
      i = j + zzfjk.zzo(1, this.key);
    }
    j = i;
    if (this.value != null) {
      j = i + zzfjk.zzo(2, this.value);
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzclz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */