package com.google.android.gms.internal;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.internal.zzac;

public class zzabd
{
  private final Object zzaCQ;
  
  public zzabd(Activity paramActivity)
  {
    zzac.zzb(paramActivity, "Activity must not be null");
    this.zzaCQ = paramActivity;
  }
  
  public boolean zzwS()
  {
    return this.zzaCQ instanceof FragmentActivity;
  }
  
  public Activity zzwT()
  {
    return (Activity)this.zzaCQ;
  }
  
  public FragmentActivity zzwU()
  {
    return (FragmentActivity)this.zzaCQ;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzabd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */