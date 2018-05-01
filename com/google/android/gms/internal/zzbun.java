package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzbun<M extends zzbun<M>>
  extends zzbut
{
  protected zzbup zzcrX;
  
  public final <T> T zza(zzbuo<M, T> paramzzbuo)
  {
    if (this.zzcrX == null) {}
    zzbuq localzzbuq;
    do
    {
      return null;
      localzzbuq = this.zzcrX.zzqx(zzbuw.zzqB(paramzzbuo.tag));
    } while (localzzbuq == null);
    return (T)localzzbuq.zzb(paramzzbuo);
  }
  
  public void zza(zzbum paramzzbum)
    throws IOException
  {
    if (this.zzcrX == null) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < this.zzcrX.size())
      {
        this.zzcrX.zzqy(i).zza(paramzzbum);
        i += 1;
      }
    }
  }
  
  protected final boolean zza(zzbul paramzzbul, int paramInt)
    throws IOException
  {
    int i = paramzzbul.getPosition();
    if (!paramzzbul.zzqh(paramInt)) {
      return false;
    }
    int j = zzbuw.zzqB(paramInt);
    zzbuv localzzbuv = new zzbuv(paramInt, paramzzbul.zzE(i, paramzzbul.getPosition() - i));
    paramzzbul = null;
    if (this.zzcrX == null) {
      this.zzcrX = new zzbup();
    }
    for (;;)
    {
      Object localObject = paramzzbul;
      if (paramzzbul == null)
      {
        localObject = new zzbuq();
        this.zzcrX.zza(j, (zzbuq)localObject);
      }
      ((zzbuq)localObject).zza(localzzbuv);
      return true;
      paramzzbul = this.zzcrX.zzqx(j);
    }
  }
  
  public M zzacN()
    throws CloneNotSupportedException
  {
    zzbun localzzbun = (zzbun)super.zzacO();
    zzbur.zza(this, localzzbun);
    return localzzbun;
  }
  
  protected int zzv()
  {
    int j = 0;
    if (this.zzcrX != null)
    {
      int i = 0;
      for (;;)
      {
        k = i;
        if (j >= this.zzcrX.size()) {
          break;
        }
        i += this.zzcrX.zzqy(j).zzv();
        j += 1;
      }
    }
    int k = 0;
    return k;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbun.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */