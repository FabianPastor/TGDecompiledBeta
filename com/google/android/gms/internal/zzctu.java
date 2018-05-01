package com.google.android.gms.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.internal.zzy;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzal;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzbp;
import com.google.android.gms.common.internal.zzm;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.internal.zzz;

public final class zzctu
  extends zzz<zzcts>
  implements zzctk
{
  private final zzq zzaCA;
  private Integer zzaHn;
  private final Bundle zzbCL;
  private final boolean zzbCT;
  
  public zzctu(Context paramContext, Looper paramLooper, boolean paramBoolean, zzq paramzzq, Bundle paramBundle, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, 44, paramzzq, paramConnectionCallbacks, paramOnConnectionFailedListener);
    this.zzbCT = paramBoolean;
    this.zzaCA = paramzzq;
    this.zzbCL = paramBundle;
    this.zzaHn = paramzzq.zzru();
  }
  
  public zzctu(Context paramContext, Looper paramLooper, boolean paramBoolean, zzq paramzzq, zzctl paramzzctl, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this(paramContext, paramLooper, true, paramzzq, zza(paramzzq), paramConnectionCallbacks, paramOnConnectionFailedListener);
  }
  
  public static Bundle zza(zzq paramzzq)
  {
    zzctl localzzctl = paramzzq.zzrt();
    Integer localInteger = paramzzq.zzru();
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("com.google.android.gms.signin.internal.clientRequestedAccount", paramzzq.getAccount());
    if (localInteger != null) {
      localBundle.putInt("com.google.android.gms.common.internal.ClientSettings.sessionId", localInteger.intValue());
    }
    if (localzzctl != null)
    {
      localBundle.putBoolean("com.google.android.gms.signin.internal.offlineAccessRequested", localzzctl.zzAr());
      localBundle.putBoolean("com.google.android.gms.signin.internal.idTokenRequested", localzzctl.isIdTokenRequested());
      localBundle.putString("com.google.android.gms.signin.internal.serverClientId", localzzctl.getServerClientId());
      localBundle.putBoolean("com.google.android.gms.signin.internal.usePromptModeForAuthCode", true);
      localBundle.putBoolean("com.google.android.gms.signin.internal.forceCodeForRefreshToken", localzzctl.zzAs());
      localBundle.putString("com.google.android.gms.signin.internal.hostedDomain", localzzctl.zzAt());
      localBundle.putBoolean("com.google.android.gms.signin.internal.waitForAccessTokenRefresh", localzzctl.zzAu());
      if (localzzctl.zzAv() != null) {
        localBundle.putLong("com.google.android.gms.signin.internal.authApiSignInModuleVersion", localzzctl.zzAv().longValue());
      }
      if (localzzctl.zzAw() != null) {
        localBundle.putLong("com.google.android.gms.signin.internal.realClientLibraryVersion", localzzctl.zzAw().longValue());
      }
    }
    return localBundle;
  }
  
  public final void connect()
  {
    zza(new zzm(this));
  }
  
  public final void zzAq()
  {
    try
    {
      ((zzcts)zzrf()).zzbv(this.zzaHn.intValue());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("SignInClientImpl", "Remote service probably died when clearAccountFromSessionStore is called");
    }
  }
  
  public final void zza(zzal paramzzal, boolean paramBoolean)
  {
    try
    {
      ((zzcts)zzrf()).zza(paramzzal, this.zzaHn.intValue(), paramBoolean);
      return;
    }
    catch (RemoteException paramzzal)
    {
      Log.w("SignInClientImpl", "Remote service probably died when saveDefaultAccount is called");
    }
  }
  
  public final void zza(zzctq paramzzctq)
  {
    zzbo.zzb(paramzzctq, "Expecting a valid ISignInCallbacks");
    try
    {
      Account localAccount = this.zzaCA.zzrl();
      Object localObject = null;
      if ("<<default account>>".equals(localAccount.name)) {
        localObject = zzy.zzaj(getContext()).zzmN();
      }
      localObject = new zzbp(localAccount, this.zzaHn.intValue(), (GoogleSignInAccount)localObject);
      ((zzcts)zzrf()).zza(new zzctv((zzbp)localObject), paramzzctq);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("SignInClientImpl", "Remote service probably died when signIn is called");
      try
      {
        paramzzctq.zzb(new zzctx(8));
        return;
      }
      catch (RemoteException paramzzctq)
      {
        Log.wtf("SignInClientImpl", "ISignInCallbacks#onSignInComplete should be executed from the same process, unexpected RemoteException.", localRemoteException);
      }
    }
  }
  
  protected final String zzdb()
  {
    return "com.google.android.gms.signin.service.START";
  }
  
  protected final String zzdc()
  {
    return "com.google.android.gms.signin.internal.ISignInService";
  }
  
  protected final Bundle zzmo()
  {
    String str = this.zzaCA.zzrq();
    if (!getContext().getPackageName().equals(str)) {
      this.zzbCL.putString("com.google.android.gms.signin.internal.realClientPackageName", this.zzaCA.zzrq());
    }
    return this.zzbCL;
  }
  
  public final boolean zzmv()
  {
    return this.zzbCT;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzctu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */