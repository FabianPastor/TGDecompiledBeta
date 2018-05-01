package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzeu;
import com.google.android.gms.internal.zzew;

public final class zzap
  extends zzeu
  implements zzan
{
  zzap(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.common.internal.IAccountAccessor");
  }
  
  public final Account getAccount()
    throws RemoteException
  {
    Parcel localParcel = zza(2, zzbe());
    Account localAccount = (Account)zzew.zza(localParcel, Account.CREATOR);
    localParcel.recycle();
    return localAccount;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */