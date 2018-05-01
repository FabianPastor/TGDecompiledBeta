package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;

public abstract interface zzbcw
{
  public abstract void begin();
  
  public abstract void connect();
  
  public abstract boolean disconnect();
  
  public abstract void onConnected(Bundle paramBundle);
  
  public abstract void onConnectionSuspended(int paramInt);
  
  public abstract void zza(ConnectionResult paramConnectionResult, Api<?> paramApi, boolean paramBoolean);
  
  public abstract <A extends Api.zzb, R extends Result, T extends zzbay<R, A>> T zzd(T paramT);
  
  public abstract <A extends Api.zzb, T extends zzbay<? extends Result, A>> T zze(T paramT);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbcw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */