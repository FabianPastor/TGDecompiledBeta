package com.google.android.gms.signin.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.internal.zzk;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.ResolveAccountRequest;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zze.zzi;
import com.google.android.gms.common.internal.zzh;
import com.google.android.gms.common.internal.zzl;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.internal.zzwz;
import com.google.android.gms.internal.zzxa;

public class zzg
  extends zzl<zze>
  implements zzwz
{
  private Integer Ca;
  private final boolean aAk;
  private final Bundle aAl;
  private final zzh xB;
  
  public zzg(Context paramContext, Looper paramLooper, boolean paramBoolean, zzh paramzzh, Bundle paramBundle, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, 44, paramzzh, paramConnectionCallbacks, paramOnConnectionFailedListener);
    this.aAk = paramBoolean;
    this.xB = paramzzh;
    this.aAl = paramBundle;
    this.Ca = paramzzh.zzaun();
  }
  
  public zzg(Context paramContext, Looper paramLooper, boolean paramBoolean, zzh paramzzh, zzxa paramzzxa, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this(paramContext, paramLooper, paramBoolean, paramzzh, zza(paramzzh), paramConnectionCallbacks, paramOnConnectionFailedListener);
  }
  
  public static Bundle zza(zzh paramzzh)
  {
    zzxa localzzxa = paramzzh.zzaum();
    Integer localInteger = paramzzh.zzaun();
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("com.google.android.gms.signin.internal.clientRequestedAccount", paramzzh.getAccount());
    if (localInteger != null) {
      localBundle.putInt("com.google.android.gms.common.internal.ClientSettings.sessionId", localInteger.intValue());
    }
    if (localzzxa != null)
    {
      localBundle.putBoolean("com.google.android.gms.signin.internal.offlineAccessRequested", localzzxa.zzcdb());
      localBundle.putBoolean("com.google.android.gms.signin.internal.idTokenRequested", localzzxa.zzahk());
      localBundle.putString("com.google.android.gms.signin.internal.serverClientId", localzzxa.zzahn());
      localBundle.putBoolean("com.google.android.gms.signin.internal.usePromptModeForAuthCode", true);
      localBundle.putBoolean("com.google.android.gms.signin.internal.forceCodeForRefreshToken", localzzxa.zzahm());
      localBundle.putString("com.google.android.gms.signin.internal.hostedDomain", localzzxa.zzaho());
      localBundle.putBoolean("com.google.android.gms.signin.internal.waitForAccessTokenRefresh", localzzxa.zzcdc());
      if (localzzxa.zzcdd() != null) {
        localBundle.putLong("com.google.android.gms.signin.internal.authApiSignInModuleVersion", localzzxa.zzcdd().longValue());
      }
      if (localzzxa.zzcde() != null) {
        localBundle.putLong("com.google.android.gms.signin.internal.realClientLibraryVersion", localzzxa.zzcde().longValue());
      }
    }
    return localBundle;
  }
  
  private ResolveAccountRequest zzcdj()
  {
    Account localAccount = this.xB.zzatv();
    GoogleSignInAccount localGoogleSignInAccount = null;
    if ("<<default account>>".equals(localAccount.name)) {
      localGoogleSignInAccount = zzk.zzbd(getContext()).zzaic();
    }
    return new ResolveAccountRequest(localAccount, this.Ca.intValue(), localGoogleSignInAccount);
  }
  
  public void connect()
  {
    zza(new zze.zzi(this));
  }
  
  public void zza(zzr paramzzr, boolean paramBoolean)
  {
    try
    {
      ((zze)zzatx()).zza(paramzzr, this.Ca.intValue(), paramBoolean);
      return;
    }
    catch (RemoteException paramzzr)
    {
      Log.w("SignInClientImpl", "Remote service probably died when saveDefaultAccount is called");
    }
  }
  
  public void zza(zzd paramzzd)
  {
    zzac.zzb(paramzzd, "Expecting a valid ISignInCallbacks");
    try
    {
      ResolveAccountRequest localResolveAccountRequest = zzcdj();
      ((zze)zzatx()).zza(new SignInRequest(localResolveAccountRequest), paramzzd);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("SignInClientImpl", "Remote service probably died when signIn is called");
      try
      {
        paramzzd.zzb(new SignInResponse(8));
        return;
      }
      catch (RemoteException paramzzd)
      {
        Log.wtf("SignInClientImpl", "ISignInCallbacks#onSignInComplete should be executed from the same process, unexpected RemoteException.", localRemoteException);
      }
    }
  }
  
  protected Bundle zzagl()
  {
    String str = this.xB.zzauj();
    if (!getContext().getPackageName().equals(str)) {
      this.aAl.putString("com.google.android.gms.signin.internal.realClientPackageName", this.xB.zzauj());
    }
    return this.aAl;
  }
  
  public boolean zzahd()
  {
    return this.aAk;
  }
  
  public void zzcda()
  {
    try
    {
      ((zze)zzatx()).zzaaf(this.Ca.intValue());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("SignInClientImpl", "Remote service probably died when clearAccountFromSessionStore is called");
    }
  }
  
  protected String zzix()
  {
    return "com.google.android.gms.signin.service.START";
  }
  
  protected String zziy()
  {
    return "com.google.android.gms.signin.internal.ISignInService";
  }
  
  protected zze zzlc(IBinder paramIBinder)
  {
    return zze.zza.zzlb(paramIBinder);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */