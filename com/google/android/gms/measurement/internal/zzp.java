package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zze;
import com.google.android.gms.common.internal.zze.zzb;
import com.google.android.gms.common.internal.zze.zzc;

public class zzp
  extends zze<zzm>
{
  public zzp(Context paramContext, Looper paramLooper, zze.zzb paramzzb, zze.zzc paramzzc)
  {
    super(paramContext, paramLooper, 93, paramzzb, paramzzc, null);
  }
  
  public zzm zzjq(IBinder paramIBinder)
  {
    return zzm.zza.zzjp(paramIBinder);
  }
  
  @NonNull
  protected String zzjx()
  {
    return "com.google.android.gms.measurement.START";
  }
  
  @NonNull
  protected String zzjy()
  {
    return "com.google.android.gms.measurement.internal.IMeasurementService";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */