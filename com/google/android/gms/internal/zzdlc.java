package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;

public final class zzdlc
  extends zzeu
  implements zzdlb
{
  zzdlc(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.wallet.internal.IOwService");
  }
  
  public final void zza(FullWalletRequest paramFullWalletRequest, Bundle paramBundle, zzdlf paramzzdlf)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramFullWalletRequest);
    zzew.zza(localParcel, paramBundle);
    zzew.zza(localParcel, paramzzdlf);
    zzc(2, localParcel);
  }
  
  public final void zza(IsReadyToPayRequest paramIsReadyToPayRequest, Bundle paramBundle, zzdlf paramzzdlf)
    throws RemoteException
  {
    Parcel localParcel = zzbe();
    zzew.zza(localParcel, paramIsReadyToPayRequest);
    zzew.zza(localParcel, paramBundle);
    zzew.zza(localParcel, paramzzdlf);
    zzc(14, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzdlc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */