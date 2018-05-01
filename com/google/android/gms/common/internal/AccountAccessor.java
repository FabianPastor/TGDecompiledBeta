package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.content.Context;
import android.os.Binder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesUtilLight;

public class AccountAccessor
  extends IAccountAccessor.Stub
{
  private Context mContext;
  private int zzqu;
  private Account zzs;
  
  public static Account getAccountBinderSafe(IAccountAccessor paramIAccountAccessor)
  {
    localObject1 = null;
    localObject2 = localObject1;
    if (paramIAccountAccessor != null) {
      l = Binder.clearCallingIdentity();
    }
    try
    {
      localObject2 = paramIAccountAccessor.getAccount();
    }
    catch (RemoteException paramIAccountAccessor)
    {
      for (;;)
      {
        Log.w("AccountAccessor", "Remote account accessor probably died");
        Binder.restoreCallingIdentity(l);
        localObject2 = localObject1;
      }
    }
    finally
    {
      Binder.restoreCallingIdentity(l);
    }
    return (Account)localObject2;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool;
    if (this == paramObject) {
      bool = true;
    }
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof AccountAccessor)) {
        bool = false;
      } else {
        bool = this.zzs.equals(((AccountAccessor)paramObject).zzs);
      }
    }
  }
  
  public Account getAccount()
  {
    int i = Binder.getCallingUid();
    if (i == this.zzqu) {}
    for (Account localAccount = this.zzs;; localAccount = this.zzs)
    {
      return localAccount;
      if (!GooglePlayServicesUtilLight.isGooglePlayServicesUid(this.mContext, i)) {
        break;
      }
      this.zzqu = i;
    }
    throw new SecurityException("Caller is not GooglePlayServices");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/AccountAccessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */