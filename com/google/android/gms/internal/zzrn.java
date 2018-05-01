package com.google.android.gms.internal;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zzs;

public class zzrn
{
  private final Object Be;
  
  public zzrn(Activity paramActivity)
  {
    zzaa.zzb(paramActivity, "Activity must not be null");
    if ((zzs.zzayn()) || ((paramActivity instanceof FragmentActivity))) {}
    for (boolean bool = true;; bool = false)
    {
      zzaa.zzb(bool, "This Activity is not supported before platform version 11 (3.0 Honeycomb). Please use FragmentActivity instead.");
      this.Be = paramActivity;
      return;
    }
  }
  
  public boolean zzatv()
  {
    return this.Be instanceof FragmentActivity;
  }
  
  public Activity zzatw()
  {
    return (Activity)this.Be;
  }
  
  public FragmentActivity zzatx()
  {
    return (FragmentActivity)this.Be;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */