package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzee;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import java.util.List;

public abstract interface IPolylineDelegate
  extends IInterface
{
  public abstract boolean equalsRemote(IPolylineDelegate paramIPolylineDelegate)
    throws RemoteException;
  
  public abstract int getColor()
    throws RemoteException;
  
  public abstract Cap getEndCap()
    throws RemoteException;
  
  public abstract String getId()
    throws RemoteException;
  
  public abstract int getJointType()
    throws RemoteException;
  
  public abstract List<PatternItem> getPattern()
    throws RemoteException;
  
  public abstract List<LatLng> getPoints()
    throws RemoteException;
  
  public abstract Cap getStartCap()
    throws RemoteException;
  
  public abstract IObjectWrapper getTag()
    throws RemoteException;
  
  public abstract float getWidth()
    throws RemoteException;
  
  public abstract float getZIndex()
    throws RemoteException;
  
  public abstract int hashCodeRemote()
    throws RemoteException;
  
  public abstract boolean isClickable()
    throws RemoteException;
  
  public abstract boolean isGeodesic()
    throws RemoteException;
  
  public abstract boolean isVisible()
    throws RemoteException;
  
  public abstract void remove()
    throws RemoteException;
  
  public abstract void setClickable(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setColor(int paramInt)
    throws RemoteException;
  
  public abstract void setEndCap(Cap paramCap)
    throws RemoteException;
  
  public abstract void setGeodesic(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setJointType(int paramInt)
    throws RemoteException;
  
  public abstract void setPattern(List<PatternItem> paramList)
    throws RemoteException;
  
  public abstract void setPoints(List<LatLng> paramList)
    throws RemoteException;
  
  public abstract void setStartCap(Cap paramCap)
    throws RemoteException;
  
  public abstract void setTag(IObjectWrapper paramIObjectWrapper)
    throws RemoteException;
  
  public abstract void setVisible(boolean paramBoolean)
    throws RemoteException;
  
  public abstract void setWidth(float paramFloat)
    throws RemoteException;
  
  public abstract void setZIndex(float paramFloat)
    throws RemoteException;
  
  public static abstract class zza
    extends zzee
    implements IPolylineDelegate
  {
    public static IPolylineDelegate zzah(IBinder paramIBinder)
    {
      if (paramIBinder == null) {
        return null;
      }
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.maps.model.internal.IPolylineDelegate");
      if ((localIInterface instanceof IPolylineDelegate)) {
        return (IPolylineDelegate)localIInterface;
      }
      return new zzv(paramIBinder);
    }
    
    public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
      throws RemoteException
    {
      if (zza(paramInt1, paramParcel1, paramParcel2, paramInt2)) {
        return true;
      }
      switch (paramInt1)
      {
      default: 
        return false;
      case 1: 
        remove();
        paramParcel2.writeNoException();
      }
      for (;;)
      {
        return true;
        paramParcel1 = getId();
        paramParcel2.writeNoException();
        paramParcel2.writeString(paramParcel1);
        continue;
        setPoints(paramParcel1.createTypedArrayList(LatLng.CREATOR));
        paramParcel2.writeNoException();
        continue;
        paramParcel1 = getPoints();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        continue;
        setWidth(paramParcel1.readFloat());
        paramParcel2.writeNoException();
        continue;
        float f = getWidth();
        paramParcel2.writeNoException();
        paramParcel2.writeFloat(f);
        continue;
        setColor(paramParcel1.readInt());
        paramParcel2.writeNoException();
        continue;
        paramInt1 = getColor();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        continue;
        setZIndex(paramParcel1.readFloat());
        paramParcel2.writeNoException();
        continue;
        f = getZIndex();
        paramParcel2.writeNoException();
        paramParcel2.writeFloat(f);
        continue;
        setVisible(zzef.zza(paramParcel1));
        paramParcel2.writeNoException();
        continue;
        boolean bool = isVisible();
        paramParcel2.writeNoException();
        zzef.zza(paramParcel2, bool);
        continue;
        setGeodesic(zzef.zza(paramParcel1));
        paramParcel2.writeNoException();
        continue;
        bool = isGeodesic();
        paramParcel2.writeNoException();
        zzef.zza(paramParcel2, bool);
        continue;
        paramParcel1 = paramParcel1.readStrongBinder();
        if (paramParcel1 == null) {
          paramParcel1 = null;
        }
        for (;;)
        {
          bool = equalsRemote(paramParcel1);
          paramParcel2.writeNoException();
          zzef.zza(paramParcel2, bool);
          break;
          IInterface localIInterface = paramParcel1.queryLocalInterface("com.google.android.gms.maps.model.internal.IPolylineDelegate");
          if ((localIInterface instanceof IPolylineDelegate)) {
            paramParcel1 = (IPolylineDelegate)localIInterface;
          } else {
            paramParcel1 = new zzv(paramParcel1);
          }
        }
        paramInt1 = hashCodeRemote();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        continue;
        setClickable(zzef.zza(paramParcel1));
        paramParcel2.writeNoException();
        continue;
        bool = isClickable();
        paramParcel2.writeNoException();
        zzef.zza(paramParcel2, bool);
        continue;
        setStartCap((Cap)zzef.zza(paramParcel1, Cap.CREATOR));
        paramParcel2.writeNoException();
        continue;
        paramParcel1 = getStartCap();
        paramParcel2.writeNoException();
        zzef.zzb(paramParcel2, paramParcel1);
        continue;
        setEndCap((Cap)zzef.zza(paramParcel1, Cap.CREATOR));
        paramParcel2.writeNoException();
        continue;
        paramParcel1 = getEndCap();
        paramParcel2.writeNoException();
        zzef.zzb(paramParcel2, paramParcel1);
        continue;
        setJointType(paramParcel1.readInt());
        paramParcel2.writeNoException();
        continue;
        paramInt1 = getJointType();
        paramParcel2.writeNoException();
        paramParcel2.writeInt(paramInt1);
        continue;
        setPattern(paramParcel1.createTypedArrayList(PatternItem.CREATOR));
        paramParcel2.writeNoException();
        continue;
        paramParcel1 = getPattern();
        paramParcel2.writeNoException();
        paramParcel2.writeTypedList(paramParcel1);
        continue;
        setTag(IObjectWrapper.zza.zzM(paramParcel1.readStrongBinder()));
        paramParcel2.writeNoException();
        continue;
        paramParcel1 = getTag();
        paramParcel2.writeNoException();
        zzef.zza(paramParcel2, paramParcel1);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/IPolylineDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */