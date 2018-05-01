package com.google.android.gms.internal.wallet;

import android.app.Activity;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.dynamic.RemoteCreator;
import com.google.android.gms.dynamic.RemoteCreator.RemoteCreatorException;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public final class zzak
  extends RemoteCreator<zzs>
{
  private static zzak zzgl;
  
  protected zzak()
  {
    super("com.google.android.gms.wallet.dynamite.WalletDynamiteCreatorImpl");
  }
  
  public static zzl zza(Activity paramActivity, IFragmentWrapper paramIFragmentWrapper, WalletFragmentOptions paramWalletFragmentOptions, zzo paramzzo)
    throws GooglePlayServicesNotAvailableException
  {
    int i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(paramActivity, 12451000);
    if (i != 0) {
      throw new GooglePlayServicesNotAvailableException(i);
    }
    try
    {
      if (zzgl == null)
      {
        zzak localzzak = new com/google/android/gms/internal/wallet/zzak;
        localzzak.<init>();
        zzgl = localzzak;
      }
      paramActivity = ((zzs)zzgl.getRemoteCreatorInstance(paramActivity)).zza(ObjectWrapper.wrap(paramActivity), paramIFragmentWrapper, paramWalletFragmentOptions, paramzzo);
      return paramActivity;
    }
    catch (RemoteException paramActivity)
    {
      throw new RuntimeException(paramActivity);
    }
    catch (RemoteCreator.RemoteCreatorException paramActivity)
    {
      throw new RuntimeException(paramActivity);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/wallet/zzak.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */