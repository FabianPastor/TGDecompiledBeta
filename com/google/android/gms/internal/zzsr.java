package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.Looper;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzj;

public class zzsr
  extends zzj<zzst>
{
  public zzsr(Context paramContext, Looper paramLooper, zzf paramzzf, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, 39, paramzzf, paramConnectionCallbacks, paramOnConnectionFailedListener);
  }
  
  protected zzst zzdz(IBinder paramIBinder)
  {
    return zzst.zza.zzeb(paramIBinder);
  }
  
  public String zzjx()
  {
    return "com.google.android.gms.common.service.START";
  }
  
  protected String zzjy()
  {
    return "com.google.android.gms.common.internal.service.ICommonService";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzsr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */