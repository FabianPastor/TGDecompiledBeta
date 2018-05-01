package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;

public final class gg
  extends zzed
  implements gf
{
  gg(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.wallet.internal.IOwService");
  }
  
  public final void zza(Bundle paramBundle, gj paramgj)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBundle);
    zzef.zza(localParcel, paramgj);
    zzc(5, localParcel);
  }
  
  public final void zza(FullWalletRequest paramFullWalletRequest, Bundle paramBundle, gj paramgj)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramFullWalletRequest);
    zzef.zza(localParcel, paramBundle);
    zzef.zza(localParcel, paramgj);
    zzc(2, localParcel);
  }
  
  public final void zza(IsReadyToPayRequest paramIsReadyToPayRequest, Bundle paramBundle, gj paramgj)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramIsReadyToPayRequest);
    zzef.zza(localParcel, paramBundle);
    zzef.zza(localParcel, paramgj);
    zzc(14, localParcel);
  }
  
  public final void zza(MaskedWalletRequest paramMaskedWalletRequest, Bundle paramBundle, gj paramgj)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramMaskedWalletRequest);
    zzef.zza(localParcel, paramBundle);
    zzef.zza(localParcel, paramgj);
    zzc(1, localParcel);
  }
  
  public final void zza(NotifyTransactionStatusRequest paramNotifyTransactionStatusRequest, Bundle paramBundle)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramNotifyTransactionStatusRequest);
    zzef.zza(localParcel, paramBundle);
    zzc(4, localParcel);
  }
  
  public final void zza(String paramString1, String paramString2, Bundle paramBundle, gj paramgj)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeString(paramString1);
    localParcel.writeString(paramString2);
    zzef.zza(localParcel, paramBundle);
    zzef.zza(localParcel, paramgj);
    zzc(3, localParcel);
  }
  
  public final void zzb(Bundle paramBundle, gj paramgj)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBundle);
    zzef.zza(localParcel, paramgj);
    zzc(11, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/gg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */