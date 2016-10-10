package com.google.android.gms.internal;

import com.google.android.gms.common.data.DataHolder;

public abstract class zzqj<L>
  implements zzrd.zzc<L>
{
  private final DataHolder xi;
  
  protected zzqj(DataHolder paramDataHolder)
  {
    this.xi = paramDataHolder;
  }
  
  protected abstract void zza(L paramL, DataHolder paramDataHolder);
  
  public void zzarg()
  {
    if (this.xi != null) {
      this.xi.close();
    }
  }
  
  public final void zzt(L paramL)
  {
    zza(paramL, this.xi);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */