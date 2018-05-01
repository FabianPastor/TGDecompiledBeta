package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public abstract interface zzabc
{
  public abstract ConnectionResult blockingConnect();
  
  public abstract ConnectionResult blockingConnect(long paramLong, TimeUnit paramTimeUnit);
  
  public abstract void connect();
  
  public abstract void disconnect();
  
  public abstract void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString);
  
  @Nullable
  public abstract ConnectionResult getConnectionResult(@NonNull Api<?> paramApi);
  
  public abstract boolean isConnected();
  
  public abstract boolean isConnecting();
  
  public abstract <A extends Api.zzb, R extends Result, T extends zzaad.zza<R, A>> T zza(@NonNull T paramT);
  
  public abstract boolean zza(zzabq paramzzabq);
  
  public abstract <A extends Api.zzb, T extends zzaad.zza<? extends Result, A>> T zzb(@NonNull T paramT);
  
  public abstract void zzvM();
  
  public abstract void zzvn();
  
  public static abstract interface zza
  {
    public abstract void zzc(int paramInt, boolean paramBoolean);
    
    public abstract void zzc(ConnectionResult paramConnectionResult);
    
    public abstract void zzo(Bundle paramBundle);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzabc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */