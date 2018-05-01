package com.google.android.gms.internal.wallet;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public final class zzt
  extends zza
  implements zzs
{
  zzt(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.wallet.internal.IWalletDynamiteCreator");
  }
  
  public final zzl zza(IObjectWrapper paramIObjectWrapper, IFragmentWrapper paramIFragmentWrapper, WalletFragmentOptions paramWalletFragmentOptions, zzo paramzzo)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramIObjectWrapper);
    zzc.zza(localParcel, paramIFragmentWrapper);
    zzc.zza(localParcel, paramWalletFragmentOptions);
    zzc.zza(localParcel, paramzzo);
    paramIObjectWrapper = transactAndReadException(1, localParcel);
    paramIFragmentWrapper = zzm.zza(paramIObjectWrapper.readStrongBinder());
    paramIObjectWrapper.recycle();
    return paramIFragmentWrapper;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/wallet/zzt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */