package com.google.android.gms.internal;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.internal.zzbo;

public final class zzbdr
{
  private final Object zzaEF;
  
  public zzbdr(Activity paramActivity)
  {
    zzbo.zzb(paramActivity, "Activity must not be null");
    this.zzaEF = paramActivity;
  }
  
  public final boolean zzqC()
  {
    return this.zzaEF instanceof FragmentActivity;
  }
  
  public final Activity zzqD()
  {
    return (Activity)this.zzaEF;
  }
  
  public final FragmentActivity zzqE()
  {
    return (FragmentActivity)this.zzaEF;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbdr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */