package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class zzaap
  extends GoogleApiClient
{
  private final UnsupportedOperationException zzaBj;
  
  public zzaap(String paramString)
  {
    this.zzaBj = new UnsupportedOperationException(paramString);
  }
  
  public ConnectionResult blockingConnect()
  {
    throw this.zzaBj;
  }
  
  public ConnectionResult blockingConnect(long paramLong, @NonNull TimeUnit paramTimeUnit)
  {
    throw this.zzaBj;
  }
  
  public PendingResult<Status> clearDefaultAccountAndReconnect()
  {
    throw this.zzaBj;
  }
  
  public void connect()
  {
    throw this.zzaBj;
  }
  
  public void disconnect()
  {
    throw this.zzaBj;
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    throw this.zzaBj;
  }
  
  @NonNull
  public ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    throw this.zzaBj;
  }
  
  public boolean hasConnectedApi(@NonNull Api<?> paramApi)
  {
    throw this.zzaBj;
  }
  
  public boolean isConnected()
  {
    throw this.zzaBj;
  }
  
  public boolean isConnecting()
  {
    throw this.zzaBj;
  }
  
  public boolean isConnectionCallbacksRegistered(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    throw this.zzaBj;
  }
  
  public boolean isConnectionFailedListenerRegistered(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    throw this.zzaBj;
  }
  
  public void reconnect()
  {
    throw this.zzaBj;
  }
  
  public void registerConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    throw this.zzaBj;
  }
  
  public void registerConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    throw this.zzaBj;
  }
  
  public void stopAutoManage(@NonNull FragmentActivity paramFragmentActivity)
  {
    throw this.zzaBj;
  }
  
  public void unregisterConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    throw this.zzaBj;
  }
  
  public void unregisterConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    throw this.zzaBj;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */