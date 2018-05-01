package com.google.android.gms.common.api.internal;

import java.lang.ref.WeakReference;

final class zzbb
  extends GooglePlayServicesUpdatedReceiver.Callback
{
  private WeakReference<zzav> zziy;
  
  zzbb(zzav paramzzav)
  {
    this.zziy = new WeakReference(paramzzav);
  }
  
  public final void zzv()
  {
    zzav localzzav = (zzav)this.zziy.get();
    if (localzzav == null) {}
    for (;;)
    {
      return;
      zzav.zza(localzzav);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzbb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */