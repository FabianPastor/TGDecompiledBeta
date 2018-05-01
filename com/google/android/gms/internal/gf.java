package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;

public abstract interface gf
  extends IInterface
{
  public abstract void zza(Bundle paramBundle, gj paramgj)
    throws RemoteException;
  
  public abstract void zza(FullWalletRequest paramFullWalletRequest, Bundle paramBundle, gj paramgj)
    throws RemoteException;
  
  public abstract void zza(IsReadyToPayRequest paramIsReadyToPayRequest, Bundle paramBundle, gj paramgj)
    throws RemoteException;
  
  public abstract void zza(MaskedWalletRequest paramMaskedWalletRequest, Bundle paramBundle, gj paramgj)
    throws RemoteException;
  
  public abstract void zza(NotifyTransactionStatusRequest paramNotifyTransactionStatusRequest, Bundle paramBundle)
    throws RemoteException;
  
  public abstract void zza(String paramString1, String paramString2, Bundle paramBundle, gj paramgj)
    throws RemoteException;
  
  public abstract void zzb(Bundle paramBundle, gj paramgj)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/gf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */