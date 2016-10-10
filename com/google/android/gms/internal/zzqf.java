package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzac;

public class zzqf
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
  public final Api<?> tv;
  private final int wT;
  private zzqg wU;
  
  public zzqf(Api<?> paramApi, int paramInt)
  {
    this.tv = paramApi;
    this.wT = paramInt;
  }
  
  private void zzaqx()
  {
    zzac.zzb(this.wU, "Callbacks must be attached to a ClientConnectionHelper instance before connecting the client.");
  }
  
  public void onConnected(@Nullable Bundle paramBundle)
  {
    zzaqx();
    this.wU.onConnected(paramBundle);
  }
  
  public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
  {
    zzaqx();
    this.wU.zza(paramConnectionResult, this.tv, this.wT);
  }
  
  public void onConnectionSuspended(int paramInt)
  {
    zzaqx();
    this.wU.onConnectionSuspended(paramInt);
  }
  
  public void zza(zzqg paramzzqg)
  {
    this.wU = paramzzqg;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */