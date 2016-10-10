package com.google.android.gms.internal;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzs;

public class zzqz
{
  private final Object yX;
  
  public zzqz(Activity paramActivity)
  {
    zzac.zzb(paramActivity, "Activity must not be null");
    if ((zzs.zzaxk()) || ((paramActivity instanceof FragmentActivity))) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zzb(bool, "This Activity is not supported before platform version 11 (3.0 Honeycomb). Please use FragmentActivity instead.");
      this.yX = paramActivity;
      return;
    }
  }
  
  public boolean zzasn()
  {
    return this.yX instanceof FragmentActivity;
  }
  
  public Activity zzaso()
  {
    return (Activity)this.yX;
  }
  
  public FragmentActivity zzasp()
  {
    return (FragmentActivity)this.yX;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */