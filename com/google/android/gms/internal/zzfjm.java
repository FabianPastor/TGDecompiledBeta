package com.google.android.gms.internal;

import java.io.IOException;

public abstract class zzfjm<M extends zzfjm<M>>
  extends zzfjs
{
  protected zzfjo zzpnc;
  
  public void zza(zzfjk paramzzfjk)
    throws IOException
  {
    if (this.zzpnc == null) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < this.zzpnc.size())
      {
        this.zzpnc.zzmk(i).zza(paramzzfjk);
        i += 1;
      }
    }
  }
  
  protected final boolean zza(zzfjj paramzzfjj, int paramInt)
    throws IOException
  {
    int i = paramzzfjj.getPosition();
    if (!paramzzfjj.zzkq(paramInt)) {
      return false;
    }
    int j = paramInt >>> 3;
    zzfju localzzfju = new zzfju(paramInt, paramzzfjj.zzal(i, paramzzfjj.getPosition() - i));
    paramzzfjj = null;
    if (this.zzpnc == null) {
      this.zzpnc = new zzfjo();
    }
    for (;;)
    {
      Object localObject = paramzzfjj;
      if (paramzzfjj == null)
      {
        localObject = new zzfjp();
        this.zzpnc.zza(j, (zzfjp)localObject);
      }
      ((zzfjp)localObject).zza(localzzfju);
      return true;
      paramzzfjj = this.zzpnc.zzmj(j);
    }
  }
  
  public M zzdaf()
    throws CloneNotSupportedException
  {
    zzfjm localzzfjm = (zzfjm)super.zzdag();
    zzfjq.zza(this, localzzfjm);
    return localzzfjm;
  }
  
  protected int zzq()
  {
    int j = 0;
    if (this.zzpnc != null)
    {
      int i = 0;
      for (;;)
      {
        k = i;
        if (j >= this.zzpnc.size()) {
          break;
        }
        i += this.zzpnc.zzmk(j).zzq();
        j += 1;
      }
    }
    int k = 0;
    return k;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzfjm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */