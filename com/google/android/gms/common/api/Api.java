package com.google.android.gms.common.api;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.internal.BaseGmsClient.ConnectionProgressReportCallbacks;
import com.google.android.gms.common.internal.BaseGmsClient.SignOutCallbacks;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.common.internal.Preconditions;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class Api<O extends ApiOptions>
{
  private final String mName;
  private final AbstractClientBuilder<?, O> zzby;
  private final zza<?, O> zzbz;
  private final ClientKey<?> zzca;
  private final zzb<?> zzcb;
  
  public <C extends Client> Api(String paramString, AbstractClientBuilder<C, O> paramAbstractClientBuilder, ClientKey<C> paramClientKey)
  {
    Preconditions.checkNotNull(paramAbstractClientBuilder, "Cannot construct an Api with a null ClientBuilder");
    Preconditions.checkNotNull(paramClientKey, "Cannot construct an Api with a null ClientKey");
    this.mName = paramString;
    this.zzby = paramAbstractClientBuilder;
    this.zzbz = null;
    this.zzca = paramClientKey;
    this.zzcb = null;
  }
  
  public final AnyClientKey<?> getClientKey()
  {
    if (this.zzca != null) {
      return this.zzca;
    }
    throw new IllegalStateException("This API was constructed with null client keys. This should not be possible.");
  }
  
  public final String getName()
  {
    return this.mName;
  }
  
  public final BaseClientBuilder<?, O> zzj()
  {
    return this.zzby;
  }
  
  public final AbstractClientBuilder<?, O> zzk()
  {
    if (this.zzby != null) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool, "This API was constructed with a SimpleClientBuilder. Use getSimpleClientBuilder");
      return this.zzby;
    }
  }
  
  public static abstract class AbstractClientBuilder<T extends Api.Client, O>
    extends Api.BaseClientBuilder<T, O>
  {
    public abstract T buildClient(Context paramContext, Looper paramLooper, ClientSettings paramClientSettings, O paramO, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener);
  }
  
  public static abstract interface AnyClient {}
  
  public static class AnyClientKey<C extends Api.AnyClient> {}
  
  public static abstract interface ApiOptions
  {
    public static abstract interface HasAccountOptions
      extends Api.ApiOptions.HasOptions, Api.ApiOptions.NotRequiredOptions
    {
      public abstract Account getAccount();
    }
    
    public static abstract interface HasGoogleSignInAccountOptions
      extends Api.ApiOptions.HasOptions
    {
      public abstract GoogleSignInAccount getGoogleSignInAccount();
    }
    
    public static abstract interface HasOptions
      extends Api.ApiOptions
    {}
    
    public static abstract interface NotRequiredOptions
      extends Api.ApiOptions
    {}
    
    public static abstract interface Optional
      extends Api.ApiOptions.HasOptions, Api.ApiOptions.NotRequiredOptions
    {}
  }
  
  public static class BaseClientBuilder<T extends Api.AnyClient, O>
  {
    public List<Scope> getImpliedScopes(O paramO)
    {
      return Collections.emptyList();
    }
    
    public int getPriority()
    {
      return Integer.MAX_VALUE;
    }
  }
  
  public static abstract interface Client
    extends Api.AnyClient
  {
    public abstract void connect(BaseGmsClient.ConnectionProgressReportCallbacks paramConnectionProgressReportCallbacks);
    
    public abstract void disconnect();
    
    public abstract void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString);
    
    public abstract Feature[] getAvailableFeatures();
    
    public abstract String getEndpointPackageName();
    
    public abstract int getMinApkVersion();
    
    public abstract void getRemoteService(IAccountAccessor paramIAccountAccessor, Set<Scope> paramSet);
    
    public abstract IBinder getServiceBrokerBinder();
    
    public abstract Intent getSignInIntent();
    
    public abstract boolean isConnected();
    
    public abstract boolean isConnecting();
    
    public abstract void onUserSignOut(BaseGmsClient.SignOutCallbacks paramSignOutCallbacks);
    
    public abstract boolean providesSignIn();
    
    public abstract boolean requiresGooglePlayServices();
    
    public abstract boolean requiresSignIn();
  }
  
  public static final class ClientKey<C extends Api.Client>
    extends Api.AnyClientKey<C>
  {}
  
  public static abstract interface SimpleClient<T extends IInterface>
    extends Api.AnyClient
  {
    public abstract T createServiceInterface(IBinder paramIBinder);
    
    public abstract String getServiceDescriptor();
    
    public abstract String getStartServiceAction();
    
    public abstract void setState(int paramInt, T paramT);
  }
  
  public static class zza<T extends Api.SimpleClient, O>
    extends Api.BaseClientBuilder<T, O>
  {}
  
  public static final class zzb<C extends Api.SimpleClient>
    extends Api.AnyClientKey<C>
  {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/Api.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */