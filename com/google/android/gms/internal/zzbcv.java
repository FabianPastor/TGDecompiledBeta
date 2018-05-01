package com.google.android.gms.internal;

import java.lang.ref.WeakReference;

final class zzbcv
  extends zzbdl
{
  private WeakReference<zzbcp> zzaDR;
  
  zzbcv(zzbcp paramzzbcp)
  {
    this.zzaDR = new WeakReference(paramzzbcp);
  }
  
  public final void zzpA()
  {
    zzbcp localzzbcp = (zzbcp)this.zzaDR.get();
    if (localzzbcp == null) {
      return;
    }
    zzbcp.zza(localzzbcp);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbcv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */