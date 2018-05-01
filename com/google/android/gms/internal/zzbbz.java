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

public class zzbbz
  extends GoogleApiClient
{
  private final UnsupportedOperationException zzaCY;
  
  public zzbbz(String paramString)
  {
    this.zzaCY = new UnsupportedOperationException(paramString);
  }
  
  public ConnectionResult blockingConnect()
  {
    throw this.zzaCY;
  }
  
  public ConnectionResult blockingConnect(long paramLong, @NonNull TimeUnit paramTimeUnit)
  {
    throw this.zzaCY;
  }
  
  public PendingResult<Status> clearDefaultAccountAndReconnect()
  {
    throw this.zzaCY;
  }
  
  public void connect()
  {
    throw this.zzaCY;
  }
  
  public void disconnect()
  {
    throw this.zzaCY;
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    throw this.zzaCY;
  }
  
  @NonNull
  public ConnectionResult getConnectionResult(@NonNull Api<?> paramApi)
  {
    throw this.zzaCY;
  }
  
  public boolean hasConnectedApi(@NonNull Api<?> paramApi)
  {
    throw this.zzaCY;
  }
  
  public boolean isConnected()
  {
    throw this.zzaCY;
  }
  
  public boolean isConnecting()
  {
    throw this.zzaCY;
  }
  
  public boolean isConnectionCallbacksRegistered(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    throw this.zzaCY;
  }
  
  public boolean isConnectionFailedListenerRegistered(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    throw this.zzaCY;
  }
  
  public void reconnect()
  {
    throw this.zzaCY;
  }
  
  public void registerConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    throw this.zzaCY;
  }
  
  public void registerConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    throw this.zzaCY;
  }
  
  public void stopAutoManage(@NonNull FragmentActivity paramFragmentActivity)
  {
    throw this.zzaCY;
  }
  
  public void unregisterConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks)
  {
    throw this.zzaCY;
  }
  
  public void unregisterConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    throw this.zzaCY;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbbz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */