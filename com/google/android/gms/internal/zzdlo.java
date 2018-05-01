package com.google.android.gms.internal;

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
import com.google.android.gms.common.api.internal.zzn;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzd;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;

public final class zzdlo
  extends zzab<zzdlb>
{
  private final Context mContext;
  private final int mTheme;
  private final String zzebv;
  private final int zzlea;
  private final boolean zzleb;
  
  public zzdlo(Context paramContext, Looper paramLooper, zzr paramzzr, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramContext, paramLooper, 4, paramzzr, paramConnectionCallbacks, paramOnConnectionFailedListener);
    this.mContext = paramContext;
    this.zzlea = paramInt1;
    this.zzebv = paramzzr.getAccountName();
    this.mTheme = paramInt2;
    this.zzleb = paramBoolean;
  }
  
  private final Bundle zzbka()
  {
    int i = this.zzlea;
    String str1 = this.mContext.getPackageName();
    String str2 = this.zzebv;
    int j = this.mTheme;
    boolean bool = this.zzleb;
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
    zzdlp localzzdlp = new zzdlp((Activity)this.mContext, paramInt);
    Bundle localBundle = zzbka();
    try
    {
      ((zzdlb)zzakn()).zza(paramFullWalletRequest, localBundle, localzzdlp);
      return;
    }
    catch (RemoteException paramFullWalletRequest)
    {
      Log.e("WalletClientImpl", "RemoteException getting full wallet", paramFullWalletRequest);
      localzzdlp.zza(8, null, Bundle.EMPTY);
    }
  }
  
  public final void zza(IsReadyToPayRequest paramIsReadyToPayRequest, zzn<BooleanResult> paramzzn)
  {
    paramzzn = new zzdlt(paramzzn);
    Bundle localBundle = zzbka();
    try
    {
      ((zzdlb)zzakn()).zza(paramIsReadyToPayRequest, localBundle, paramzzn);
      return;
    }
    catch (RemoteException paramIsReadyToPayRequest)
    {
      Log.e("WalletClientImpl", "RemoteException during isReadyToPay", paramIsReadyToPayRequest);
      paramzzn.zza(Status.zzfnk, false, Bundle.EMPTY);
    }
  }
  
  public final boolean zzako()
  {
    return true;
  }
  
  protected final String zzhi()
  {
    return "com.google.android.gms.wallet.service.BIND";
  }
  
  protected final String zzhj()
  {
    return "com.google.android.gms.wallet.internal.IOwService";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzdlo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */