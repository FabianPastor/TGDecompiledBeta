package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.Preconditions;

public final class zzp
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
  public final Api<?> mApi;
  private final boolean zzfo;
  private zzq zzfp;
  
  public zzp(Api<?> paramApi, boolean paramBoolean)
  {
    this.mApi = paramApi;
    this.zzfo = paramBoolean;
  }
  
  private final void zzy()
  {
    Preconditions.checkNotNull(this.zzfp, "Callbacks must be attached to a ClientConnectionHelper instance before connecting the client.");
  }
  
  public final void onConnected(Bundle paramBundle)
  {
    zzy();
    this.zzfp.onConnected(paramBundle);
  }
  
  public final void onConnectionFailed(ConnectionResult paramConnectionResult)
  {
    zzy();
    this.zzfp.zza(paramConnectionResult, this.mApi, this.zzfo);
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    zzy();
    this.zzfp.onConnectionSuspended(paramInt);
  }
  
  public final void zza(zzq paramzzq)
  {
    this.zzfp = paramzzq;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */