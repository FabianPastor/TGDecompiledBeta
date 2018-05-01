package com.google.android.gms.internal;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;

public final class gc
  extends zzed
  implements ga
{
  gc(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
  }
  
  public final int getState()
    throws RemoteException
  {
    Parcel localParcel = zza(13, zzZ());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final void initialize(WalletFragmentInitParams paramWalletFragmentInitParams)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramWalletFragmentInitParams);
    zzb(10, localParcel);
  }
  
  public final void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeInt(paramInt1);
    localParcel.writeInt(paramInt2);
    zzef.zza(localParcel, paramIntent);
    zzb(9, localParcel);
  }
  
  public final void onCreate(Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBundle);
    zzb(2, localParcel);
  }
  
  public final IObjectWrapper onCreateView(IObjectWrapper paramIObjectWrapper1, IObjectWrapper paramIObjectWrapper2, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramIObjectWrapper1);
    zzef.zza(localParcel, paramIObjectWrapper2);
    zzef.zza(localParcel, paramBundle);
    paramIObjectWrapper1 = zza(3, localParcel);
    paramIObjectWrapper2 = IObjectWrapper.zza.zzM(paramIObjectWrapper1.readStrongBinder());
    paramIObjectWrapper1.recycle();
    return paramIObjectWrapper2;
  }
  
  public final void onPause()
    throws RemoteException
  {
    zzb(6, zzZ());
  }
  
  public final void onResume()
    throws RemoteException
  {
    zzb(5, zzZ());
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBundle);
    localParcel = zza(8, localParcel);
    if (localParcel.readInt() != 0) {
      paramBundle.readFromParcel(localParcel);
    }
    localParcel.recycle();
  }
  
  public final void onStart()
    throws RemoteException
  {
    zzb(4, zzZ());
  }
  
  public final void onStop()
    throws RemoteException
  {
    zzb(7, zzZ());
  }
  
  public final void setEnabled(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBoolean);
    zzb(12, localParcel);
  }
  
  public final void updateMaskedWallet(MaskedWallet paramMaskedWallet)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramMaskedWallet);
    zzb(14, localParcel);
  }
  
  public final void updateMaskedWalletRequest(MaskedWalletRequest paramMaskedWalletRequest)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramMaskedWalletRequest);
    zzb(11, localParcel);
  }
  
  public final void zza(IObjectWrapper paramIObjectWrapper, WalletFragmentOptions paramWalletFragmentOptions, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramIObjectWrapper);
    zzef.zza(localParcel, paramWalletFragmentOptions);
    zzef.zza(localParcel, paramBundle);
    zzb(1, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/gc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */