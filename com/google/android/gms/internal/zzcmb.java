package com.google.android.gms.internal;

import java.io.IOException;

public final class zzcmb
  extends zzfjm<zzcmb>
{
  private static volatile zzcmb[] zzjlg;
  public Integer count = null;
  public String name = null;
  public zzcmc[] zzjlh = zzcmc.zzbbi();
  public Long zzjli = null;
  public Long zzjlj = null;
  
  public zzcmb()
  {
    this.zzpnc = null;
    this.zzpfd = -1;
  }
  
  public static zzcmb[] zzbbh()
  {
    if (zzjlg == null) {}
    synchronized (zzfjq.zzpnk)
    {
      if (zzjlg == null) {
        zzjlg = new zzcmb[0];
      }
      return zzjlg;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzcmb)) {
        return false;
      }
      paramObject = (zzcmb)paramObject;
      if (!zzfjq.equals(this.zzjlh, ((zzcmb)paramObject).zzjlh)) {
        return false;
      }
      if (this.name == null)
      {
        if (((zzcmb)paramObject).name != null) {
          return false;
        }
      }
      else if (!this.name.equals(((zzcmb)paramObject).name)) {
        return false;
      }
      if (this.zzjli == null)
      {
        if (((zzcmb)paramObject).zzjli != null) {
          return false;
        }
      }
      else if (!this.zzjli.equals(((zzcmb)paramObject).zzjli)) {
        return false;
      }
      if (this.zzjlj == null)
      {
        if (((zzcmb)paramObject).zzjlj != null) {
          return false;
        }
      }
      else if (!this.zzjlj.equals(((zzcmb)paramObject).zzjlj)) {
        return false;
      }
      if (this.count == null)
      {
        if (((zzcmb)paramObject).count != null) {
          return false;
        }
      }
      else if (!this.count.equals(((zzcmb)paramObject).count)) {
        return false;
      }
      if ((this.zzpnc != null) && (!this.zzpnc.isEmpty())) {
        break;
      }
    } while ((((zzcmb)paramObject).zzpnc == null) || (((zzcmb)paramObject).zzpnc.isEmpty()));
    return false;
    return this.zzpnc.equals(((zzcmb)paramObject).zzpnc);
  }
  
  public final int hashCode()
  {
    int i1 = 0;
    int i2 = getClass().getName().hashCode();
    int i3 = zzfjq.hashCode(this.zzjlh);
    int i;
    int j;
    label42:
    int k;
    label51:
    int m;
    if (this.name == null)
    {
      i = 0;
      if (this.zzjli != null) {
        break label137;
      }
      j = 0;
      if (this.zzjlj != null) {
        break label148;
      }
      k = 0;
      if (this.count != null) {
        break label159;
      }
      m = 0;
      label61:
      n = i1;
      if (this.zzpnc != null) {
        if (!this.zzpnc.isEmpty()) {
          break label171;
        }
      }
    }
    label137:
    label148:
    label159:
    label171:
    for (int n = i1;; n = this.zzpnc.hashCode())
    {
      return (m + (k + (j + (i + ((i2 + 527) * 31 + i3) * 31) * 31) * 31) * 31) * 31 + n;
      i = this.name.hashCode();
      break;
      j = this.zzjli.hashCode();
      break label42;
      k = this.zzjlj.hashCode();
      break label51;
      m = this.count.hashCode();
      break label61;
    }
  }
  
  public final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    if ((this.zzjlh != null) && (this.zzjlh.length > 0))
    {
      int i = 0;
      while (i < this.zzjlh.length)
      {
        zzcmc localzzcmc = this.zzjlh[i];
        if (localzzcmc != null) {
          paramzzfjk.zza(1, localzzcmc);
        }
        i += 1;
      }
    }
    if (this.name != null) {
      paramzzfjk.zzn(2, this.name);
    }
    if (this.zzjli != null) {
      paramzzfjk.zzf(3, this.zzjli.longValue());
    }
    if (this.zzjlj != null) {
      paramzzfjk.zzf(4, this.zzjlj.longValue());
    }
    if (this.count != null) {
      paramzzfjk.zzaa(5, this.count.intValue());
    }
    super.zza(paramzzfjk);
  }
  
  protected final int zzq()
  {
    int i = super.zzq();
    int j = i;
    if (this.zzjlh != null)
    {
      j = i;
      if (this.zzjlh.length > 0)
      {
        int k = 0;
        for (;;)
        {
          j = i;
          if (k >= this.zzjlh.length) {
            break;
          }
          zzcmc localzzcmc = this.zzjlh[k];
          j = i;
          if (localzzcmc != null) {
            j = i + zzfjk.zzb(1, localzzcmc);
          }
          k += 1;
          i = j;
        }
      }
    }
    i = j;
    if (this.name != null) {
      i = j + zzfjk.zzo(2, this.name);
    }
    j = i;
    if (this.zzjli != null) {
      j = i + zzfjk.zzc(3, this.zzjli.longValue());
    }
    i = j;
    if (this.zzjlj != null) {
      i = j + zzfjk.zzc(4, this.zzjlj.longValue());
    }
    j = i;
    if (this.count != null) {
      j = i + zzfjk.zzad(5, this.count.intValue());
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcmb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */