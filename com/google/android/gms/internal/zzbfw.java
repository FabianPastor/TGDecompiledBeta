package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.internal.zzz;

public final class zzbfw
  extends zzz<zzbfz>
{
  public zzbfw(Context paramContext, Looper paramLooper, zzq paramzzq, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, 39, paramzzq, paramConnectionCallbacks, paramOnConnectionFailedListener);
  }
  
  public final String zzdb()
  {
    return "com.google.android.gms.common.service.START";
  }
  
  protected final String zzdc()
  {
    return "com.google.android.gms.common.internal.service.ICommonService";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbfw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */