package com.google.android.gms.dynamic;

import android.os.Bundle;

final class zzc
  implements DeferredLifecycleHelper.zza
{
  zzc(DeferredLifecycleHelper paramDeferredLifecycleHelper, Bundle paramBundle) {}
  
  public final int getState()
  {
    return 1;
  }
  
  public final void zza(LifecycleDelegate paramLifecycleDelegate)
  {
    DeferredLifecycleHelper.zzb(this.zzabg).onCreate(this.zzabi);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */