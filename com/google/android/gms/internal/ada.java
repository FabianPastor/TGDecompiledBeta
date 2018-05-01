package com.google.android.gms.internal;

import java.io.IOException;

public abstract class ada<M extends ada<M>>
  extends adg
{
  protected adc zzcrZ;
  
  public M zzLL()
    throws CloneNotSupportedException
  {
    ada localada = (ada)super.zzLM();
    ade.zza(this, localada);
    return localada;
  }
  
  public final <T> T zza(adb<M, T> paramadb)
  {
    if (this.zzcrZ == null) {}
    add localadd;
    do
    {
      return null;
      localadd = this.zzcrZ.zzcx(paramadb.tag >>> 3);
    } while (localadd == null);
    return (T)localadd.zzb(paramadb);
  }
  
  public void zza(acy paramacy)
    throws IOException
  {
    if (this.zzcrZ == null) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < this.zzcrZ.size())
      {
        this.zzcrZ.zzcy(i).zza(paramacy);
        i += 1;
      }
    }
  }
  
  protected final boolean zza(acx paramacx, int paramInt)
    throws IOException
  {
    int i = paramacx.getPosition();
    if (!paramacx.zzcm(paramInt)) {
      return false;
    }
    int j = paramInt >>> 3;
    adi localadi = new adi(paramInt, paramacx.zzp(i, paramacx.getPosition() - i));
    paramacx = null;
    if (this.zzcrZ == null) {
      this.zzcrZ = new adc();
    }
    for (;;)
    {
      Object localObject = paramacx;
      if (paramacx == null)
      {
        localObject = new add();
        this.zzcrZ.zza(j, (add)localObject);
      }
      ((add)localObject).zza(localadi);
      return true;
      paramacx = this.zzcrZ.zzcx(j);
    }
  }
  
  protected int zzn()
  {
    int j = 0;
    if (this.zzcrZ != null)
    {
      int i = 0;
      for (;;)
      {
        k = i;
        if (j >= this.zzcrZ.size()) {
          break;
        }
        i += this.zzcrZ.zzcy(j).zzn();
        j += 1;
      }
    }
    int k = 0;
    return k;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/ada.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */