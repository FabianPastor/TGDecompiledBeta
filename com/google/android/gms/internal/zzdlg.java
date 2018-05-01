package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.zzar;

public abstract class zzdlg
  extends zzev
  implements zzdlf
{
  public zzdlg()
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
      zza(paramParcel1.readInt(), (MaskedWallet)zzew.zza(paramParcel1, MaskedWallet.CREATOR), (Bundle)zzew.zza(paramParcel1, Bundle.CREATOR));
    }
    for (;;)
    {
      return true;
      zza(paramParcel1.readInt(), (FullWallet)zzew.zza(paramParcel1, FullWallet.CREATOR), (Bundle)zzew.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zza(paramParcel1.readInt(), zzew.zza(paramParcel1), (Bundle)zzew.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zzg(paramParcel1.readInt(), (Bundle)zzew.zza(paramParcel1, Bundle.CREATOR));
      continue;
      paramParcel1.readInt();
      zzew.zza(paramParcel1);
      zzew.zza(paramParcel1, Bundle.CREATOR);
      continue;
      zzew.zza(paramParcel1, Status.CREATOR);
      zzew.zza(paramParcel1, zzdkq.CREATOR);
      zzew.zza(paramParcel1, Bundle.CREATOR);
      continue;
      zzew.zza(paramParcel1, Status.CREATOR);
      zzew.zza(paramParcel1, Bundle.CREATOR);
      continue;
      zza((Status)zzew.zza(paramParcel1, Status.CREATOR), zzew.zza(paramParcel1), (Bundle)zzew.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zzew.zza(paramParcel1, Status.CREATOR);
      zzew.zza(paramParcel1, zzdks.CREATOR);
      zzew.zza(paramParcel1, Bundle.CREATOR);
      continue;
      zzew.zza(paramParcel1, Status.CREATOR);
      zzew.zza(paramParcel1, Bundle.CREATOR);
      continue;
      zzew.zza(paramParcel1, Status.CREATOR);
      zzew.zza(paramParcel1, zzar.CREATOR);
      zzew.zza(paramParcel1, Bundle.CREATOR);
      continue;
      zzew.zza(paramParcel1, Status.CREATOR);
      zzew.zza(paramParcel1, Bundle.CREATOR);
      continue;
      zza((Status)zzew.zza(paramParcel1, Status.CREATOR), (PaymentData)zzew.zza(paramParcel1, PaymentData.CREATOR), (Bundle)zzew.zza(paramParcel1, Bundle.CREATOR));
      continue;
      zzew.zza(paramParcel1, Status.CREATOR);
      zzew.zza(paramParcel1, zzdku.CREATOR);
      zzew.zza(paramParcel1, Bundle.CREATOR);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzdlg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */