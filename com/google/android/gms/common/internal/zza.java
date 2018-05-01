package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.os.Binder;
import android.os.RemoteException;
import android.util.Log;

public final class zza
  extends zzao
{
  public static Account zza(zzan paramzzan)
  {
    Account localAccount = null;
    long l;
    if (paramzzan != null) {
      l = Binder.clearCallingIdentity();
    }
    try
    {
      localAccount = paramzzan.getAccount();
      return localAccount;
    }
    catch (RemoteException paramzzan)
    {
      Log.w("AccountAccessor", "Remote account accessor probably died");
      return null;
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    throw new NoSuchMethodError();
  }
  
  public final Account getAccount()
  {
    throw new NoSuchMethodError();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */