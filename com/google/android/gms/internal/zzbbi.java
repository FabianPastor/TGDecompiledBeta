package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzbo;

public final class zzbbi
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
  private final boolean zzaCj;
  private zzbbj zzaCk;
  public final Api<?> zzayW;
  
  public zzbbi(Api<?> paramApi, boolean paramBoolean)
  {
    this.zzayW = paramApi;
    this.zzaCj = paramBoolean;
  }
  
  private final void zzpD()
  {
    zzbo.zzb(this.zzaCk, "Callbacks must be attached to a ClientConnectionHelper instance before connecting the client.");
  }
  
  public final void onConnected(@Nullable Bundle paramBundle)
  {
    zzpD();
    this.zzaCk.onConnected(paramBundle);
  }
  
  public final void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
  {
    zzpD();
    this.zzaCk.zza(paramConnectionResult, this.zzayW, this.zzaCj);
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    zzpD();
    this.zzaCk.onConnectionSuspended(paramInt);
  }
  
  public final void zza(zzbbj paramzzbbj)
  {
    this.zzaCk = paramzzbbj;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */