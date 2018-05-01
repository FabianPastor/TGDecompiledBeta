package com.google.android.gms.common.api.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.ResolveAccountResponse;
import com.google.android.gms.signin.SignIn;
import com.google.android.gms.signin.SignInClient;
import com.google.android.gms.signin.SignInOptions;
import com.google.android.gms.signin.internal.BaseSignInCallbacks;
import com.google.android.gms.signin.internal.SignInResponse;
import java.util.Set;

public final class zzby
  extends BaseSignInCallbacks
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
  private static Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> zzlv = SignIn.CLIENT_BUILDER;
  private final Context mContext;
  private final Handler mHandler;
  private Set<Scope> mScopes;
  private final Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> zzby;
  private ClientSettings zzgf;
  private SignInClient zzhn;
  private zzcb zzlw;
  
  public zzby(Context paramContext, Handler paramHandler, ClientSettings paramClientSettings)
  {
    this(paramContext, paramHandler, paramClientSettings, zzlv);
  }
  
  public zzby(Context paramContext, Handler paramHandler, ClientSettings paramClientSettings, Api.AbstractClientBuilder<? extends SignInClient, SignInOptions> paramAbstractClientBuilder)
  {
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.zzgf = ((ClientSettings)Preconditions.checkNotNull(paramClientSettings, "ClientSettings must not be null"));
    this.mScopes = paramClientSettings.getRequiredScopes();
    this.zzby = paramAbstractClientBuilder;
  }
  
  private final void zzb(SignInResponse paramSignInResponse)
  {
    Object localObject = paramSignInResponse.getConnectionResult();
    if (((ConnectionResult)localObject).isSuccess())
    {
      localObject = paramSignInResponse.getResolveAccountResponse();
      paramSignInResponse = ((ResolveAccountResponse)localObject).getConnectionResult();
      if (!paramSignInResponse.isSuccess())
      {
        localObject = String.valueOf(paramSignInResponse);
        Log.wtf("SignInCoordinator", String.valueOf(localObject).length() + 48 + "Sign-in succeeded with resolve account failure: " + (String)localObject, new Exception());
        this.zzlw.zzg(paramSignInResponse);
        this.zzhn.disconnect();
        return;
      }
      this.zzlw.zza(((ResolveAccountResponse)localObject).getAccountAccessor(), this.mScopes);
    }
    for (;;)
    {
      this.zzhn.disconnect();
      break;
      this.zzlw.zzg((ConnectionResult)localObject);
    }
  }
  
  public final void onConnected(Bundle paramBundle)
  {
    this.zzhn.signIn(this);
  }
  
  public final void onConnectionFailed(ConnectionResult paramConnectionResult)
  {
    this.zzlw.zzg(paramConnectionResult);
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    this.zzhn.disconnect();
  }
  
  public final void onSignInComplete(SignInResponse paramSignInResponse)
  {
    this.mHandler.post(new zzca(this, paramSignInResponse));
  }
  
  public final void zza(zzcb paramzzcb)
  {
    if (this.zzhn != null) {
      this.zzhn.disconnect();
    }
    this.zzgf.setClientSessionId(Integer.valueOf(System.identityHashCode(this)));
    this.zzhn = ((SignInClient)this.zzby.buildClient(this.mContext, this.mHandler.getLooper(), this.zzgf, this.zzgf.getSignInOptions(), this, this));
    this.zzlw = paramzzcb;
    if ((this.mScopes == null) || (this.mScopes.isEmpty())) {
      this.mHandler.post(new zzbz(this));
    }
    for (;;)
    {
      return;
      this.zzhn.connect();
    }
  }
  
  public final SignInClient zzbt()
  {
    return this.zzhn;
  }
  
  public final void zzbz()
  {
    if (this.zzhn != null) {
      this.zzhn.disconnect();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzby.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */