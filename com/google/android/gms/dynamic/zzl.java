package com.google.android.gms.dynamic;

import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzev;
import com.google.android.gms.internal.zzew;

public abstract class zzl
  extends zzev
  implements zzk
{
  public zzl()
  {
    attachInterface(this, "com.google.android.gms.dynamic.IFragmentWrapper");
  }
  
  public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
    throws RemoteException
  {
    Object localObject = null;
    IInterface localIInterface = null;
    if (zza(paramInt1, paramParcel1, paramParcel2, paramInt2)) {
      return true;
    }
    switch (paramInt1)
    {
    default: 
      return false;
    case 2: 
      paramParcel1 = zzapx();
      paramParcel2.writeNoException();
      zzew.zza(paramParcel2, paramParcel1);
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    case 13: 
    case 14: 
    case 15: 
    case 16: 
    case 17: 
    case 18: 
    case 19: 
    case 20: 
    case 21: 
    case 22: 
    case 23: 
    case 24: 
    case 25: 
    case 26: 
      for (;;)
      {
        return true;
        paramParcel1 = getArguments();
        paramParcel2.writeNoException();
        zzew.zzb(paramParcel2, paramParcel1);
        continue;
        paramInt1 = getId();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        continue;
        paramParcel1 = zzapy();
        paramParcel2.writeNoException();
        zzew.zza(paramParcel2, paramParcel1);
        continue;
        paramParcel1 = zzapz();
        paramParcel2.writeNoException();
        zzew.zza(paramParcel2, paramParcel1);
        continue;
        boolean bool = getRetainInstance();
        paramParcel2.writeNoException();
        zzew.zza(paramParcel2, bool);
        continue;
        paramParcel1 = getTag();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        continue;
        paramParcel1 = zzaqa();
        paramParcel2.writeNoException();
        zzew.zza(paramParcel2, paramParcel1);
        continue;
        paramInt1 = getTargetRequestCode();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        continue;
        bool = getUserVisibleHint();
        paramParcel2.writeNoException();
        zzew.zza(paramParcel2, bool);
        continue;
        paramParcel1 = getView();
        paramParcel2.writeNoException();
        zzew.zza(paramParcel2, paramParcel1);
        continue;
        bool = isAdded();
        paramParcel2.writeNoException();
        zzew.zza(paramParcel2, bool);
        continue;
        bool = isDetached();
        paramParcel2.writeNoException();
        zzew.zza(paramParcel2, bool);
        continue;
        bool = isHidden();
        paramParcel2.writeNoException();
        zzew.zza(paramParcel2, bool);
        continue;
        bool = isInLayout();
        paramParcel2.writeNoException();
        zzew.zza(paramParcel2, bool);
        continue;
        bool = isRemoving();
        paramParcel2.writeNoException();
        zzew.zza(paramParcel2, bool);
        continue;
        bool = isResumed();
        paramParcel2.writeNoException();
        zzew.zza(paramParcel2, bool);
        continue;
        bool = isVisible();
        paramParcel2.writeNoException();
        zzew.zza(paramParcel2, bool);
        continue;
        paramParcel1 = paramParcel1.readStrongBinder();
        if (paramParcel1 == null) {
          paramParcel1 = localIInterface;
        }
        for (;;)
        {
          zzv(paramParcel1);
          paramParcel2.writeNoException();
          break;
          localIInterface = paramParcel1.queryLocalInterface("com.google.android.gms.dynamic.IObjectWrapper");
          if ((localIInterface instanceof IObjectWrapper)) {
            paramParcel1 = (IObjectWrapper)localIInterface;
          } else {
            paramParcel1 = new zzm(paramParcel1);
          }
        }
        setHasOptionsMenu(zzew.zza(paramParcel1));
        paramParcel2.writeNoException();
        continue;
        setMenuVisibility(zzew.zza(paramParcel1));
        paramParcel2.writeNoException();
        continue;
        setRetainInstance(zzew.zza(paramParcel1));
        paramParcel2.writeNoException();
        continue;
        setUserVisibleHint(zzew.zza(paramParcel1));
        paramParcel2.writeNoException();
        continue;
        startActivity((Intent)zzew.zza(paramParcel1, Intent.CREATOR));
        paramParcel2.writeNoException();
        continue;
        startActivityForResult((Intent)zzew.zza(paramParcel1, Intent.CREATOR), paramParcel1.readInt());
        paramParcel2.writeNoException();
      }
    }
    paramParcel1 = paramParcel1.readStrongBinder();
    if (paramParcel1 == null) {
      paramParcel1 = (Parcel)localObject;
    }
    for (;;)
    {
      zzw(paramParcel1);
      paramParcel2.writeNoException();
      break;
      localIInterface = paramParcel1.queryLocalInterface("com.google.android.gms.dynamic.IObjectWrapper");
      if ((localIInterface instanceof IObjectWrapper)) {
        paramParcel1 = (IObjectWrapper)localIInterface;
      } else {
        paramParcel1 = new zzm(paramParcel1);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */