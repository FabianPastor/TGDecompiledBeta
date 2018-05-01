package com.google.android.gms.wearable.internal;

import com.google.android.gms.internal.zzbaz;

class zzfc<T>
  extends zza
{
  private zzbaz<T> zzajL;
  
  public zzfc(zzbaz<T> paramzzbaz)
  {
    this.zzajL = paramzzbaz;
  }
  
  public final void zzR(T paramT)
  {
    zzbaz localzzbaz = this.zzajL;
    if (localzzbaz != null)
    {
      localzzbaz.setResult(paramT);
      this.zzajL = null;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzfc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */