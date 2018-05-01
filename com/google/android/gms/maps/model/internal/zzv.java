package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import java.util.ArrayList;
import java.util.List;

public final class zzv
  extends zzed
  implements IPolylineDelegate
{
  zzv(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.model.internal.IPolylineDelegate");
  }
  
  public final boolean equalsRemote(IPolylineDelegate paramIPolylineDelegate)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramIPolylineDelegate);
    paramIPolylineDelegate = zza(15, localParcel);
    boolean bool = zzef.zza(paramIPolylineDelegate);
    paramIPolylineDelegate.recycle();
    return bool;
  }
  
  public final int getColor()
    throws RemoteException
  {
    Parcel localParcel = zza(8, zzZ());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final Cap getEndCap()
    throws RemoteException
  {
    Parcel localParcel = zza(22, zzZ());
    Cap localCap = (Cap)zzef.zza(localParcel, Cap.CREATOR);
    localParcel.recycle();
    return localCap;
  }
  
  public final String getId()
    throws RemoteException
  {
    Parcel localParcel = zza(2, zzZ());
    String str = localParcel.readString();
    localParcel.recycle();
    return str;
  }
  
  public final int getJointType()
    throws RemoteException
  {
    Parcel localParcel = zza(24, zzZ());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final List<PatternItem> getPattern()
    throws RemoteException
  {
    Parcel localParcel = zza(26, zzZ());
    ArrayList localArrayList = localParcel.createTypedArrayList(PatternItem.CREATOR);
    localParcel.recycle();
    return localArrayList;
  }
  
  public final List<LatLng> getPoints()
    throws RemoteException
  {
    Parcel localParcel = zza(4, zzZ());
    ArrayList localArrayList = localParcel.createTypedArrayList(LatLng.CREATOR);
    localParcel.recycle();
    return localArrayList;
  }
  
  public final Cap getStartCap()
    throws RemoteException
  {
    Parcel localParcel = zza(20, zzZ());
    Cap localCap = (Cap)zzef.zza(localParcel, Cap.CREATOR);
    localParcel.recycle();
    return localCap;
  }
  
  public final IObjectWrapper getTag()
    throws RemoteException
  {
    Parcel localParcel = zza(28, zzZ());
    IObjectWrapper localIObjectWrapper = IObjectWrapper.zza.zzM(localParcel.readStrongBinder());
    localParcel.recycle();
    return localIObjectWrapper;
  }
  
  public final float getWidth()
    throws RemoteException
  {
    Parcel localParcel = zza(6, zzZ());
    float f = localParcel.readFloat();
    localParcel.recycle();
    return f;
  }
  
  public final float getZIndex()
    throws RemoteException
  {
    Parcel localParcel = zza(10, zzZ());
    float f = localParcel.readFloat();
    localParcel.recycle();
    return f;
  }
  
  public final int hashCodeRemote()
    throws RemoteException
  {
    Parcel localParcel = zza(16, zzZ());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final boolean isClickable()
    throws RemoteException
  {
    Parcel localParcel = zza(18, zzZ());
    boolean bool = zzef.zza(localParcel);
    localParcel.recycle();
    return bool;
  }
  
  public final boolean isGeodesic()
    throws RemoteException
  {
    Parcel localParcel = zza(14, zzZ());
    boolean bool = zzef.zza(localParcel);
    localParcel.recycle();
    return bool;
  }
  
  public final boolean isVisible()
    throws RemoteException
  {
    Parcel localParcel = zza(12, zzZ());
    boolean bool = zzef.zza(localParcel);
    localParcel.recycle();
    return bool;
  }
  
  public final void remove()
    throws RemoteException
  {
    zzb(1, zzZ());
  }
  
  public final void setClickable(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBoolean);
    zzb(17, localParcel);
  }
  
  public final void setColor(int paramInt)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeInt(paramInt);
    zzb(7, localParcel);
  }
  
  public final void setEndCap(Cap paramCap)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramCap);
    zzb(21, localParcel);
  }
  
  public final void setGeodesic(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBoolean);
    zzb(13, localParcel);
  }
  
  public final void setJointType(int paramInt)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeInt(paramInt);
    zzb(23, localParcel);
  }
  
  public final void setPattern(List<PatternItem> paramList)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeTypedList(paramList);
    zzb(25, localParcel);
  }
  
  public final void setPoints(List<LatLng> paramList)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeTypedList(paramList);
    zzb(3, localParcel);
  }
  
  public final void setStartCap(Cap paramCap)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramCap);
    zzb(19, localParcel);
  }
  
  public final void setTag(IObjectWrapper paramIObjectWrapper)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramIObjectWrapper);
    zzb(27, localParcel);
  }
  
  public final void setVisible(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBoolean);
    zzb(11, localParcel);
  }
  
  public final void setWidth(float paramFloat)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeFloat(paramFloat);
    zzb(5, localParcel);
  }
  
  public final void setZIndex(float paramFloat)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeFloat(paramFloat);
    zzb(9, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */