package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.internal.BaseImplementation.ResultHolder;

class zzgm<T>
  extends zza
{
  private BaseImplementation.ResultHolder<T> zzet;
  
  public zzgm(BaseImplementation.ResultHolder<T> paramResultHolder)
  {
    this.zzet = paramResultHolder;
  }
  
  public final void zza(T paramT)
  {
    BaseImplementation.ResultHolder localResultHolder = this.zzet;
    if (localResultHolder != null)
    {
      localResultHolder.setResult(paramT);
      this.zzet = null;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzgm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */