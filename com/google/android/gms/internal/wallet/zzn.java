package com.google.android.gms.internal.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.Stub;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public final class zzn
  extends zza
  implements zzl
{
  zzn(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
  }
  
  public final void initialize(WalletFragmentInitParams paramWalletFragmentInitParams)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramWalletFragmentInitParams);
    transactAndReadExceptionReturnVoid(10, localParcel);
  }
  
  public final void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    localParcel.writeInt(paramInt1);
    localParcel.writeInt(paramInt2);
    zzc.zza(localParcel, paramIntent);
    transactAndReadExceptionReturnVoid(9, localParcel);
  }
  
  public final void onCreate(Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramBundle);
    transactAndReadExceptionReturnVoid(2, localParcel);
  }
  
  public final IObjectWrapper onCreateView(IObjectWrapper paramIObjectWrapper1, IObjectWrapper paramIObjectWrapper2, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramIObjectWrapper1);
    zzc.zza(localParcel, paramIObjectWrapper2);
    zzc.zza(localParcel, paramBundle);
    paramIObjectWrapper1 = transactAndReadException(3, localParcel);
    paramIObjectWrapper2 = IObjectWrapper.Stub.asInterface(paramIObjectWrapper1.readStrongBinder());
    paramIObjectWrapper1.recycle();
    return paramIObjectWrapper2;
  }
  
  public final void onPause()
    throws RemoteException
  {
    transactAndReadExceptionReturnVoid(6, obtainAndWriteInterfaceToken());
  }
  
  public final void onResume()
    throws RemoteException
  {
    transactAndReadExceptionReturnVoid(5, obtainAndWriteInterfaceToken());
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramBundle);
    localParcel = transactAndReadException(8, localParcel);
    if (localParcel.readInt() != 0) {
      paramBundle.readFromParcel(localParcel);
    }
    localParcel.recycle();
  }
  
  public final void onStart()
    throws RemoteException
  {
    transactAndReadExceptionReturnVoid(4, obtainAndWriteInterfaceToken());
  }
  
  public final void onStop()
    throws RemoteException
  {
    transactAndReadExceptionReturnVoid(7, obtainAndWriteInterfaceToken());
  }
  
  public final void setEnabled(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramBoolean);
    transactAndReadExceptionReturnVoid(12, localParcel);
  }
  
  public final void updateMaskedWallet(MaskedWallet paramMaskedWallet)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramMaskedWallet);
    transactAndReadExceptionReturnVoid(14, localParcel);
  }
  
  public final void updateMaskedWalletRequest(MaskedWalletRequest paramMaskedWalletRequest)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramMaskedWalletRequest);
    transactAndReadExceptionReturnVoid(11, localParcel);
  }
  
  public final void zza(IObjectWrapper paramIObjectWrapper, WalletFragmentOptions paramWalletFragmentOptions, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = obtainAndWriteInterfaceToken();
    zzc.zza(localParcel, paramIObjectWrapper);
    zzc.zza(localParcel, paramWalletFragmentOptions);
    zzc.zza(localParcel, paramBundle);
    transactAndReadExceptionReturnVoid(1, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/wallet/zzn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */