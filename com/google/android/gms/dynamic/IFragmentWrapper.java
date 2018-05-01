package com.google.android.gms.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.stable.zzb;
import com.google.android.gms.internal.stable.zzc;

public abstract interface IFragmentWrapper
  extends IInterface
{
  public abstract IObjectWrapper getActivity()
    throws RemoteException;
  
  public abstract Bundle getArguments()
    throws RemoteException;
  
  public abstract int getId()
    throws RemoteException;
  
  public abstract IFragmentWrapper getParentFragment()
    throws RemoteException;
  
  public abstract IObjectWrapper getResources()
    throws RemoteException;
  
  public abstract boolean getRetainInstance()
    throws RemoteException;
  
  public abstract String getTag()
    throws RemoteException;
  
  public abstract IFragmentWrapper getTargetFragment()
    throws RemoteException;
  
  public abstract int getTargetRequestCode()
    throws RemoteException;
  
  public abstract boolean getUserVisibleHint()
    throws RemoteException;
  
  public abstract IObjectWrapper getView()
    throws RemoteException;
  
  public abstract boolean isAdded()
    throws RemoteException;
  
  public abstract boolean isDetached()
    throws RemoteException;
  
  public abstract boolean isHidden()
    throws RemoteException;
  
  public abstract boolean isInLayout()
    throws RemoteException;
  
  public abstract boolean isRemoving()
    throws RemoteException;
  
  public abstract boolean isResumed()
    throws RemoteException;
  
  public abstract boolean isVisible()
    throws RemoteException;
  
  public abstract void registerForContextMenu(IObjectWrapper paramIObjectWrapper)
    throws RemoteException;
  
  public abstract void setHasOptionsMenu(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setMenuVisibility(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setRetainInstance(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setUserVisibleHint(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void startActivity(Intent paramIntent)
    throws RemoteException;
  
  public abstract void startActivityForResult(Intent paramIntent, int paramInt)
    throws RemoteException;
  
  public abstract void unregisterForContextMenu(IObjectWrapper paramIObjectWrapper)
    throws RemoteException;
  
  public static abstract class Stub
    extends zzb
    implements IFragmentWrapper
  {
    public Stub()
    {
      super();
    }
    
    protected boolean dispatchTransaction(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      boolean bool;
      switch (paramInt1)
      {
      default: 
        bool = false;
        return bool;
      case 2: 
        paramParcel1 = getActivity();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, paramParcel1);
      }
      for (;;)
      {
        bool = true;
        break;
        paramParcel1 = getArguments();
        paramParcel2.writeNoException();
        zzc.zzb(paramParcel2, paramParcel1);
        continue;
        paramInt1 = getId();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        continue;
        paramParcel1 = getParentFragment();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, paramParcel1);
        continue;
        paramParcel1 = getResources();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, paramParcel1);
        continue;
        bool = getRetainInstance();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, bool);
        continue;
        paramParcel1 = getTag();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        continue;
        paramParcel1 = getTargetFragment();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, paramParcel1);
        continue;
        paramInt1 = getTargetRequestCode();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        continue;
        bool = getUserVisibleHint();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, bool);
        continue;
        paramParcel1 = getView();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, paramParcel1);
        continue;
        bool = isAdded();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, bool);
        continue;
        bool = isDetached();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, bool);
        continue;
        bool = isHidden();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, bool);
        continue;
        bool = isInLayout();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, bool);
        continue;
        bool = isRemoving();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, bool);
        continue;
        bool = isResumed();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, bool);
        continue;
        bool = isVisible();
        paramParcel2.writeNoException();
        zzc.zza(paramParcel2, bool);
        continue;
        registerForContextMenu(IObjectWrapper.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        continue;
        setHasOptionsMenu(zzc.zza(paramParcel1));
        paramParcel2.writeNoException();
        continue;
        setMenuVisibility(zzc.zza(paramParcel1));
        paramParcel2.writeNoException();
        continue;
        setRetainInstance(zzc.zza(paramParcel1));
        paramParcel2.writeNoException();
        continue;
        setUserVisibleHint(zzc.zza(paramParcel1));
        paramParcel2.writeNoException();
        continue;
        startActivity((Intent)zzc.zza(paramParcel1, Intent.CREATOR));
        paramParcel2.writeNoException();
        continue;
        startActivityForResult((Intent)zzc.zza(paramParcel1, Intent.CREATOR), paramParcel1.readInt());
        paramParcel2.writeNoException();
        continue;
        unregisterForContextMenu(IObjectWrapper.Stub.asInterface(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/IFragmentWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */