package com.google.android.gms.internal;

import android.app.Activity;
import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.dynamic.zzc;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zzf;
import com.google.android.gms.dynamic.zzf.zza;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public class zzbkz
  extends zzf<zzbkv>
{
  private static zzbkz zzbSx;
  
  protected zzbkz()
  {
    super("com.google.android.gms.wallet.dynamite.WalletDynamiteCreatorImpl");
  }
  
  private static zzbkz zzUa()
  {
    if (zzbSx == null) {
      zzbSx = new zzbkz();
    }
    return zzbSx;
  }
  
  public static zzbks zza(Activity paramActivity, zzc paramzzc, WalletFragmentOptions paramWalletFragmentOptions, zzbkt paramzzbkt)
    throws GooglePlayServicesNotAvailableException
  {
    int i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(paramActivity);
    if (i != 0) {
      throw new GooglePlayServicesNotAvailableException(i);
    }
    try
    {
      paramActivity = ((zzbkv)zzUa().zzbl(paramActivity)).zza(zzd.zzA(paramActivity), paramzzc, paramWalletFragmentOptions, paramzzbkt);
      return paramActivity;
    }
    catch (RemoteException paramActivity)
    {
      throw new RuntimeException(paramActivity);
    }
    catch (zzf.zza paramActivity)
    {
      throw new RuntimeException(paramActivity);
    }
  }
  
  protected zzbkv zzfz(IBinder paramIBinder)
  {
    return zzbkv.zza.zzfw(paramIBinder);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbkz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */