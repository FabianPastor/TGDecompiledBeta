package com.google.android.gms.internal;

import android.app.Activity;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.dynamic.zzk;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.dynamic.zzp;
import com.google.android.gms.dynamic.zzq;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public final class zzdlv
  extends zzp<zzdld>
{
  private static zzdlv zzlfp;
  
  protected zzdlv()
  {
    super("com.google.android.gms.wallet.dynamite.WalletDynamiteCreatorImpl");
  }
  
  public static zzdkw zza(Activity paramActivity, zzk paramzzk, WalletFragmentOptions paramWalletFragmentOptions, zzdkz paramzzdkz)
    throws GooglePlayServicesNotAvailableException
  {
    int i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(paramActivity);
    if (i != 0) {
      throw new GooglePlayServicesNotAvailableException(i);
    }
    try
    {
      if (zzlfp == null) {
        zzlfp = new zzdlv();
      }
      paramActivity = ((zzdld)zzlfp.zzde(paramActivity)).zza(zzn.zzz(paramActivity), paramzzk, paramWalletFragmentOptions, paramzzdkz);
      return paramActivity;
    }
    catch (RemoteException paramActivity)
    {
      throw new RuntimeException(paramActivity);
    }
    catch (zzq paramActivity)
    {
      throw new RuntimeException(paramActivity);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzdlv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */