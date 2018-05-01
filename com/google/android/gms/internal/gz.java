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

public final class gz
  extends zzp<gh>
{
  private static gz zzbQF;
  
  protected gz()
  {
    super("com.google.android.gms.wallet.dynamite.WalletDynamiteCreatorImpl");
  }
  
  public static ga zza(Activity paramActivity, zzk paramzzk, WalletFragmentOptions paramWalletFragmentOptions, gd paramgd)
    throws GooglePlayServicesNotAvailableException
  {
    int i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(paramActivity);
    if (i != 0) {
      throw new GooglePlayServicesNotAvailableException(i);
    }
    try
    {
      if (zzbQF == null) {
        zzbQF = new gz();
      }
      paramActivity = ((gh)zzbQF.zzaS(paramActivity)).zza(zzn.zzw(paramActivity), paramzzk, paramWalletFragmentOptions, paramgd);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/gz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */