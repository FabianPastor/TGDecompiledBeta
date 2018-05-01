package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzk;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public final class zzdle
  extends zzeu
  implements zzdld
{
  zzdle(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.wallet.internal.IWalletDynamiteCreator");
  }
  
  public final zzdkw zza(IObjectWrapper paramIObjectWrapper, zzk paramzzk, WalletFragmentOptions paramWalletFragmentOptions, zzdkz paramzzdkz)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramIObjectWrapper);
    zzew.zza(localParcel, paramzzk);
    zzew.zza(localParcel, paramWalletFragmentOptions);
    zzew.zza(localParcel, paramzzdkz);
    paramIObjectWrapper = zza(1, localParcel);
    paramzzk = zzdkx.zzbq(paramIObjectWrapper.readStrongBinder());
    paramIObjectWrapper.recycle();
    return paramzzk;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzdle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */