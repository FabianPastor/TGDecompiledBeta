package com.google.android.gms.internal.wallet;

import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;

public abstract interface zzq
  extends IInterface
{
  public abstract void zza(FullWalletRequest paramFullWalletRequest, Bundle paramBundle, zzu paramzzu)
    throws RemoteException;
  
  public abstract void zza(IsReadyToPayRequest paramIsReadyToPayRequest, Bundle paramBundle, zzu paramzzu)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/wallet/zzq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */