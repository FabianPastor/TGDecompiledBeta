package com.google.android.gms.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzad;
import com.google.android.gms.common.internal.zzf.zzi;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzr;

public class zzaxy
  extends com.google.android.gms.common.internal.zzl<zzaxv>
  implements zzaxn
{
  private Integer zzaEe;
  private final zzg zzazs;
  private final Bundle zzbCf;
  private final boolean zzbCq;
  
  public zzaxy(Context paramContext, Looper paramLooper, boolean paramBoolean, zzg paramzzg, Bundle paramBundle, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, 44, paramzzg, paramConnectionCallbacks, paramOnConnectionFailedListener);
    this.zzbCq = paramBoolean;
    this.zzazs = paramzzg;
    this.zzbCf = paramBundle;
    this.zzaEe = paramzzg.zzxl();
  }
  
  public zzaxy(Context paramContext, Looper paramLooper, boolean paramBoolean, zzg paramzzg, zzaxo paramzzaxo, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this(paramContext, paramLooper, paramBoolean, paramzzg, zza(paramzzg), paramConnectionCallbacks, paramOnConnectionFailedListener);
  }
  
  private zzad zzOn()
  {
    Account localAccount = this.zzazs.zzwU();
    GoogleSignInAccount localGoogleSignInAccount = null;
    if ("<<default account>>".equals(localAccount.name)) {
      localGoogleSignInAccount = com.google.android.gms.auth.api.signin.internal.zzl.zzaa(getContext()).zzrc();
    }
    return new zzad(localAccount, this.zzaEe.intValue(), localGoogleSignInAccount);
  }
  
  public static Bundle zza(zzg paramzzg)
  {
    zzaxo localzzaxo = paramzzg.zzxk();
    Integer localInteger = paramzzg.zzxl();
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("com.google.android.gms.signin.internal.clientRequestedAccount", paramzzg.getAccount());
    if (localInteger != null) {
      localBundle.putInt("com.google.android.gms.common.internal.ClientSettings.sessionId", localInteger.intValue());
    }
    if (localzzaxo != null)
    {
      localBundle.putBoolean("com.google.android.gms.signin.internal.offlineAccessRequested", localzzaxo.zzOf());
      localBundle.putBoolean("com.google.android.gms.signin.internal.idTokenRequested", localzzaxo.zzqK());
      localBundle.putString("com.google.android.gms.signin.internal.serverClientId", localzzaxo.zzqN());
      localBundle.putBoolean("com.google.android.gms.signin.internal.usePromptModeForAuthCode", true);
      localBundle.putBoolean("com.google.android.gms.signin.internal.forceCodeForRefreshToken", localzzaxo.zzqM());
      localBundle.putString("com.google.android.gms.signin.internal.hostedDomain", localzzaxo.zzqO());
      localBundle.putBoolean("com.google.android.gms.signin.internal.waitForAccessTokenRefresh", localzzaxo.zzOg());
      if (localzzaxo.zzOh() != null) {
        localBundle.putLong("com.google.android.gms.signin.internal.authApiSignInModuleVersion", localzzaxo.zzOh().longValue());
      }
      if (localzzaxo.zzOi() != null) {
        localBundle.putLong("com.google.android.gms.signin.internal.realClientLibraryVersion", localzzaxo.zzOi().longValue());
      }
    }
    return localBundle;
  }
  
  public void connect()
  {
    zza(new zzf.zzi(this));
  }
  
  public void zzOe()
  {
    try
    {
      ((zzaxv)zzwW()).zzmK(this.zzaEe.intValue());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("SignInClientImpl", "Remote service probably died when clearAccountFromSessionStore is called");
    }
  }
  
  public void zza(zzr paramzzr, boolean paramBoolean)
  {
    try
    {
      ((zzaxv)zzwW()).zza(paramzzr, this.zzaEe.intValue(), paramBoolean);
      return;
    }
    catch (RemoteException paramzzr)
    {
      Log.w("SignInClientImpl", "Remote service probably died when saveDefaultAccount is called");
    }
  }
  
  public void zza(zzaxu paramzzaxu)
  {
    zzac.zzb(paramzzaxu, "Expecting a valid ISignInCallbacks");
    try
    {
      zzad localzzad = zzOn();
      ((zzaxv)zzwW()).zza(new zzaxz(localzzad), paramzzaxu);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("SignInClientImpl", "Remote service probably died when signIn is called");
      try
      {
        paramzzaxu.zzb(new zzayb(8));
        return;
      }
      catch (RemoteException paramzzaxu)
      {
        Log.wtf("SignInClientImpl", "ISignInCallbacks#onSignInComplete should be executed from the same process, unexpected RemoteException.", localRemoteException);
      }
    }
  }
  
  protected zzaxv zzeZ(IBinder paramIBinder)
  {
    return zzaxv.zza.zzeY(paramIBinder);
  }
  
  protected String zzeu()
  {
    return "com.google.android.gms.signin.service.START";
  }
  
  protected String zzev()
  {
    return "com.google.android.gms.signin.internal.ISignInService";
  }
  
  public boolean zzqD()
  {
    return this.zzbCq;
  }
  
  protected Bundle zzql()
  {
    String str = this.zzazs.zzxh();
    if (!getContext().getPackageName().equals(str)) {
      this.zzbCf.putString("com.google.android.gms.signin.internal.realClientPackageName", this.zzazs.zzxh());
    }
    return this.zzbCf;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */