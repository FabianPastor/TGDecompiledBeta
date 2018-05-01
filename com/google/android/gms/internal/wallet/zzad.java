package com.google.android.gms.internal.wallet;

import android.accounts.Account;
import android.app.Activity;
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
import com.google.android.gms.common.api.internal.BaseImplementation.ResultHolder;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.GmsClient;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;

public final class zzad
  extends GmsClient<zzq>
{
  private final int environment;
  private final int theme;
  private final String zzci;
  private final boolean zzer;
  private final Context zzgh;
  
  public zzad(Context paramContext, Looper paramLooper, ClientSettings paramClientSettings, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramContext, paramLooper, 4, paramClientSettings, paramConnectionCallbacks, paramOnConnectionFailedListener);
    this.zzgh = paramContext;
    this.environment = paramInt1;
    this.zzci = paramClientSettings.getAccountName();
    this.theme = paramInt2;
    this.zzer = paramBoolean;
  }
  
  private final Bundle zzd()
  {
    int i = this.environment;
    String str1 = this.zzgh.getPackageName();
    String str2 = this.zzci;
    int j = this.theme;
    boolean bool = this.zzer;
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
  
  public final int getMinApkVersion()
  {
    return 12451000;
  }
  
  protected final String getServiceDescriptor()
  {
    return "com.google.android.gms.wallet.internal.IOwService";
  }
  
  protected final String getStartServiceAction()
  {
    return "com.google.android.gms.wallet.service.BIND";
  }
  
  public final boolean requiresAccount()
  {
    return true;
  }
  
  public final void zza(FullWalletRequest paramFullWalletRequest, int paramInt)
  {
    zzae localzzae = new zzae((Activity)this.zzgh, paramInt);
    Bundle localBundle = zzd();
    try
    {
      ((zzq)getService()).zza(paramFullWalletRequest, localBundle, localzzae);
      return;
    }
    catch (RemoteException paramFullWalletRequest)
    {
      for (;;)
      {
        Log.e("WalletClientImpl", "RemoteException getting full wallet", paramFullWalletRequest);
        localzzae.zza(8, null, Bundle.EMPTY);
      }
    }
  }
  
  public final void zza(IsReadyToPayRequest paramIsReadyToPayRequest, BaseImplementation.ResultHolder<BooleanResult> paramResultHolder)
  {
    paramResultHolder = new zzai(paramResultHolder);
    Bundle localBundle = zzd();
    try
    {
      ((zzq)getService()).zza(paramIsReadyToPayRequest, localBundle, paramResultHolder);
      return;
    }
    catch (RemoteException paramIsReadyToPayRequest)
    {
      for (;;)
      {
        Log.e("WalletClientImpl", "RemoteException during isReadyToPay", paramIsReadyToPayRequest);
        paramResultHolder.zza(Status.RESULT_INTERNAL_ERROR, false, Bundle.EMPTY);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/wallet/zzad.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */