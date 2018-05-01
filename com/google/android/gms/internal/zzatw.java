package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzf.zzb;
import com.google.android.gms.common.internal.zzf.zzc;

public class zzatw
  extends zzf<zzatt>
{
  public zzatw(Context paramContext, Looper paramLooper, zzf.zzb paramzzb, zzf.zzc paramzzc)
  {
    super(paramContext, paramLooper, 93, paramzzb, paramzzc, null);
  }
  
  @NonNull
  protected String zzeA()
  {
    return "com.google.android.gms.measurement.internal.IMeasurementService";
  }
  
  public zzatt zzet(IBinder paramIBinder)
  {
    return zzatt.zza.zzes(paramIBinder);
  }
  
  @NonNull
  protected String zzez()
  {
    return "com.google.android.gms.measurement.START";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */