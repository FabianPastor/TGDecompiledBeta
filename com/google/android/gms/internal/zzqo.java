package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import java.util.Collections;
import java.util.Queue;

public class zzqo
  implements zzqq
{
  private final zzqr xk;
  
  public zzqo(zzqr paramzzqr)
  {
    this.xk = paramzzqr;
  }
  
  public void begin()
  {
    this.xk.zzary();
    this.xk.wV.xX = Collections.emptySet();
  }
  
  public void connect()
  {
    this.xk.zzarw();
  }
  
  public boolean disconnect()
  {
    return true;
  }
  
  public void onConnected(Bundle paramBundle) {}
  
  public void onConnectionSuspended(int paramInt) {}
  
  public void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, int paramInt) {}
  
  public <A extends Api.zzb, R extends Result, T extends zzqc.zza<R, A>> T zzc(T paramT)
  {
    this.xk.wV.xQ.add(paramT);
    return paramT;
  }
  
  public <A extends Api.zzb, T extends zzqc.zza<? extends Result, A>> T zzd(T paramT)
  {
    throw new IllegalStateException("GoogleApiClient is not connected yet.");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */