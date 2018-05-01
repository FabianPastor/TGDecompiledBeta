package com.google.android.gms.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.internal.zzz;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;

public final class gu
  extends zzz<gf>
{
  private final Context mContext;
  private final int mTheme;
  private final String zzakh;
  private final int zzbPT;
  private final boolean zzbPU;
  
  public gu(Context paramContext, Looper paramLooper, zzq paramzzq, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramContext, paramLooper, 4, paramzzq, paramConnectionCallbacks, paramOnConnectionFailedListener);
    this.mContext = paramContext;
    this.zzbPT = paramInt1;
    this.zzakh = paramzzq.getAccountName();
    this.mTheme = paramInt2;
    this.zzbPU = paramBoolean;
  }
  
  private final Bundle zzDT()
  {
    int i = this.zzbPT;
    String str1 = this.mContext.getPackageName();
    String str2 = this.zzakh;
    int j = this.mTheme;
    boolean bool = this.zzbPU;
    Bundle localBundle = new Bundle();
    localBundle.putInt("com.google.android.gms.wallet.EXTRA_ENVIRONMENT", i);
    localBundle.putBoolean("com.google.android.gms.wallet.EXTRA_USING_ANDROID_PAY_BRAND", bool);
    localBundle.putString("androidPackageName", str1);
    if (!TextUtils.isEmpty(str2)) {
      localBundle.putParcelable("com.google.android.gms.wallet.EXTRA_BUYER_ACCOUNT", new Account(str2, "com.google"));
    }
    localBundle.putInt("com.google.android.gms.wallet.EXTRA_THEME", j);
    return localBundle;
  }
  
  public final void zza(FullWalletRequest paramFullWalletRequest, int paramInt)
  {
    gy localgy = new gy(this.mContext, paramInt);
    Bundle localBundle = zzDT();
    try
    {
      ((gf)zzrf()).zza(paramFullWalletRequest, localBundle, localgy);
      return;
    }
    catch (RemoteException paramFullWalletRequest)
    {
      Log.e("WalletClientImpl", "RemoteException getting full wallet", paramFullWalletRequest);
      localgy.zza(8, null, Bundle.EMPTY);
    }
  }
  
  public final void zza(IsReadyToPayRequest paramIsReadyToPayRequest, zzbaz<BooleanResult> paramzzbaz)
  {
    paramzzbaz = new gx(paramzzbaz);
    Bundle localBundle = zzDT();
    try
    {
      ((gf)zzrf()).zza(paramIsReadyToPayRequest, localBundle, paramzzbaz);
      return;
    }
    catch (RemoteException paramIsReadyToPayRequest)
    {
      Log.e("WalletClientImpl", "RemoteException during isReadyToPay", paramIsReadyToPayRequest);
      paramzzbaz.zza(Status.zzaBo, false, Bundle.EMPTY);
    }
  }
  
  public final void zza(MaskedWalletRequest paramMaskedWalletRequest, int paramInt)
  {
    Bundle localBundle = zzDT();
    gy localgy = new gy(this.mContext, paramInt);
    try
    {
      ((gf)zzrf()).zza(paramMaskedWalletRequest, localBundle, localgy);
      return;
    }
    catch (RemoteException paramMaskedWalletRequest)
    {
      Log.e("WalletClientImpl", "RemoteException getting masked wallet", paramMaskedWalletRequest);
      localgy.zza(8, null, Bundle.EMPTY);
    }
  }
  
  public final void zza(NotifyTransactionStatusRequest paramNotifyTransactionStatusRequest)
  {
    Bundle localBundle = zzDT();
    try
    {
      ((gf)zzrf()).zza(paramNotifyTransactionStatusRequest, localBundle);
      return;
    }
    catch (RemoteException paramNotifyTransactionStatusRequest) {}
  }
  
  public final void zzbP(int paramInt)
  {
    Bundle localBundle = zzDT();
    gy localgy = new gy(this.mContext, paramInt);
    try
    {
      ((gf)zzrf()).zza(localBundle, localgy);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("WalletClientImpl", "RemoteException during checkForPreAuthorization", localRemoteException);
      localgy.zza(8, false, Bundle.EMPTY);
    }
  }
  
  public final void zzbQ(int paramInt)
  {
    Bundle localBundle = zzDT();
    gy localgy = new gy(this.mContext, paramInt);
    try
    {
      ((gf)zzrf()).zzb(localBundle, localgy);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("WalletClientImpl", "RemoteException during isNewUser", localRemoteException);
      localgy.zzb(8, false, Bundle.EMPTY);
    }
  }
  
  public final void zzc(String paramString1, String paramString2, int paramInt)
  {
    Bundle localBundle = zzDT();
    gy localgy = new gy(this.mContext, paramInt);
    try
    {
      ((gf)zzrf()).zza(paramString1, paramString2, localBundle, localgy);
      return;
    }
    catch (RemoteException paramString1)
    {
      Log.e("WalletClientImpl", "RemoteException changing masked wallet", paramString1);
      localgy.zza(8, null, Bundle.EMPTY);
    }
  }
  
  protected final String zzdb()
  {
    return "com.google.android.gms.wallet.service.BIND";
  }
  
  protected final String zzdc()
  {
    return "com.google.android.gms.wallet.internal.IOwService";
  }
  
  public final boolean zzrg()
  {
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/gu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */