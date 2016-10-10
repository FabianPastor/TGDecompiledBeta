package com.google.android.gms.common.api;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zze.zzf;
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.common.internal.zzr;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class Api<O extends ApiOptions>
{
  private final String mName;
  private final zza<?, O> vi;
  private final zzh<?, O> vj;
  private final zzf<?> vk;
  private final zzi<?> vl;
  
  public <C extends zze> Api(String paramString, zza<C, O> paramzza, zzf<C> paramzzf)
  {
    zzac.zzb(paramzza, "Cannot construct an Api with a null ClientBuilder");
    zzac.zzb(paramzzf, "Cannot construct an Api with a null ClientKey");
    this.mName = paramString;
    this.vi = paramzza;
    this.vj = null;
    this.vk = paramzzf;
    this.vl = null;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public zzd<?, O> zzapm()
  {
    if (zzapq()) {
      return null;
    }
    return this.vi;
  }
  
  public zza<?, O> zzapn()
  {
    if (this.vi != null) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zza(bool, "This API was constructed with a SimpleClientBuilder. Use getSimpleClientBuilder");
      return this.vi;
    }
  }
  
  public zzh<?, O> zzapo()
  {
    zzac.zza(false, "This API was constructed with a ClientBuilder. Use getClientBuilder");
    return null;
  }
  
  public zzc<?> zzapp()
  {
    if (this.vk != null) {
      return this.vk;
    }
    throw new IllegalStateException("This API was constructed with null client keys. This should not be possible.");
  }
  
  public boolean zzapq()
  {
    return false;
  }
  
  public static abstract interface ApiOptions
  {
    public static abstract interface HasOptions
      extends Api.ApiOptions
    {}
    
    public static final class NoOptions
      implements Api.ApiOptions.NotRequiredOptions
    {}
    
    public static abstract interface NotRequiredOptions
      extends Api.ApiOptions
    {}
    
    public static abstract interface Optional
      extends Api.ApiOptions.HasOptions, Api.ApiOptions.NotRequiredOptions
    {}
  }
  
  public static abstract class zza<T extends Api.zze, O>
    extends Api.zzd<T, O>
  {
    public abstract T zza(Context paramContext, Looper paramLooper, zzh paramzzh, O paramO, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener);
  }
  
  public static abstract interface zzb {}
  
  public static class zzc<C extends Api.zzb> {}
  
  public static abstract class zzd<T extends Api.zzb, O>
  {
    public int getPriority()
    {
      return Integer.MAX_VALUE;
    }
    
    public List<Scope> zzp(O paramO)
    {
      return Collections.emptyList();
    }
  }
  
  public static abstract interface zze
    extends Api.zzb
  {
    public abstract void disconnect();
    
    public abstract void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString);
    
    public abstract boolean isConnected();
    
    public abstract boolean isConnecting();
    
    public abstract void zza(zze.zzf paramzzf);
    
    public abstract void zza(zzr paramzzr, Set<Scope> paramSet);
    
    public abstract boolean zzahd();
    
    public abstract boolean zzahs();
    
    public abstract Intent zzaht();
    
    public abstract boolean zzapr();
    
    @Nullable
    public abstract IBinder zzaps();
  }
  
  public static final class zzf<C extends Api.zze>
    extends Api.zzc<C>
  {}
  
  public static abstract interface zzg<T extends IInterface>
    extends Api.zzb
  {
    public abstract void zza(int paramInt, T paramT);
    
    public abstract T zzh(IBinder paramIBinder);
    
    public abstract String zzix();
    
    public abstract String zziy();
  }
  
  public static abstract class zzh<T extends Api.zzg, O>
    extends Api.zzd<T, O>
  {
    public abstract int zzapt();
    
    public abstract T zzr(O paramO);
  }
  
  public static final class zzi<C extends Api.zzg>
    extends Api.zzc<C>
  {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/Api.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */