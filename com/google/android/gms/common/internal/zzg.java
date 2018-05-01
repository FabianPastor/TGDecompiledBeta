package com.google.android.gms.common.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

final class zzg
  implements BaseGmsClient.BaseOnConnectionFailedListener
{
  zzg(GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener) {}
  
  public final void onConnectionFailed(ConnectionResult paramConnectionResult)
  {
    this.zzte.onConnectionFailed(paramConnectionResult);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */