package com.google.android.gms.common.api.internal;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

public class LifecycleActivity
{
  private final Object zzkz;
  
  public final boolean zzbv()
  {
    return this.zzkz instanceof FragmentActivity;
  }
  
  public final boolean zzbw()
  {
    return this.zzkz instanceof Activity;
  }
  
  public final Activity zzbx()
  {
    return (Activity)this.zzkz;
  }
  
  public final FragmentActivity zzby()
  {
    return (FragmentActivity)this.zzkz;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/LifecycleActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */