package com.google.android.gms.internal.wallet;

import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.zzar;

public abstract class zzv
  extends zzb
  implements zzu
{
  public zzv()
  {
    super("com.google.android.gms.wallet.internal.IWalletServiceCallbacks");
  }
  
  protected final boolean dispatchTransaction(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    boolean bool;
    switch (paramInt1)
    {
    case 5: 
    default: 
      bool = false;
      return bool;
    case 1: 
      zza(paramParcel1.readInt(), (MaskedWallet)zzc.zza(paramParcel1, MaskedWallet.CREATOR), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
    }
    for (;;)
    {
      bool = true;
      break;
      zza(paramParcel1.readInt(), (FullWallet)zzc.zza(paramParcel1, FullWallet.CREATOR), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zza(paramParcel1.readInt(), zzc.zza(paramParcel1), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zza(paramParcel1.readInt(), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zzb(paramParcel1.readInt(), zzc.zza(paramParcel1), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zza((Status)zzc.zza(paramParcel1, Status.CREATOR), (zzf)zzc.zza(paramParcel1, zzf.CREATOR), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zza((Status)zzc.zza(paramParcel1, Status.CREATOR), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zza((Status)zzc.zza(paramParcel1, Status.CREATOR), zzc.zza(paramParcel1), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zza((Status)zzc.zza(paramParcel1, Status.CREATOR), (zzh)zzc.zza(paramParcel1, zzh.CREATOR), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zzb((Status)zzc.zza(paramParcel1, Status.CREATOR), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zza((Status)zzc.zza(paramParcel1, Status.CREATOR), (zzar)zzc.zza(paramParcel1, zzar.CREATOR), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zzc((Status)zzc.zza(paramParcel1, Status.CREATOR), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zza((Status)zzc.zza(paramParcel1, Status.CREATOR), (PaymentData)zzc.zza(paramParcel1, PaymentData.CREATOR), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zza((Status)zzc.zza(paramParcel1, Status.CREATOR), (zzj)zzc.zza(paramParcel1, zzj.CREATOR), (Bundle)zzc.zza(paramParcel1, Bundle.CREATOR));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/wallet/zzv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */