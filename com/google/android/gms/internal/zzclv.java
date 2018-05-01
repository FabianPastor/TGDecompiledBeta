package com.google.android.gms.internal;

import java.io.IOException;

public final class zzclv
  extends zzfjm<zzclv>
{
  private static volatile zzclv[] zzjkl;
  public Integer zzjjw = null;
  public String zzjkm = null;
  public zzclt zzjkn = null;
  
  public zzclv()
  {
    this.zzpnc = null;
    this.zzpfd = -1;
  }
  
  public static zzclv[] zzbbd()
  {
    if (zzjkl == null) {}
    synchronized (zzfjq.zzpnk)
    {
      if (zzjkl == null) {
        zzjkl = new zzclv[0];
      }
      return zzjkl;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzclv)) {
        return false;
      }
      paramObject = (zzclv)paramObject;
      if (this.zzjjw == null)
      {
        if (((zzclv)paramObject).zzjjw != null) {
          return false;
        }
      }
      else if (!this.zzjjw.equals(((zzclv)paramObject).zzjjw)) {
        return false;
      }
      if (this.zzjkm == null)
      {
        if (((zzclv)paramObject).zzjkm != null) {
          return false;
        }
      }
      else if (!this.zzjkm.equals(((zzclv)paramObject).zzjkm)) {
        return false;
      }
      if (this.zzjkn == null)
      {
        if (((zzclv)paramObject).zzjkn != null) {
          return false;
        }
      }
      else if (!this.zzjkn.equals(((zzclv)paramObject).zzjkn)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzclv)paramObject).zzpnc == null) || (((zzclv)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzclv)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int n = 0;
    int i1 = getClass().getName().hashCode();
    int i;
    int j;
    label33:
    zzclt localzzclt;
    int k;
    if (this.zzjjw == null)
    {
      i = 0;
      if (this.zzjkm != null) {
        break label110;
      }
      j = 0;
      localzzclt = this.zzjkn;
      if (localzzclt != null) {
        break label121;
      }
      k = 0;
      label46:
      m = n;
      if (this.zzpnc != null) {
        if (!this.zzpnc.isEmpty()) {
          break label130;
        }
      }
    }
    label110:
    label121:
    label130:
    for (int m = n;; m = this.zzpnc.hashCode())
    {
      return (k + (j + (i + (i1 + 527) * 31) * 31) * 31) * 31 + m;
      i = this.zzjjw.hashCode();
      break;
      j = this.zzjkm.hashCode();
      break label33;
      k = localzzclt.hashCode();
      break label46;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    if (this.zzjjw != null) {
      paramzzfjk.zzaa(1, this.zzjjw.intValue());
    }
    if (this.zzjkm != null) {
      paramzzfjk.zzn(2, this.zzjkm);
    }
    if (this.zzjkn != null) {
      paramzzfjk.zza(3, this.zzjkn);
    }
    super.zza(paramzzfjk);
  }
  
  protected final int zzq()
  {
    int j = super.zzq();
    int i = j;
    if (this.zzjjw != null) {
      i = j + zzfjk.zzad(1, this.zzjjw.intValue());
    }
    j = i;
    if (this.zzjkm != null) {
      j = i + zzfjk.zzo(2, this.zzjkm);
    }
    i = j;
    if (this.zzjkn != null) {
      i = j + zzfjk.zzb(3, this.zzjkn);
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzclv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */