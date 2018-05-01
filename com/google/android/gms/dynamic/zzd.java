package com.google.android.gms.dynamic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

final class zzd
  implements DeferredLifecycleHelper.zza
{
  zzd(DeferredLifecycleHelper paramDeferredLifecycleHelper, FrameLayout paramFrameLayout, LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {}
  
  public final int getState()
  {
    return 2;
  }
  
  public final void zza(LifecycleDelegate paramLifecycleDelegate)
  {
    this.zzabj.removeAllViews();
    this.zzabj.addView(DeferredLifecycleHelper.zzb(this.zzabg).onCreateView(this.zzabk, this.val$container, this.zzabi));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */