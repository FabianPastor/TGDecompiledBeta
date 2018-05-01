package com.google.android.gms.internal.wallet;

import android.os.IBinder;
import android.os.IInterface;

public abstract class zzm
  extends zzb
  implements zzl
{
  public static zzl zza(IBinder paramIBinder)
  {
    if (paramIBinder == null) {
      paramIBinder = null;
    }
    for (;;)
    {
      return paramIBinder;
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.wallet.fragment.internal.IWalletFragmentDelegate");
      if ((localIInterface instanceof zzl)) {
        paramIBinder = (zzl)localIInterface;
      } else {
        paramIBinder = new zzn(paramIBinder);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/wallet/zzm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */