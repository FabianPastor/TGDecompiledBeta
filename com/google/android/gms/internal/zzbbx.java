package com.google.android.gms.internal;

import com.google.android.gms.common.data.DataHolder;

public abstract class zzbbx<L>
  implements zzbdz<L>
{
  private final DataHolder zzaCX;
  
  protected zzbbx(DataHolder paramDataHolder)
  {
    this.zzaCX = paramDataHolder;
  }
  
  protected abstract void zza(L paramL, DataHolder paramDataHolder);
  
  public final void zzpT()
  {
    if (this.zzaCX != null) {
      this.zzaCX.close();
    }
  }
  
  public final void zzq(L paramL)
  {
    zza(paramL, this.zzaCX);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */