package com.google.android.gms.internal;

import java.io.IOException;

public final class zzclx
  extends zzfjm<zzclx>
{
  private static volatile zzclx[] zzjks;
  public String name = null;
  public Boolean zzjkt = null;
  public Boolean zzjku = null;
  public Integer zzjkv = null;
  
  public zzclx()
  {
    this.zzpnc = null;
    this.zzpfd = -1;
  }
  
  public static zzclx[] zzbbe()
  {
    if (zzjks == null) {}
    synchronized (zzfjq.zzpnk)
    {
      if (zzjks == null) {
        zzjks = new zzclx[0];
      }
      return zzjks;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzclx)) {
        return false;
      }
      paramObject = (zzclx)paramObject;
      if (this.name == null)
      {
        if (((zzclx)paramObject).name != null) {
          return false;
        }
      }
      else if (!this.name.equals(((zzclx)paramObject).name)) {
        return false;
      }
      if (this.zzjkt == null)
      {
        if (((zzclx)paramObject).zzjkt != null) {
          return false;
        }
      }
      else if (!this.zzjkt.equals(((zzclx)paramObject).zzjkt)) {
        return false;
      }
      if (this.zzjku == null)
      {
        if (((zzclx)paramObject).zzjku != null) {
          return false;
        }
      }
      else if (!this.zzjku.equals(((zzclx)paramObject).zzjku)) {
        return false;
      }
      if (this.zzjkv == null)
      {
        if (((zzclx)paramObject).zzjkv != null) {
          return false;
        }
      }
      else if (!this.zzjkv.equals(((zzclx)paramObject).zzjkv)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzclx)paramObject).zzpnc == null) || (((zzclx)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzclx)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int i1 = 0;
    int i2 = getClass().getName().hashCode();
    int i;
    int j;
    label33:
    int k;
    label42:
    int m;
    if (this.name == null)
    {
      i = 0;
      if (this.zzjkt != null) {
        break label122;
      }
      j = 0;
      if (this.zzjku != null) {
        break label133;
      }
      k = 0;
      if (this.zzjkv != null) {
        break label144;
      }
      m = 0;
      label52:
      n = i1;
      if (this.zzpnc != null) {
        if (!this.zzpnc.isEmpty()) {
          break label156;
        }
      }
    }
    label122:
    label133:
    label144:
    label156:
    for (int n = i1;; n = this.zzpnc.hashCode())
    {
      return (m + (k + (j + (i + (i2 + 527) * 31) * 31) * 31) * 31) * 31 + n;
      i = this.name.hashCode();
      break;
      j = this.zzjkt.hashCode();
      break label33;
      k = this.zzjku.hashCode();
      break label42;
      m = this.zzjkv.hashCode();
      break label52;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    if (this.name != null) {
      paramzzfjk.zzn(1, this.name);
    }
    if (this.zzjkt != null) {
      paramzzfjk.zzl(2, this.zzjkt.booleanValue());
    }
    if (this.zzjku != null) {
      paramzzfjk.zzl(3, this.zzjku.booleanValue());
    }
    if (this.zzjkv != null) {
      paramzzfjk.zzaa(4, this.zzjkv.intValue());
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
    if (this.zzjkt != null)
    {
      this.zzjkt.booleanValue();
      j = i + (zzfjk.zzlg(2) + 1);
    }
    i = j;
    if (this.zzjku != null)
    {
      this.zzjku.booleanValue();
      i = j + (zzfjk.zzlg(3) + 1);
    }
    j = i;
    if (this.zzjkv != null) {
      j = i + zzfjk.zzad(4, this.zzjkv.intValue());
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzclx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */