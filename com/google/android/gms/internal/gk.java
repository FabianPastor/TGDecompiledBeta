package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.zzab;

public abstract class gk
  extends zzee
  implements gj
{
  public gk()
  {
    attachInterface(this, "com.google.android.gms.wallet.internal.IWalletServiceCallbacks");
  }
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    if (zza(paramInt1, paramParcel1, paramParcel2, paramInt2)) {
      return true;
    }
    switch (paramInt1)
    {
    case 5: 
    default: 
      return false;
    case 1: 
      zza(paramParcel1.readInt(), (MaskedWallet)zzef.zza(paramParcel1, MaskedWallet.CREATOR), (Bundle)zzef.zza(paramParcel1, Bundle.CREATOR));
    }
    for (;;)
    {
      return true;
      zza(paramParcel1.readInt(), (FullWallet)zzef.zza(paramParcel1, FullWallet.CREATOR), (Bundle)zzef.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zza(paramParcel1.readInt(), zzef.zza(paramParcel1), (Bundle)zzef.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zzg(paramParcel1.readInt(), (Bundle)zzef.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zzb(paramParcel1.readInt(), zzef.zza(paramParcel1), (Bundle)zzef.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zzef.zza(paramParcel1, Status.CREATOR);
      zzef.zza(paramParcel1, fw.CREATOR);
      zzef.zza(paramParcel1, Bundle.CREATOR);
      continue;
      zzef.zza(paramParcel1, Status.CREATOR);
      zzef.zza(paramParcel1, Bundle.CREATOR);
      continue;
      zza((Status)zzef.zza(paramParcel1, Status.CREATOR), zzef.zza(paramParcel1), (Bundle)zzef.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zzef.zza(paramParcel1, Status.CREATOR);
      zzef.zza(paramParcel1, fy.CREATOR);
      zzef.zza(paramParcel1, Bundle.CREATOR);
      continue;
      zzef.zza(paramParcel1, Status.CREATOR);
      zzef.zza(paramParcel1, Bundle.CREATOR);
      continue;
      zzef.zza(paramParcel1, Status.CREATOR);
      zzef.zza(paramParcel1, zzab.CREATOR);
      zzef.zza(paramParcel1, Bundle.CREATOR);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/gk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */