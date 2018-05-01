package com.google.android.gms.internal.wallet;

import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;

public abstract class zzp
  extends zzb
  implements zzo
{
  public zzp()
  {
    super("com.google.android.gms.wallet.fragment.internal.IWalletFragmentStateListener");
  }
  
  protected final boolean dispatchTransaction(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    if (paramInt1 == 2)
    {
      zza(paramParcel1.readInt(), paramParcel1.readInt(), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      paramParcel2.writeNoException();
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/wallet/zzp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */