package com.google.android.gms.internal.wallet;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;

public final class zzr
  extends zza
  implements zzq
{
  zzr(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.wallet.internal.IOwService");
  }
  
  public final void zza(FullWalletRequest paramFullWalletRequest, Bundle paramBundle, zzu paramzzu)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramFullWalletRequest);
    zzc.zza(localParcel, paramBundle);
    zzc.zza(localParcel, paramzzu);
    transactOneway(2, localParcel);
  }
  
  public final void zza(IsReadyToPayRequest paramIsReadyToPayRequest, Bundle paramBundle, zzu paramzzu)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramIsReadyToPayRequest);
    zzc.zza(localParcel, paramBundle);
    zzc.zza(localParcel, paramzzu);
    transactOneway(14, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/wallet/zzr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */