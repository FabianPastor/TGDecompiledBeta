package com.google.android.gms.common.internal;

import android.os.Bundle;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;

final class zzf
  implements BaseGmsClient.BaseConnectionCallbacks
{
  zzf(GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks) {}
  
  public final void onConnected(Bundle paramBundle)
  {
    this.zztd.onConnected(paramBundle);
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    this.zztd.onConnectionSuspended(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */