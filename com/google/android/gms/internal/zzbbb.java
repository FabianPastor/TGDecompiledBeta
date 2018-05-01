package com.google.android.gms.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzbo;

final class zzbbb
{
  private final int zzaBP;
  private final ConnectionResult zzaBQ;
  
  zzbbb(ConnectionResult paramConnectionResult, int paramInt)
  {
    zzbo.zzu(paramConnectionResult);
    this.zzaBQ = paramConnectionResult;
    this.zzaBP = paramInt;
  }
  
  final int zzpy()
  {
    return this.zzaBP;
  }
  
  final ConnectionResult zzpz()
  {
    return this.zzaBQ;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */