package com.google.android.gms.internal;

import android.support.annotation.BinderThread;
import java.lang.ref.WeakReference;

final class zzbck
  extends zzctp
{
  private final WeakReference<zzbcd> zzaDq;
  
  zzbck(zzbcd paramzzbcd)
  {
    this.zzaDq = new WeakReference(paramzzbcd);
  }
  
  @BinderThread
  public final void zzb(zzctx paramzzctx)
  {
    zzbcd localzzbcd = (zzbcd)this.zzaDq.get();
    if (localzzbcd == null) {
      return;
    }
    zzbcd.zzd(localzzbcd).zza(new zzbcl(this, localzzbcd, localzzbcd, paramzzctx));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbck.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */