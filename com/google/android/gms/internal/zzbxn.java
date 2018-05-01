package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbxn<M extends zzbxn<M>>
  extends zzbxt
{
  protected zzbxp zzcuI;
  
  private void zza(int paramInt, zzbxv paramzzbxv)
  {
    zzbxq localzzbxq1 = null;
    if (this.zzcuI == null) {
      this.zzcuI = new zzbxp();
    }
    for (;;)
    {
      zzbxq localzzbxq2 = localzzbxq1;
      if (localzzbxq1 == null)
      {
        localzzbxq2 = new zzbxq();
        this.zzcuI.zza(paramInt, localzzbxq2);
      }
      localzzbxq2.zza(paramzzbxv);
      return;
      localzzbxq1 = this.zzcuI.zzro(paramInt);
    }
  }
  
  public final <T> T zza(zzbxo<M, T> paramzzbxo)
  {
    if (this.zzcuI == null) {}
    zzbxq localzzbxq;
    do
    {
      return null;
      localzzbxq = this.zzcuI.zzro(zzbxw.zzrs(paramzzbxo.tag));
    } while (localzzbxq == null);
    return (T)localzzbxq.zzb(paramzzbxo);
  }
  
  public void zza(zzbxm paramzzbxm)
    throws IOException
  {
    if (this.zzcuI == null) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < this.zzcuI.size())
      {
        this.zzcuI.zzrp(i).zza(paramzzbxm);
        i += 1;
      }
    }
  }
  
  protected final boolean zza(zzbxl paramzzbxl, int paramInt)
    throws IOException
  {
    int i = paramzzbxl.getPosition();
    if (!paramzzbxl.zzqY(paramInt)) {
      return false;
    }
    zza(zzbxw.zzrs(paramInt), new zzbxv(paramInt, paramzzbxl.zzI(i, paramzzbxl.getPosition() - i)));
    return true;
  }
  
  public M zzaeH()
    throws CloneNotSupportedException
  {
    zzbxn localzzbxn = (zzbxn)super.zzaeI();
    zzbxr.zza(this, localzzbxn);
    return localzzbxn;
  }
  
  protected int zzu()
  {
    int j = 0;
    if (this.zzcuI != null)
    {
      int i = 0;
      for (;;)
      {
        k = i;
        if (j >= this.zzcuI.size()) {
          break;
        }
        i += this.zzcuI.zzrp(j).zzu();
        j += 1;
      }
    }
    int k = 0;
    return k;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbxn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */