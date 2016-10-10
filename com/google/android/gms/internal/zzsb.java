package com.google.android.gms.internal;

import android.content.Context;
import android.os.IBinder;
import android.os.Looper;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.common.internal.zzl;

public class zzsb
  extends zzl<zzsd>
{
  public zzsb(Context paramContext, Looper paramLooper, zzh paramzzh, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, 39, paramzzh, paramConnectionCallbacks, paramOnConnectionFailedListener);
  }
  
  protected zzsd zzea(IBinder paramIBinder)
  {
    return zzsd.zza.zzec(paramIBinder);
  }
  
  public String zzix()
  {
    return "com.google.android.gms.common.service.START";
  }
  
  protected String zziy()
  {
    return "com.google.android.gms.common.internal.service.ICommonService";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzsb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */