package com.google.android.gms.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.internal.zzz;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzan;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzbr;
import com.google.android.gms.common.internal.zzd;
import com.google.android.gms.common.internal.zzm;
import com.google.android.gms.common.internal.zzr;

public final class zzcxn
  extends zzab<zzcxl>
  implements zzcxd
{
  private final zzr zzfpx;
  private Integer zzfzj;
  private final boolean zzkbz = true;
  private final Bundle zzkca;
  
  private zzcxn(Context paramContext, Looper paramLooper, boolean paramBoolean, zzr paramzzr, Bundle paramBundle, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, 44, paramzzr, paramConnectionCallbacks, paramOnConnectionFailedListener);
    this.zzfpx = paramzzr;
    this.zzkca = paramBundle;
    this.zzfzj = paramzzr.zzalc();
  }
  
  public zzcxn(Context paramContext, Looper paramLooper, boolean paramBoolean, zzr paramzzr, zzcxe paramzzcxe, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    this(paramContext, paramLooper, true, paramzzr, zza(paramzzr), paramConnectionCallbacks, paramOnConnectionFailedListener);
  }
  
  public static Bundle zza(zzr paramzzr)
  {
    zzcxe localzzcxe = paramzzr.zzalb();
    Integer localInteger = paramzzr.zzalc();
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("com.google.android.gms.signin.internal.clientRequestedAccount", paramzzr.getAccount());
    if (localInteger != null) {
      localBundle.putInt("com.google.android.gms.common.internal.ClientSettings.sessionId", localInteger.intValue());
    }
    if (localzzcxe != null)
    {
      localBundle.putBoolean("com.google.android.gms.signin.internal.offlineAccessRequested", localzzcxe.zzbdc());
      localBundle.putBoolean("com.google.android.gms.signin.internal.idTokenRequested", localzzcxe.isIdTokenRequested());
      localBundle.putString("com.google.android.gms.signin.internal.serverClientId", localzzcxe.getServerClientId());
      localBundle.putBoolean("com.google.android.gms.signin.internal.usePromptModeForAuthCode", true);
      localBundle.putBoolean("com.google.android.gms.signin.internal.forceCodeForRefreshToken", localzzcxe.zzbdd());
      localBundle.putString("com.google.android.gms.signin.internal.hostedDomain", localzzcxe.zzbde());
      localBundle.putBoolean("com.google.android.gms.signin.internal.waitForAccessTokenRefresh", localzzcxe.zzbdf());
      if (localzzcxe.zzbdg() != null) {
        localBundle.putLong("com.google.android.gms.signin.internal.authApiSignInModuleVersion", localzzcxe.zzbdg().longValue());
      }
      if (localzzcxe.zzbdh() != null) {
        localBundle.putLong("com.google.android.gms.signin.internal.realClientLibraryVersion", localzzcxe.zzbdh().longValue());
      }
    }
    return localBundle;
  }
  
  public final void connect()
  {
    zza(new zzm(this));
  }
  
  public final void zza(zzan paramzzan, boolean paramBoolean)
  {
    try
    {
      ((zzcxl)zzakn()).zza(paramzzan, this.zzfzj.intValue(), paramBoolean);
      return;
    }
    catch (RemoteException paramzzan)
    {
      Log.w("SignInClientImpl", "Remote service probably died when saveDefaultAccount is called");
    }
  }
  
  public final void zza(zzcxj paramzzcxj)
  {
    zzbq.checkNotNull(paramzzcxj, "Expecting a valid ISignInCallbacks");
    try
    {
      Account localAccount = this.zzfpx.zzakt();
      Object localObject = null;
      if ("<<default account>>".equals(localAccount.name)) {
        localObject = zzz.zzbt(getContext()).zzabt();
      }
      localObject = new zzbr(localAccount, this.zzfzj.intValue(), (GoogleSignInAccount)localObject);
      ((zzcxl)zzakn()).zza(new zzcxo((zzbr)localObject), paramzzcxj);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("SignInClientImpl", "Remote service probably died when signIn is called");
      try
      {
        paramzzcxj.zzb(new zzcxq(8));
        return;
      }
      catch (RemoteException paramzzcxj)
      {
        Log.wtf("SignInClientImpl", "ISignInCallbacks#onSignInComplete should be executed from the same process, unexpected RemoteException.", localRemoteException);
      }
    }
  }
  
  protected final Bundle zzaap()
  {
    String str = this.zzfpx.zzaky();
    if (!getContext().getPackageName().equals(str)) {
      this.zzkca.putString("com.google.android.gms.signin.internal.realClientPackageName", this.zzfpx.zzaky());
    }
    return this.zzkca;
  }
  
  public final boolean zzaay()
  {
    return this.zzkbz;
  }
  
  public final void zzbdb()
  {
    try
    {
      ((zzcxl)zzakn()).zzeh(this.zzfzj.intValue());
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("SignInClientImpl", "Remote service probably died when clearAccountFromSessionStore is called");
    }
  }
  
  protected final String zzhi()
  {
    return "com.google.android.gms.signin.service.START";
  }
  
  protected final String zzhj()
  {
    return "com.google.android.gms.signin.internal.ISignInService";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcxn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */