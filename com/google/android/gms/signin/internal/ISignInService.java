package com.google.android.gms.signin.internal;

import android.accounts.Account;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.common.internal.AuthAccountRequest;
import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.common.internal.IAccountAccessor.Stub;
import com.google.android.gms.common.internal.IResolveAccountCallbacks;
import com.google.android.gms.common.internal.IResolveAccountCallbacks.Stub;
import com.google.android.gms.common.internal.ResolveAccountRequest;
import com.google.android.gms.internal.stable.zza;
import com.google.android.gms.internal.stable.zzb;
import com.google.android.gms.internal.stable.zzc;

public abstract interface ISignInService
  extends IInterface
{
  public abstract void authAccount(AuthAccountRequest paramAuthAccountRequest, ISignInCallbacks paramISignInCallbacks)
    throws RemoteException;
  
  public abstract void clearAccountFromSessionStore(int paramInt)
    throws RemoteException;
  
  public abstract void getCurrentAccount(ISignInCallbacks paramISignInCallbacks)
    throws RemoteException;
  
  public abstract void onCheckServerAuthorization(CheckServerAuthResult paramCheckServerAuthResult)
    throws RemoteException;
  
  public abstract void onUploadServerAuthCode(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void recordConsent(RecordConsentRequest paramRecordConsentRequest, ISignInCallbacks paramISignInCallbacks)
    throws RemoteException;
  
  public abstract void resolveAccount(ResolveAccountRequest paramResolveAccountRequest, IResolveAccountCallbacks paramIResolveAccountCallbacks)
    throws RemoteException;
  
  public abstract void saveAccountToSessionStore(int paramInt, Account paramAccount, ISignInCallbacks paramISignInCallbacks)
    throws RemoteException;
  
  public abstract void saveDefaultAccountToSharedPref(IAccountAccessor paramIAccountAccessor, int paramInt, boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setGamesHasBeenGreeted(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void signIn(SignInRequest paramSignInRequest, ISignInCallbacks paramISignInCallbacks)
    throws RemoteException;
  
  public static abstract class Stub
    extends zzb
    implements ISignInService
  {
    public static ISignInService asInterface(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        paramIBinder = null;
      }
      for (;;)
      {
        return paramIBinder;
        IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.signin.internal.ISignInService");
        if ((localIInterface instanceof ISignInService)) {
          paramIBinder = (ISignInService)localIInterface;
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
      case 6: 
      default: 
        bool = false;
        return bool;
      case 2: 
        authAccount((AuthAccountRequest)zzc.zza(paramParcel1, AuthAccountRequest.CREATOR), ISignInCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()));
      }
      for (;;)
      {
        paramParcel2.writeNoException();
        bool = true;
        break;
        onCheckServerAuthorization((CheckServerAuthResult)zzc.zza(paramParcel1, CheckServerAuthResult.CREATOR));
        continue;
        onUploadServerAuthCode(zzc.zza(paramParcel1));
        continue;
        resolveAccount((ResolveAccountRequest)zzc.zza(paramParcel1, ResolveAccountRequest.CREATOR), IResolveAccountCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()));
        continue;
        clearAccountFromSessionStore(paramParcel1.readInt());
        continue;
        saveAccountToSessionStore(paramParcel1.readInt(), (Account)zzc.zza(paramParcel1, Account.CREATOR), ISignInCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()));
        continue;
        saveDefaultAccountToSharedPref(IAccountAccessor.Stub.asInterface(paramParcel1.readStrongBinder()), paramParcel1.readInt(), zzc.zza(paramParcel1));
        continue;
        recordConsent((RecordConsentRequest)zzc.zza(paramParcel1, RecordConsentRequest.CREATOR), ISignInCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()));
        continue;
        getCurrentAccount(ISignInCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()));
        continue;
        signIn((SignInRequest)zzc.zza(paramParcel1, SignInRequest.CREATOR), ISignInCallbacks.Stub.asInterface(paramParcel1.readStrongBinder()));
        continue;
        setGamesHasBeenGreeted(zzc.zza(paramParcel1));
      }
    }
    
    public static class Proxy
      extends zza
      implements ISignInService
    {
      Proxy(IBinder paramIBinder)
      {
        super("com.google.android.gms.signin.internal.ISignInService");
      }
      
      public void authAccount(AuthAccountRequest paramAuthAccountRequest, ISignInCallbacks paramISignInCallbacks)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramAuthAccountRequest);
        zzc.zza(localParcel, paramISignInCallbacks);
        transactAndReadExceptionReturnVoid(2, localParcel);
      }
      
      public void clearAccountFromSessionStore(int paramInt)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        localParcel.writeInt(paramInt);
        transactAndReadExceptionReturnVoid(7, localParcel);
      }
      
      public void getCurrentAccount(ISignInCallbacks paramISignInCallbacks)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramISignInCallbacks);
        transactAndReadExceptionReturnVoid(11, localParcel);
      }
      
      public void onCheckServerAuthorization(CheckServerAuthResult paramCheckServerAuthResult)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramCheckServerAuthResult);
        transactAndReadExceptionReturnVoid(3, localParcel);
      }
      
      public void onUploadServerAuthCode(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramBoolean);
        transactAndReadExceptionReturnVoid(4, localParcel);
      }
      
      public void recordConsent(RecordConsentRequest paramRecordConsentRequest, ISignInCallbacks paramISignInCallbacks)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramRecordConsentRequest);
        zzc.zza(localParcel, paramISignInCallbacks);
        transactAndReadExceptionReturnVoid(10, localParcel);
      }
      
      public void resolveAccount(ResolveAccountRequest paramResolveAccountRequest, IResolveAccountCallbacks paramIResolveAccountCallbacks)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramResolveAccountRequest);
        zzc.zza(localParcel, paramIResolveAccountCallbacks);
        transactAndReadExceptionReturnVoid(5, localParcel);
      }
      
      public void saveAccountToSessionStore(int paramInt, Account paramAccount, ISignInCallbacks paramISignInCallbacks)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        localParcel.writeInt(paramInt);
        zzc.zza(localParcel, paramAccount);
        zzc.zza(localParcel, paramISignInCallbacks);
        transactAndReadExceptionReturnVoid(8, localParcel);
      }
      
      public void saveDefaultAccountToSharedPref(IAccountAccessor paramIAccountAccessor, int paramInt, boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramIAccountAccessor);
        localParcel.writeInt(paramInt);
        zzc.zza(localParcel, paramBoolean);
        transactAndReadExceptionReturnVoid(9, localParcel);
      }
      
      public void setGamesHasBeenGreeted(boolean paramBoolean)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramBoolean);
        transactAndReadExceptionReturnVoid(13, localParcel);
      }
      
      public void signIn(SignInRequest paramSignInRequest, ISignInCallbacks paramISignInCallbacks)
        throws RemoteException
      {
        Parcel localParcel = obtainAndWriteInterfaceToken();
        zzc.zza(localParcel, paramSignInRequest);
        zzc.zza(localParcel, paramISignInCallbacks);
        transactAndReadExceptionReturnVoid(12, localParcel);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/signin/internal/ISignInService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */