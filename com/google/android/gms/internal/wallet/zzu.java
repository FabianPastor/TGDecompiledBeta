package com.google.android.gms.internal.wallet;

import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.zzar;

public abstract interface zzu
  extends IInterface
{
  public abstract void zza(int paramInt, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zza(int paramInt, FullWallet paramFullWallet, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zza(int paramInt, MaskedWallet paramMaskedWallet, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zza(int paramInt, boolean paramBoolean, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zza(Status paramStatus, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zza(Status paramStatus, zzf paramzzf, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zza(Status paramStatus, zzh paramzzh, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zza(Status paramStatus, zzj paramzzj, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zza(Status paramStatus, PaymentData paramPaymentData, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zza(Status paramStatus, zzar paramzzar, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zza(Status paramStatus, boolean paramBoolean, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zzb(int paramInt, boolean paramBoolean, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zzb(Status paramStatus, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zzc(Status paramStatus, Bundle paramBundle)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/wallet/zzu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */