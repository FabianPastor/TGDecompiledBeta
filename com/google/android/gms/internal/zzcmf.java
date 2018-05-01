package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcmf
  extends zzfjm<zzcmf>
{
  public long[] zzjmp = zzfjv.zzpnq;
  public long[] zzjmq = zzfjv.zzpnq;
  
  public zzcmf()
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
      if (!(paramObject instanceof zzcmf)) {
        return false;
      }
      paramObject = (zzcmf)paramObject;
      if (!zzfjq.equals(this.zzjmp, ((zzcmf)paramObject).zzjmp)) {
        return false;
      }
      if (!zzfjq.equals(this.zzjmq, ((zzcmf)paramObject).zzjmq)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzcmf)paramObject).zzpnc == null) || (((zzcmf)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzcmf)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int j = getClass().getName().hashCode();
    int k = zzfjq.hashCode(this.zzjmp);
    int m = zzfjq.hashCode(this.zzjmq);
    if ((this.zzpnc == null) || (this.zzpnc.isEmpty())) {}
    for (int i = 0;; i = this.zzpnc.hashCode()) {
      return i + (((j + 527) * 31 + k) * 31 + m) * 31;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    int j = 0;
    int i;
    if ((this.zzjmp != null) && (this.zzjmp.length > 0))
    {
      i = 0;
      while (i < this.zzjmp.length)
      {
        paramzzfjk.zza(1, this.zzjmp[i]);
        i += 1;
      }
    }
    if ((this.zzjmq != null) && (this.zzjmq.length > 0))
    {
      i = j;
      while (i < this.zzjmq.length)
      {
        paramzzfjk.zza(2, this.zzjmq[i]);
        i += 1;
      }
    }
    super.zza(paramzzfjk);
  }
  
  protected final int zzq()
  {
    int m = 0;
    int k = super.zzq();
    int j;
    if ((this.zzjmp != null) && (this.zzjmp.length > 0))
    {
      i = 0;
      j = 0;
      while (i < this.zzjmp.length)
      {
        j += zzfjk.zzdi(this.zzjmp[i]);
        i += 1;
      }
    }
    for (int i = k + j + this.zzjmp.length * 1;; i = k)
    {
      j = i;
      if (this.zzjmq != null)
      {
        j = i;
        if (this.zzjmq.length > 0)
        {
          k = 0;
          j = m;
          while (j < this.zzjmq.length)
          {
            k += zzfjk.zzdi(this.zzjmq[j]);
            j += 1;
          }
          j = i + k + this.zzjmq.length * 1;
        }
      }
      return j;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcmf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */