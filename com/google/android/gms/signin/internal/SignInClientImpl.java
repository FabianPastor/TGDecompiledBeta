package com.google.android.gms.signin.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.BaseGmsClient;
import com.google.android.gms.common.internal.BaseGmsClient.LegacyClientCallbackAdapter;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.GmsClient;
import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ResolveAccountRequest;
import com.google.android.gms.signin.SignInClient;
import com.google.android.gms.signin.SignInOptions;

public class SignInClientImpl
  extends GmsClient<ISignInService>
  implements SignInClient
{
  private final Bundle zzada;
  private final boolean zzads;
  private final ClientSettings zzgf;
  private Integer zzsc;
  
  public SignInClientImpl(Context paramContext, Looper paramLooper, boolean paramBoolean, ClientSettings paramClientSettings, Bundle paramBundle, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, 44, paramClientSettings, paramConnectionCallbacks, paramOnConnectionFailedListener);
    this.zzads = paramBoolean;
    this.zzgf = paramClientSettings;
    this.zzada = paramBundle;
    this.zzsc = paramClientSettings.getClientSessionId();
  }
  
  public SignInClientImpl(Context paramContext, Looper paramLooper, boolean paramBoolean, ClientSettings paramClientSettings, SignInOptions paramSignInOptions, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this(paramContext, paramLooper, paramBoolean, paramClientSettings, createBundleFromClientSettings(paramClientSettings), paramConnectionCallbacks, paramOnConnectionFailedListener);
  }
  
  public static Bundle createBundleFromClientSettings(ClientSettings paramClientSettings)
  {
    SignInOptions localSignInOptions = paramClientSettings.getSignInOptions();
    Integer localInteger = paramClientSettings.getClientSessionId();
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("com.google.android.gms.signin.internal.clientRequestedAccount", paramClientSettings.getAccount());
    if (localInteger != null) {
      localBundle.putInt("com.google.android.gms.common.internal.ClientSettings.sessionId", localInteger.intValue());
    }
    if (localSignInOptions != null)
    {
      localBundle.putBoolean("com.google.android.gms.signin.internal.offlineAccessRequested", localSignInOptions.isOfflineAccessRequested());
      localBundle.putBoolean("com.google.android.gms.signin.internal.idTokenRequested", localSignInOptions.isIdTokenRequested());
      localBundle.putString("com.google.android.gms.signin.internal.serverClientId", localSignInOptions.getServerClientId());
      localBundle.putBoolean("com.google.android.gms.signin.internal.usePromptModeForAuthCode", true);
      localBundle.putBoolean("com.google.android.gms.signin.internal.forceCodeForRefreshToken", localSignInOptions.isForceCodeForRefreshToken());
      localBundle.putString("com.google.android.gms.signin.internal.hostedDomain", localSignInOptions.getHostedDomain());
      localBundle.putBoolean("com.google.android.gms.signin.internal.waitForAccessTokenRefresh", localSignInOptions.waitForAccessTokenRefresh());
      if (localSignInOptions.getAuthApiSignInModuleVersion() != null) {
        localBundle.putLong("com.google.android.gms.signin.internal.authApiSignInModuleVersion", localSignInOptions.getAuthApiSignInModuleVersion().longValue());
      }
      if (localSignInOptions.getRealClientLibraryVersion() != null) {
        localBundle.putLong("com.google.android.gms.signin.internal.realClientLibraryVersion", localSignInOptions.getRealClientLibraryVersion().longValue());
      }
    }
    return localBundle;
  }
  
  public void clearAccountFromSessionStore()
  {
    try
    {
      ((ISignInService)getService()).clearAccountFromSessionStore(this.zzsc.intValue());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.w("SignInClientImpl", "Remote service probably died when clearAccountFromSessionStore is called");
      }
    }
  }
  
  public void connect()
  {
    connect(new BaseGmsClient.LegacyClientCallbackAdapter(this));
  }
  
  protected ISignInService createServiceInterface(IBinder paramIBinder)
  {
    return ISignInService.Stub.asInterface(paramIBinder);
  }
  
  protected Bundle getGetServiceRequestExtraArgs()
  {
    String str = this.zzgf.getRealClientPackageName();
    if (!getContext().getPackageName().equals(str)) {
      this.zzada.putString("com.google.android.gms.signin.internal.realClientPackageName", this.zzgf.getRealClientPackageName());
    }
    return this.zzada;
  }
  
  public int getMinApkVersion()
  {
    return 12451000;
  }
  
  protected String getServiceDescriptor()
  {
    return "com.google.android.gms.signin.internal.ISignInService";
  }
  
  protected String getStartServiceAction()
  {
    return "com.google.android.gms.signin.service.START";
  }
  
  public boolean requiresSignIn()
  {
    return this.zzads;
  }
  
  public void saveDefaultAccount(IAccountAccessor paramIAccountAccessor, boolean paramBoolean)
  {
    try
    {
      ((ISignInService)getService()).saveDefaultAccountToSharedPref(paramIAccountAccessor, this.zzsc.intValue(), paramBoolean);
      return;
    }
    catch (RemoteException paramIAccountAccessor)
    {
      for (;;)
      {
        Log.w("SignInClientImpl", "Remote service probably died when saveDefaultAccount is called");
      }
    }
  }
  
  public void signIn(ISignInCallbacks paramISignInCallbacks)
  {
    Preconditions.checkNotNull(paramISignInCallbacks, "Expecting a valid ISignInCallbacks");
    try
    {
      localObject1 = this.zzgf.getAccountOrDefault();
      Object localObject2 = null;
      if ("<<default account>>".equals(((Account)localObject1).name)) {
        localObject2 = Storage.getInstance(getContext()).getSavedDefaultGoogleSignInAccount();
      }
      ResolveAccountRequest localResolveAccountRequest = new com/google/android/gms/common/internal/ResolveAccountRequest;
      localResolveAccountRequest.<init>((Account)localObject1, this.zzsc.intValue(), (GoogleSignInAccount)localObject2);
      localObject2 = (ISignInService)getService();
      localObject1 = new com/google/android/gms/signin/internal/SignInRequest;
      ((SignInRequest)localObject1).<init>(localResolveAccountRequest);
      ((ISignInService)localObject2).signIn((SignInRequest)localObject1, paramISignInCallbacks);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Object localObject1;
        Log.w("SignInClientImpl", "Remote service probably died when signIn is called");
        try
        {
          localObject1 = new com/google/android/gms/signin/internal/SignInResponse;
          ((SignInResponse)localObject1).<init>(8);
          paramISignInCallbacks.onSignInComplete((SignInResponse)localObject1);
        }
        catch (RemoteException paramISignInCallbacks)
        {
          Log.wtf("SignInClientImpl", "ISignInCallbacks#onSignInComplete should be executed from the same process, unexpected RemoteException.", localRemoteException);
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/SignInClientImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */