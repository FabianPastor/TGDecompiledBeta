package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.stable.zza;
import com.google.android.gms.internal.stable.zzb;
import com.google.android.gms.internal.stable.zzc;

public abstract interface IResolveAccountCallbacks
  extends IInterface
{
  public abstract void onAccountResolutionComplete(ResolveAccountResponse paramResolveAccountResponse)
    throws RemoteException;
  
  public static abstract class Stub
    extends zzb
    implements IResolveAccountCallbacks
  {
    public static IResolveAccountCallbacks asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        paramIBinder = null;
      }
      for (;;)
      {
        return paramIBinder;
        IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.common.internal.IResolveAccountCallbacks");
        if ((localIInterface instanceof IResolveAccountCallbacks)) {
          paramIBinder = (IResolveAccountCallbacks)localIInterface;
        } else {
          paramIBinder = new Proxy(paramIBinder);
        }
      }
    }
    
    protected boolean dispatchTransaction(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      if (paramInt1 == 2)
      {
        onAccountResolutionComplete((ResolveAccountResponse)zzc.zza(paramParcel1, ResolveAccountResponse.CREATOR));
        paramParcel2.writeNoException();
      }
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public static class Proxy
      extends zza
      implements IResolveAccountCallbacks
    {
      Proxy(IBinder paramIBinder)
      {
        super("com.google.android.gms.common.internal.IResolveAccountCallbacks");
      }
      
      public void onAccountResolutionComplete(ResolveAccountResponse paramResolveAccountResponse)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramResolveAccountResponse);
        transactAndReadExceptionReturnVoid(2, localParcel);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/IResolveAccountCallbacks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */