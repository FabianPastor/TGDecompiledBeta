package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzk;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public final class gi
  extends zzed
  implements gh
{
  gi(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.wallet.internal.IWalletDynamiteCreator");
  }
  
  public final ga zza(IObjectWrapper paramIObjectWrapper, zzk paramzzk, WalletFragmentOptions paramWalletFragmentOptions, gd paramgd)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramIObjectWrapper);
    zzef.zza(localParcel, paramzzk);
    zzef.zza(localParcel, paramWalletFragmentOptions);
    zzef.zza(localParcel, paramgd);
    paramIObjectWrapper = zza(1, localParcel);
    paramzzk = gb.zzal(paramIObjectWrapper.readStrongBinder());
    paramIObjectWrapper.recycle();
    return paramzzk;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/gi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */