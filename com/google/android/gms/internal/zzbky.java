package com.google.android.gms.internal;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.internal.zzl;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;
import com.google.android.gms.wallet.firstparty.zzf;
import com.google.android.gms.wallet.firstparty.zzj;
import com.google.android.gms.wallet.zzu;
import java.lang.ref.WeakReference;

public class zzbky
  extends zzl<zzbku>
{
  private final Context mContext;
  private final int mTheme;
  private final String zzaiu;
  private final int zzbRx;
  private final boolean zzbRy;
  
  public zzbky(Context paramContext, Looper paramLooper, zzg paramzzg, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramContext, paramLooper, 4, paramzzg, paramConnectionCallbacks, paramOnConnectionFailedListener);
    this.mContext = paramContext;
    this.zzbRx = paramInt1;
    this.zzaiu = paramzzg.getAccountName();
    this.mTheme = paramInt2;
    this.zzbRy = paramBoolean;
  }
  
  private Bundle zzTZ()
  {
    return zza(this.zzbRx, this.mContext.getPackageName(), this.zzaiu, this.mTheme, this.zzbRy);
  }
  
  @TargetApi(14)
  public static Bundle zza(int paramInt1, String paramString1, String paramString2, int paramInt2, boolean paramBoolean)
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("com.google.android.gms.wallet.EXTRA_ENVIRONMENT", paramInt1);
    localBundle.putBoolean("com.google.android.gms.wallet.EXTRA_USING_ANDROID_PAY_BRAND", paramBoolean);
    localBundle.putString("androidPackageName", paramString1);
    if (!TextUtils.isEmpty(paramString2)) {
      localBundle.putParcelable("com.google.android.gms.wallet.EXTRA_BUYER_ACCOUNT", new Account(paramString2, "com.google"));
    }
    localBundle.putInt("com.google.android.gms.wallet.EXTRA_THEME", paramInt2);
    return localBundle;
  }
  
  public void zza(FullWalletRequest paramFullWalletRequest, int paramInt)
  {
    zzc localzzc = new zzc(this.mContext, paramInt);
    Bundle localBundle = zzTZ();
    try
    {
      ((zzbku)zzxD()).zza(paramFullWalletRequest, localBundle, localzzc);
      return;
    }
    catch (RemoteException paramFullWalletRequest)
    {
      Log.e("WalletClientImpl", "RemoteException getting full wallet", paramFullWalletRequest);
      localzzc.zza(8, null, Bundle.EMPTY);
    }
  }
  
  public void zza(IsReadyToPayRequest paramIsReadyToPayRequest, zzaad.zzb<BooleanResult> paramzzb)
  {
    paramzzb = new zzb(paramzzb);
    Bundle localBundle = zzTZ();
    try
    {
      ((zzbku)zzxD()).zza(paramIsReadyToPayRequest, localBundle, paramzzb);
      return;
    }
    catch (RemoteException paramIsReadyToPayRequest)
    {
      Log.e("WalletClientImpl", "RemoteException during isReadyToPay", paramIsReadyToPayRequest);
      paramzzb.zza(Status.zzazz, false, Bundle.EMPTY);
    }
  }
  
  public void zza(MaskedWalletRequest paramMaskedWalletRequest, int paramInt)
  {
    Bundle localBundle = zzTZ();
    zzc localzzc = new zzc(this.mContext, paramInt);
    try
    {
      ((zzbku)zzxD()).zza(paramMaskedWalletRequest, localBundle, localzzc);
      return;
    }
    catch (RemoteException paramMaskedWalletRequest)
    {
      Log.e("WalletClientImpl", "RemoteException getting masked wallet", paramMaskedWalletRequest);
      localzzc.zza(8, null, Bundle.EMPTY);
    }
  }
  
  public void zza(NotifyTransactionStatusRequest paramNotifyTransactionStatusRequest)
  {
    Bundle localBundle = zzTZ();
    try
    {
      ((zzbku)zzxD()).zza(paramNotifyTransactionStatusRequest, localBundle);
      return;
    }
    catch (RemoteException paramNotifyTransactionStatusRequest) {}
  }
  
  protected String zzeA()
  {
    return "com.google.android.gms.wallet.internal.IOwService";
  }
  
  protected String zzez()
  {
    return "com.google.android.gms.wallet.service.BIND";
  }
  
  public void zzf(String paramString1, String paramString2, int paramInt)
  {
    Bundle localBundle = zzTZ();
    zzc localzzc = new zzc(this.mContext, paramInt);
    try
    {
      ((zzbku)zzxD()).zza(paramString1, paramString2, localBundle, localzzc);
      return;
    }
    catch (RemoteException paramString1)
    {
      Log.e("WalletClientImpl", "RemoteException changing masked wallet", paramString1);
      localzzc.zza(8, null, Bundle.EMPTY);
    }
  }
  
  protected zzbku zzfy(IBinder paramIBinder)
  {
    return zzbku.zza.zzfv(paramIBinder);
  }
  
  public void zzoY(int paramInt)
  {
    Bundle localBundle = zzTZ();
    zzc localzzc = new zzc(this.mContext, paramInt);
    try
    {
      ((zzbku)zzxD()).zza(localBundle, localzzc);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("WalletClientImpl", "RemoteException during checkForPreAuthorization", localRemoteException);
      localzzc.zza(8, false, Bundle.EMPTY);
    }
  }
  
  public void zzoZ(int paramInt)
  {
    Bundle localBundle = zzTZ();
    zzc localzzc = new zzc(this.mContext, paramInt);
    try
    {
      ((zzbku)zzxD()).zzb(localBundle, localzzc);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("WalletClientImpl", "RemoteException during isNewUser", localRemoteException);
      localzzc.zzb(8, false, Bundle.EMPTY);
    }
  }
  
  public boolean zzxE()
  {
    return true;
  }
  
  private static class zza
    extends zzbkw.zza
  {
    public void zza(int paramInt, FullWallet paramFullWallet, Bundle paramBundle) {}
    
    public void zza(int paramInt, MaskedWallet paramMaskedWallet, Bundle paramBundle) {}
    
    public void zza(int paramInt, boolean paramBoolean, Bundle paramBundle) {}
    
    public void zza(Status paramStatus, Bundle paramBundle) {}
    
    public void zza(Status paramStatus, zzf paramzzf, Bundle paramBundle) {}
    
    public void zza(Status paramStatus, zzj paramzzj, Bundle paramBundle) {}
    
    public void zza(Status paramStatus, zzu paramzzu, Bundle paramBundle) {}
    
    public void zza(Status paramStatus, boolean paramBoolean, Bundle paramBundle) {}
    
    public void zzb(int paramInt, boolean paramBoolean, Bundle paramBundle) {}
    
    public void zzb(Status paramStatus, Bundle paramBundle) {}
    
    public void zzm(int paramInt, Bundle paramBundle) {}
  }
  
  private static class zzb
    extends zzbky.zza
  {
    private final zzaad.zzb<BooleanResult> zzaGN;
    
    public zzb(zzaad.zzb<BooleanResult> paramzzb)
    {
      super();
      this.zzaGN = paramzzb;
    }
    
    public void zza(Status paramStatus, boolean paramBoolean, Bundle paramBundle)
    {
      this.zzaGN.setResult(new BooleanResult(paramStatus, paramBoolean));
    }
  }
  
  static final class zzc
    extends zzbky.zza
  {
    private final int zzazu;
    private final WeakReference<Activity> zzbSw;
    
    public zzc(Context paramContext, int paramInt)
    {
      super();
      this.zzbSw = new WeakReference((Activity)paramContext);
      this.zzazu = paramInt;
    }
    
    public void zza(int paramInt, FullWallet paramFullWallet, Bundle paramBundle)
    {
      Activity localActivity = (Activity)this.zzbSw.get();
      if (localActivity == null)
      {
        Log.d("WalletClientImpl", "Ignoring onFullWalletLoaded, Activity has gone");
        return;
      }
      Object localObject = null;
      if (paramBundle != null) {
        localObject = (PendingIntent)paramBundle.getParcelable("com.google.android.gms.wallet.EXTRA_PENDING_INTENT");
      }
      paramBundle = new ConnectionResult(paramInt, (PendingIntent)localObject);
      if (paramBundle.hasResolution()) {
        try
        {
          paramBundle.startResolutionForResult(localActivity, this.zzazu);
          return;
        }
        catch (IntentSender.SendIntentException paramFullWallet)
        {
          Log.w("WalletClientImpl", "Exception starting pending intent", paramFullWallet);
          return;
        }
      }
      localObject = new Intent();
      int i;
      if (paramBundle.isSuccess())
      {
        i = -1;
        ((Intent)localObject).putExtra("com.google.android.gms.wallet.EXTRA_FULL_WALLET", paramFullWallet);
        paramFullWallet = localActivity.createPendingResult(this.zzazu, (Intent)localObject, NUM);
        if (paramFullWallet == null) {
          Log.w("WalletClientImpl", "Null pending result returned for onFullWalletLoaded");
        }
      }
      else
      {
        if (paramInt == 408) {}
        for (i = 0;; i = 1)
        {
          ((Intent)localObject).putExtra("com.google.android.gms.wallet.EXTRA_ERROR_CODE", paramInt);
          break;
        }
      }
      try
      {
        paramFullWallet.send(i);
        return;
      }
      catch (PendingIntent.CanceledException paramFullWallet)
      {
        Log.w("WalletClientImpl", "Exception setting pending result", paramFullWallet);
      }
    }
    
    public void zza(int paramInt, MaskedWallet paramMaskedWallet, Bundle paramBundle)
    {
      Activity localActivity = (Activity)this.zzbSw.get();
      if (localActivity == null)
      {
        Log.d("WalletClientImpl", "Ignoring onMaskedWalletLoaded, Activity has gone");
        return;
      }
      Object localObject = null;
      if (paramBundle != null) {
        localObject = (PendingIntent)paramBundle.getParcelable("com.google.android.gms.wallet.EXTRA_PENDING_INTENT");
      }
      paramBundle = new ConnectionResult(paramInt, (PendingIntent)localObject);
      if (paramBundle.hasResolution()) {
        try
        {
          paramBundle.startResolutionForResult(localActivity, this.zzazu);
          return;
        }
        catch (IntentSender.SendIntentException paramMaskedWallet)
        {
          Log.w("WalletClientImpl", "Exception starting pending intent", paramMaskedWallet);
          return;
        }
      }
      localObject = new Intent();
      int i;
      if (paramBundle.isSuccess())
      {
        i = -1;
        ((Intent)localObject).putExtra("com.google.android.gms.wallet.EXTRA_MASKED_WALLET", paramMaskedWallet);
        paramMaskedWallet = localActivity.createPendingResult(this.zzazu, (Intent)localObject, NUM);
        if (paramMaskedWallet == null) {
          Log.w("WalletClientImpl", "Null pending result returned for onMaskedWalletLoaded");
        }
      }
      else
      {
        if (paramInt == 408) {}
        for (i = 0;; i = 1)
        {
          ((Intent)localObject).putExtra("com.google.android.gms.wallet.EXTRA_ERROR_CODE", paramInt);
          break;
        }
      }
      try
      {
        paramMaskedWallet.send(i);
        return;
      }
      catch (PendingIntent.CanceledException paramMaskedWallet)
      {
        Log.w("WalletClientImpl", "Exception setting pending result", paramMaskedWallet);
      }
    }
    
    public void zza(int paramInt, boolean paramBoolean, Bundle paramBundle)
    {
      paramBundle = (Activity)this.zzbSw.get();
      if (paramBundle == null)
      {
        Log.d("WalletClientImpl", "Ignoring onPreAuthorizationDetermined, Activity has gone");
        return;
      }
      Intent localIntent = new Intent();
      localIntent.putExtra("com.google.android.gm.wallet.EXTRA_IS_USER_PREAUTHORIZED", paramBoolean);
      paramBundle = paramBundle.createPendingResult(this.zzazu, localIntent, NUM);
      if (paramBundle == null)
      {
        Log.w("WalletClientImpl", "Null pending result returned for onPreAuthorizationDetermined");
        return;
      }
      try
      {
        paramBundle.send(-1);
        return;
      }
      catch (PendingIntent.CanceledException paramBundle)
      {
        Log.w("WalletClientImpl", "Exception setting pending result", paramBundle);
      }
    }
    
    public void zza(Status paramStatus, boolean paramBoolean, Bundle paramBundle)
    {
      paramStatus = (Activity)this.zzbSw.get();
      if (paramStatus == null)
      {
        Log.d("WalletClientImpl", "Ignoring onIsReadyToPayDetermined, Activity has gone");
        return;
      }
      paramBundle = new Intent();
      paramBundle.putExtra("com.google.android.gms.wallet.EXTRA_IS_READY_TO_PAY", paramBoolean);
      paramStatus = paramStatus.createPendingResult(this.zzazu, paramBundle, NUM);
      if (paramStatus == null)
      {
        Log.w("WalletClientImpl", "Null pending result returned for onIsReadyToPayDetermined");
        return;
      }
      try
      {
        paramStatus.send(-1);
        return;
      }
      catch (PendingIntent.CanceledException paramStatus)
      {
        Log.w("WalletClientImpl", "Exception setting pending result in onIsReadyToPayDetermined", paramStatus);
      }
    }
    
    public void zzb(int paramInt, boolean paramBoolean, Bundle paramBundle)
    {
      paramBundle = (Activity)this.zzbSw.get();
      if (paramBundle == null)
      {
        Log.d("WalletClientImpl", "Ignoring onIsNewUserDetermined, Activity has gone");
        return;
      }
      Intent localIntent = new Intent();
      localIntent.putExtra("com.google.android.gms.wallet.EXTRA_IS_NEW_USER", paramBoolean);
      paramBundle = paramBundle.createPendingResult(this.zzazu, localIntent, NUM);
      if (paramBundle == null)
      {
        Log.w("WalletClientImpl", "Null pending result returned for onIsNewUserDetermined");
        return;
      }
      try
      {
        paramBundle.send(-1);
        return;
      }
      catch (PendingIntent.CanceledException paramBundle)
      {
        Log.w("WalletClientImpl", "Exception setting pending result", paramBundle);
      }
    }
    
    public void zzm(int paramInt, Bundle paramBundle)
    {
      zzac.zzb(paramBundle, "Bundle should not be null");
      Activity localActivity = (Activity)this.zzbSw.get();
      if (localActivity == null)
      {
        Log.d("WalletClientImpl", "Ignoring onWalletObjectsCreated, Activity has gone");
        return;
      }
      paramBundle = new ConnectionResult(paramInt, (PendingIntent)paramBundle.getParcelable("com.google.android.gms.wallet.EXTRA_PENDING_INTENT"));
      if (paramBundle.hasResolution()) {
        try
        {
          paramBundle.startResolutionForResult(localActivity, this.zzazu);
          return;
        }
        catch (IntentSender.SendIntentException paramBundle)
        {
          Log.w("WalletClientImpl", "Exception starting pending intent", paramBundle);
          return;
        }
      }
      paramBundle = String.valueOf(paramBundle);
      Log.e("WalletClientImpl", String.valueOf(paramBundle).length() + 75 + "Create Wallet Objects confirmation UI will not be shown connection result: " + paramBundle);
      paramBundle = new Intent();
      paramBundle.putExtra("com.google.android.gms.wallet.EXTRA_ERROR_CODE", 413);
      paramBundle = localActivity.createPendingResult(this.zzazu, paramBundle, NUM);
      if (paramBundle == null)
      {
        Log.w("WalletClientImpl", "Null pending result returned for onWalletObjectsCreated");
        return;
      }
      try
      {
        paramBundle.send(1);
        return;
      }
      catch (PendingIntent.CanceledException paramBundle)
      {
        Log.w("WalletClientImpl", "Exception setting pending result", paramBundle);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbky.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */