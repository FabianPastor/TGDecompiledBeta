package com.google.android.gms.internal;

import java.io.IOException;

public final class zzclr
  extends zzfjm<zzclr>
{
  private static volatile zzclr[] zzjjr;
  public Integer zzjjs = null;
  public zzclv[] zzjjt = zzclv.zzbbd();
  public zzcls[] zzjju = zzcls.zzbbb();
  
  public zzclr()
  {
    this.zzpnc = null;
    this.zzpfd = -1;
  }
  
  public static zzclr[] zzbba()
  {
    if (zzjjr == null) {}
    synchronized (zzfjq.zzpnk)
    {
      if (zzjjr == null) {
        zzjjr = new zzclr[0];
      }
      return zzjjr;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzclr)) {
        return false;
      }
      paramObject = (zzclr)paramObject;
      if (this.zzjjs == null)
      {
        if (((zzclr)paramObject).zzjjs != null) {
          return false;
        }
      }
      else if (!this.zzjjs.equals(((zzclr)paramObject).zzjjs)) {
        return false;
      }
      if (!zzfjq.equals(this.zzjjt, ((zzclr)paramObject).zzjjt)) {
        return false;
      }
      if (!zzfjq.equals(this.zzjju, ((zzclr)paramObject).zzjju)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzclr)paramObject).zzpnc == null) || (((zzclr)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzclr)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int k = 0;
    int m = getClass().getName().hashCode();
    int i;
    int n;
    int i1;
    if (this.zzjjs == null)
    {
      i = 0;
      n = zzfjq.hashCode(this.zzjjt);
      i1 = zzfjq.hashCode(this.zzjju);
      j = k;
      if (this.zzpnc != null) {
        if (!this.zzpnc.isEmpty()) {
          break label102;
        }
      }
    }
    label102:
    for (int j = k;; j = this.zzpnc.hashCode())
    {
      return (((i + (m + 527) * 31) * 31 + n) * 31 + i1) * 31 + j;
      i = this.zzjjs.hashCode();
      break;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    int j = 0;
    if (this.zzjjs != null) {
      paramzzfjk.zzaa(1, this.zzjjs.intValue());
    }
    int i;
    Object localObject;
    if ((this.zzjjt != null) && (this.zzjjt.length > 0))
    {
      i = 0;
      while (i < this.zzjjt.length)
      {
        localObject = this.zzjjt[i];
        if (localObject != null) {
          paramzzfjk.zza(2, (zzfjs)localObject);
        }
        i += 1;
      }
    }
    if ((this.zzjju != null) && (this.zzjju.length > 0))
    {
      i = j;
      while (i < this.zzjju.length)
      {
        localObject = this.zzjju[i];
        if (localObject != null) {
          paramzzfjk.zza(3, (zzfjs)localObject);
        }
        i += 1;
      }
    }
    super.zza(paramzzfjk);
  }
  
  protected final int zzq()
  {
    int m = 0;
    int i = super.zzq();
    int j = i;
    if (this.zzjjs != null) {
      j = i + zzfjk.zzad(1, this.zzjjs.intValue());
    }
    i = j;
    Object localObject;
    if (this.zzjjt != null)
    {
      i = j;
      if (this.zzjjt.length > 0)
      {
        i = j;
        j = 0;
        while (j < this.zzjjt.length)
        {
          localObject = this.zzjjt[j];
          k = i;
          if (localObject != null) {
            k = i + zzfjk.zzb(2, (zzfjs)localObject);
          }
          j += 1;
          i = k;
        }
      }
    }
    int k = i;
    if (this.zzjju != null)
    {
      k = i;
      if (this.zzjju.length > 0)
      {
        j = m;
        for (;;)
        {
          k = i;
          if (j >= this.zzjju.length) {
            break;
          }
          localObject = this.zzjju[j];
          k = i;
          if (localObject != null) {
            k = i + zzfjk.zzb(3, (zzfjs)localObject);
          }
          j += 1;
          i = k;
        }
      }
    }
    return k;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzclr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */