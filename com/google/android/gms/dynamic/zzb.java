package com.google.android.gms.dynamic;

import android.app.Activity;
import android.os.Bundle;

final class zzb
  implements DeferredLifecycleHelper.zza
{
  zzb(DeferredLifecycleHelper paramDeferredLifecycleHelper, Activity paramActivity, Bundle paramBundle1, Bundle paramBundle2) {}
  
  public final int getState()
  {
    return 0;
  }
  
  public final void zza(LifecycleDelegate paramLifecycleDelegate)
  {
    DeferredLifecycleHelper.zzb(this.zzabg).onInflate(this.val$activity, this.zzabh, this.zzabi);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */