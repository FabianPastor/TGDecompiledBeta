package com.google.android.gms.signin.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.stable.zza;
import com.google.android.gms.internal.stable.zzb;
import com.google.android.gms.internal.stable.zzc;

public abstract interface ISignInCallbacks
  extends IInterface
{
  public abstract void onAuthAccountComplete(ConnectionResult paramConnectionResult, AuthAccountResult paramAuthAccountResult)
    throws RemoteException;
  
  public abstract void onGetCurrentAccountComplete(Status paramStatus, GoogleSignInAccount paramGoogleSignInAccount)
    throws RemoteException;
  
  public abstract void onRecordConsentComplete(Status paramStatus)
    throws RemoteException;
  
  public abstract void onSaveAccountToSessionStoreComplete(Status paramStatus)
    throws RemoteException;
  
  public abstract void onSignInComplete(SignInResponse paramSignInResponse)
    throws RemoteException;
  
  public static abstract class Stub
    extends zzb
    implements ISignInCallbacks
  {
    public Stub()
    {
      super();
    }
    
    public static ISignInCallbacks asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        paramIBinder = null;
      }
      for (;;)
      {
        return paramIBinder;
        IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.signin.internal.ISignInCallbacks");
        if ((localIInterface instanceof ISignInCallbacks)) {
          paramIBinder = (ISignInCallbacks)localIInterface;
        } else {
          paramIBinder = new Proxy(paramIBinder);
        }
      }
    }
    
    protected boolean dispatchTransaction(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      boolean bool;
      switch (paramInt1)
      {
      case 5: 
      default: 
        bool = false;
        return bool;
      case 3: 
        onAuthAccountComplete((ConnectionResult)zzc.zza(paramParcel1, ConnectionResult.CREATOR), (AuthAccountResult)zzc.zza(paramParcel1, AuthAccountResult.CREATOR));
      }
      for (;;)
      {
        paramParcel2.writeNoException();
        bool = true;
        break;
        onSaveAccountToSessionStoreComplete((Status)zzc.zza(paramParcel1, Status.CREATOR));
        continue;
        onRecordConsentComplete((Status)zzc.zza(paramParcel1, Status.CREATOR));
        continue;
        onGetCurrentAccountComplete((Status)zzc.zza(paramParcel1, Status.CREATOR), (GoogleSignInAccount)zzc.zza(paramParcel1, GoogleSignInAccount.CREATOR));
        continue;
        onSignInComplete((SignInResponse)zzc.zza(paramParcel1, SignInResponse.CREATOR));
      }
    }
    
    public static class Proxy
      extends zza
      implements ISignInCallbacks
    {
      Proxy(IBinder paramIBinder)
      {
        super("com.google.android.gms.signin.internal.ISignInCallbacks");
      }
      
      public void onAuthAccountComplete(ConnectionResult paramConnectionResult, AuthAccountResult paramAuthAccountResult)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramConnectionResult);
        zzc.zza(localParcel, paramAuthAccountResult);
        transactAndReadExceptionReturnVoid(3, localParcel);
      }
      
      public void onGetCurrentAccountComplete(Status paramStatus, GoogleSignInAccount paramGoogleSignInAccount)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramStatus);
        zzc.zza(localParcel, paramGoogleSignInAccount);
        transactAndReadExceptionReturnVoid(7, localParcel);
      }
      
      public void onRecordConsentComplete(Status paramStatus)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramStatus);
        transactAndReadExceptionReturnVoid(6, localParcel);
      }
      
      public void onSaveAccountToSessionStoreComplete(Status paramStatus)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramStatus);
        transactAndReadExceptionReturnVoid(4, localParcel);
      }
      
      public void onSignInComplete(SignInResponse paramSignInResponse)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramSignInResponse);
        transactAndReadExceptionReturnVoid(8, localParcel);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/ISignInCallbacks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */