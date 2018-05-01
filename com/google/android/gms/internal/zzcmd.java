package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcmd
  extends zzfjm<zzcmd>
{
  public zzcme[] zzjlm = zzcme.zzbbj();
  
  public zzcmd()
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
      if (!(paramObject instanceof zzcmd)) {
        return false;
      }
      paramObject = (zzcmd)paramObject;
      if (!zzfjq.equals(this.zzjlm, ((zzcmd)paramObject).zzjlm)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzcmd)paramObject).zzpnc == null) || (((zzcmd)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzcmd)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int j = getClass().getName().hashCode();
    int k = zzfjq.hashCode(this.zzjlm);
    if ((this.zzpnc == null) || (this.zzpnc.isEmpty())) {}
    for (int i = 0;; i = this.zzpnc.hashCode()) {
      return i + ((j + 527) * 31 + k) * 31;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    if ((this.zzjlm != null) && (this.zzjlm.length > 0))
    {
      int i = 0;
      while (i < this.zzjlm.length)
      {
        zzcme localzzcme = this.zzjlm[i];
        if (localzzcme != null) {
          paramzzfjk.zza(1, localzzcme);
        }
        i += 1;
      }
    }
    super.zza(paramzzfjk);
  }
  
  protected final int zzq()
  {
    int i = super.zzq();
    int k = i;
    if (this.zzjlm != null)
    {
      k = i;
      if (this.zzjlm.length > 0)
      {
        int j = 0;
        for (;;)
        {
          k = i;
          if (j >= this.zzjlm.length) {
            break;
          }
          zzcme localzzcme = this.zzjlm[j];
          k = i;
          if (localzzcme != null) {
            k = i + zzfjk.zzb(1, localzzcme);
          }
          j += 1;
          i = k;
        }
      }
    }
    return k;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcmd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */