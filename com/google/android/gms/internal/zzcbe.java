package com.google.android.gms.internal;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.internal.zzz;
import com.google.android.gms.identity.intents.UserAddressRequest;

public final class zzcbe
  extends zzz<zzcbi>
{
  private Activity mActivity;
  private final int mTheme;
  private final String zzakh;
  private zzcbf zzbgD;
  
  public zzcbe(Activity paramActivity, Looper paramLooper, zzq paramzzq, int paramInt, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    super(paramActivity, paramLooper, 12, paramzzq, paramConnectionCallbacks, paramOnConnectionFailedListener);
    this.zzakh = paramzzq.getAccountName();
    this.mActivity = paramActivity;
    this.mTheme = paramInt;
  }
  
  public final void disconnect()
  {
    super.disconnect();
    if (this.zzbgD != null)
    {
      zzcbf.zza(this.zzbgD, null);
      this.zzbgD = null;
    }
  }
  
  public final void zza(UserAddressRequest paramUserAddressRequest, int paramInt)
  {
    super.zzre();
    this.zzbgD = new zzcbf(paramInt, this.mActivity);
    try
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("com.google.android.gms.identity.intents.EXTRA_CALLING_PACKAGE_NAME", getContext().getPackageName());
      if (!TextUtils.isEmpty(this.zzakh)) {
        localBundle.putParcelable("com.google.android.gms.identity.intents.EXTRA_ACCOUNT", new Account(this.zzakh, "com.google"));
      }
      localBundle.putInt("com.google.android.gms.identity.intents.EXTRA_THEME", this.mTheme);
      ((zzcbi)super.zzrf()).zza(this.zzbgD, paramUserAddressRequest, localBundle);
      return;
    }
    catch (RemoteException paramUserAddressRequest)
    {
      Log.e("AddressClientImpl", "Exception requesting user address", paramUserAddressRequest);
      paramUserAddressRequest = new Bundle();
      paramUserAddressRequest.putInt("com.google.android.gms.identity.intents.EXTRA_ERROR_CODE", 555);
      this.zzbgD.zze(1, paramUserAddressRequest);
    }
  }
  
  protected final String zzdb()
  {
    return "com.google.android.gms.identity.service.BIND";
  }
  
  protected final String zzdc()
  {
    return "com.google.android.gms.identity.intents.internal.IAddressService";
  }
  
  public final boolean zzrg()
  {
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcbe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */