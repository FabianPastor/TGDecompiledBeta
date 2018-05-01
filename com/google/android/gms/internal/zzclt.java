package com.google.android.gms.internal;

import java.io.IOException;

public final class zzclt
  extends zzfjm<zzclt>
{
  private static volatile zzclt[] zzjkb;
  public zzclw zzjkc = null;
  public zzclu zzjkd = null;
  public Boolean zzjke = null;
  public String zzjkf = null;
  
  public zzclt()
  {
    this.zzpnc = null;
    this.zzpfd = -1;
  }
  
  public static zzclt[] zzbbc()
  {
    if (zzjkb == null) {}
    synchronized (zzfjq.zzpnk)
    {
      if (zzjkb == null) {
        zzjkb = new zzclt[0];
      }
      return zzjkb;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzclt)) {
        return false;
      }
      paramObject = (zzclt)paramObject;
      if (this.zzjkc == null)
      {
        if (((zzclt)paramObject).zzjkc != null) {
          return false;
        }
      }
      else if (!this.zzjkc.equals(((zzclt)paramObject).zzjkc)) {
        return false;
      }
      if (this.zzjkd == null)
      {
        if (((zzclt)paramObject).zzjkd != null) {
          return false;
        }
      }
      else if (!this.zzjkd.equals(((zzclt)paramObject).zzjkd)) {
        return false;
      }
      if (this.zzjke == null)
      {
        if (((zzclt)paramObject).zzjke != null) {
          return false;
        }
      }
      else if (!this.zzjke.equals(((zzclt)paramObject).zzjke)) {
        return false;
      }
      if (this.zzjkf == null)
      {
        if (((zzclt)paramObject).zzjkf != null) {
          return false;
        }
      }
      else if (!this.zzjkf.equals(((zzclt)paramObject).zzjkf)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzclt)paramObject).zzpnc == null) || (((zzclt)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzclt)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int i1 = 0;
    int i2 = getClass().getName().hashCode();
    Object localObject = this.zzjkc;
    int i;
    int j;
    label41:
    int k;
    label50:
    int m;
    if (localObject == null)
    {
      i = 0;
      localObject = this.zzjkd;
      if (localObject != null) {
        break label128;
      }
      j = 0;
      if (this.zzjke != null) {
        break label137;
      }
      k = 0;
      if (this.zzjkf != null) {
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
    label128:
    label137:
    label148:
    label160:
    for (int n = i1;; n = this.zzpnc.hashCode())
    {
      return (m + (k + (j + (i + (i2 + 527) * 31) * 31) * 31) * 31) * 31 + n;
      i = ((zzclw)localObject).hashCode();
      break;
      j = ((zzclu)localObject).hashCode();
      break label41;
      k = this.zzjke.hashCode();
      break label50;
      m = this.zzjkf.hashCode();
      break label60;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    if (this.zzjkc != null) {
      paramzzfjk.zza(1, this.zzjkc);
    }
    if (this.zzjkd != null) {
      paramzzfjk.zza(2, this.zzjkd);
    }
    if (this.zzjke != null) {
      paramzzfjk.zzl(3, this.zzjke.booleanValue());
    }
    if (this.zzjkf != null) {
      paramzzfjk.zzn(4, this.zzjkf);
    }
    super.zza(paramzzfjk);
  }
  
  protected final int zzq()
  {
    int j = super.zzq();
    int i = j;
    if (this.zzjkc != null) {
      i = j + zzfjk.zzb(1, this.zzjkc);
    }
    j = i;
    if (this.zzjkd != null) {
      j = i + zzfjk.zzb(2, this.zzjkd);
    }
    i = j;
    if (this.zzjke != null)
    {
      this.zzjke.booleanValue();
      i = j + (zzfjk.zzlg(3) + 1);
    }
    j = i;
    if (this.zzjkf != null) {
      j = i + zzfjk.zzo(4, this.zzjkf);
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzclt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */