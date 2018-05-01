package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public abstract class gb
  extends zzee
  implements ga
{
  public static ga zzal(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      return null;
    }
    IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
    if ((localIInterface instanceof ga)) {
      return (ga)localIInterface;
    }
    return new gc(paramIBinder);
  }
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    if (zza(paramInt1, paramParcel1, paramParcel2, paramInt2)) {
      return true;
    }
    switch (paramInt1)
    {
    default: 
      return false;
    case 1: 
      zza(IObjectWrapper.zza.zzM(paramParcel1.readStrongBinder()), (WalletFragmentOptions)zzef.zza(paramParcel1, WalletFragmentOptions.CREATOR), (Bundle)zzef.zza(paramParcel1, Bundle.CREATOR));
      paramParcel2.writeNoException();
    }
    for (;;)
    {
      return true;
      onCreate((Bundle)zzef.zza(paramParcel1, Bundle.CREATOR));
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = onCreateView(IObjectWrapper.zza.zzM(paramParcel1.readStrongBinder()), IObjectWrapper.zza.zzM(paramParcel1.readStrongBinder()), (Bundle)zzef.zza(paramParcel1, Bundle.CREATOR));
      paramParcel2.writeNoException();
      zzef.zza(paramParcel2, paramParcel1);
      continue;
      onStart();
      paramParcel2.writeNoException();
      continue;
      onResume();
      paramParcel2.writeNoException();
      continue;
      onPause();
      paramParcel2.writeNoException();
      continue;
      onStop();
      paramParcel2.writeNoException();
      continue;
      paramParcel1 = (Bundle)zzef.zza(paramParcel1, Bundle.CREATOR);
      onSaveInstanceState(paramParcel1);
      paramParcel2.writeNoException();
      zzef.zzb(paramParcel2, paramParcel1);
      continue;
      onActivityResult(paramParcel1.readInt(), paramParcel1.readInt(), (Intent)zzef.zza(paramParcel1, Intent.CREATOR));
      paramParcel2.writeNoException();
      continue;
      initialize((WalletFragmentInitParams)zzef.zza(paramParcel1, WalletFragmentInitParams.CREATOR));
      paramParcel2.writeNoException();
      continue;
      updateMaskedWalletRequest((MaskedWalletRequest)zzef.zza(paramParcel1, MaskedWalletRequest.CREATOR));
      paramParcel2.writeNoException();
      continue;
      setEnabled(zzef.zza(paramParcel1));
      paramParcel2.writeNoException();
      continue;
      paramInt1 = getState();
      paramParcel2.writeNoException();
      paramParcel2.writeInt(paramInt1);
      continue;
      updateMaskedWallet((MaskedWallet)zzef.zza(paramParcel1, MaskedWallet.CREATOR));
      paramParcel2.writeNoException();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/gb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */