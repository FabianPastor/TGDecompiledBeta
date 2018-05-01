package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import java.util.ArrayList;
import java.util.List;

public final class zzf
  extends zzed
  implements zzd
{
  zzf(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.model.internal.ICircleDelegate");
  }
  
  public final LatLng getCenter()
    throws RemoteException
  {
    Parcel localParcel = zza(4, zzZ());
    LatLng localLatLng = (LatLng)zzef.zza(localParcel, LatLng.CREATOR);
    localParcel.recycle();
    return localLatLng;
  }
  
  public final int getFillColor()
    throws RemoteException
  {
    Parcel localParcel = zza(12, zzZ());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final String getId()
    throws RemoteException
  {
    Parcel localParcel = zza(2, zzZ());
    String str = localParcel.readString();
    localParcel.recycle();
    return str;
  }
  
  public final double getRadius()
    throws RemoteException
  {
    Parcel localParcel = zza(6, zzZ());
    double d = localParcel.readDouble();
    localParcel.recycle();
    return d;
  }
  
  public final int getStrokeColor()
    throws RemoteException
  {
    Parcel localParcel = zza(10, zzZ());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final List<PatternItem> getStrokePattern()
    throws RemoteException
  {
    Parcel localParcel = zza(22, zzZ());
    ArrayList localArrayList = localParcel.createTypedArrayList(PatternItem.CREATOR);
    localParcel.recycle();
    return localArrayList;
  }
  
  public final float getStrokeWidth()
    throws RemoteException
  {
    Parcel localParcel = zza(8, zzZ());
    float f = localParcel.readFloat();
    localParcel.recycle();
    return f;
  }
  
  public final IObjectWrapper getTag()
    throws RemoteException
  {
    Parcel localParcel = zza(24, zzZ());
    IObjectWrapper localIObjectWrapper = IObjectWrapper.zza.zzM(localParcel.readStrongBinder());
    localParcel.recycle();
    return localIObjectWrapper;
  }
  
  public final float getZIndex()
    throws RemoteException
  {
    Parcel localParcel = zza(14, zzZ());
    float f = localParcel.readFloat();
    localParcel.recycle();
    return f;
  }
  
  public final int hashCodeRemote()
    throws RemoteException
  {
    Parcel localParcel = zza(18, zzZ());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final boolean isClickable()
    throws RemoteException
  {
    Parcel localParcel = zza(20, zzZ());
    boolean bool = zzef.zza(localParcel);
    localParcel.recycle();
    return bool;
  }
  
  public final boolean isVisible()
    throws RemoteException
  {
    Parcel localParcel = zza(16, zzZ());
    boolean bool = zzef.zza(localParcel);
    localParcel.recycle();
    return bool;
  }
  
  public final void remove()
    throws RemoteException
  {
    zzb(1, zzZ());
  }
  
  public final void setCenter(LatLng paramLatLng)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramLatLng);
    zzb(3, localParcel);
  }
  
  public final void setClickable(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBoolean);
    zzb(19, localParcel);
  }
  
  public final void setFillColor(int paramInt)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeInt(paramInt);
    zzb(11, localParcel);
  }
  
  public final void setRadius(double paramDouble)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeDouble(paramDouble);
    zzb(5, localParcel);
  }
  
  public final void setStrokeColor(int paramInt)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeInt(paramInt);
    zzb(9, localParcel);
  }
  
  public final void setStrokePattern(List<PatternItem> paramList)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeTypedList(paramList);
    zzb(21, localParcel);
  }
  
  public final void setStrokeWidth(float paramFloat)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeFloat(paramFloat);
    zzb(7, localParcel);
  }
  
  public final void setTag(IObjectWrapper paramIObjectWrapper)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramIObjectWrapper);
    zzb(23, localParcel);
  }
  
  public final void setVisible(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBoolean);
    zzb(15, localParcel);
  }
  
  public final void setZIndex(float paramFloat)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeFloat(paramFloat);
    zzb(13, localParcel);
  }
  
  public final boolean zzb(zzd paramzzd)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzd);
    paramzzd = zza(17, localParcel);
    boolean bool = zzef.zza(paramzzd);
    paramzzd.recycle();
    return bool;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */