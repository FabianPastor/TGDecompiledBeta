package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzaa;

public class zzqr
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
  public final Api<?> vS;
  private final int yU;
  private zzqs yV;
  
  public zzqr(Api<?> paramApi, int paramInt)
  {
    this.vS = paramApi;
    this.yU = paramInt;
  }
  
  private void zzary()
  {
    zzaa.zzb(this.yV, "Callbacks must be attached to a ClientConnectionHelper instance before connecting the client.");
  }
  
  public void onConnected(@Nullable Bundle paramBundle)
  {
    zzary();
    this.yV.onConnected(paramBundle);
  }
  
  public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
  {
    zzary();
    this.yV.zza(paramConnectionResult, this.vS, this.yU);
  }
  
  public void onConnectionSuspended(int paramInt)
  {
    zzary();
    this.yV.onConnectionSuspended(paramInt);
  }
  
  public void zza(zzqs paramzzqs)
  {
    this.yV = paramzzqs;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */