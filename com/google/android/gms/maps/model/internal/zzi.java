package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.zza;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public final class zzi
  extends zzed
  implements zzg
{
  zzi(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.model.internal.IGroundOverlayDelegate");
  }
  
  public final float getBearing()
    throws RemoteException
  {
    Parcel localParcel = zza(12, zzZ());
    float f = localParcel.readFloat();
    localParcel.recycle();
    return f;
  }
  
  public final LatLngBounds getBounds()
    throws RemoteException
  {
    Parcel localParcel = zza(10, zzZ());
    LatLngBounds localLatLngBounds = (LatLngBounds)zzef.zza(localParcel, LatLngBounds.CREATOR);
    localParcel.recycle();
    return localLatLngBounds;
  }
  
  public final float getHeight()
    throws RemoteException
  {
    Parcel localParcel = zza(8, zzZ());
    float f = localParcel.readFloat();
    localParcel.recycle();
    return f;
  }
  
  public final String getId()
    throws RemoteException
  {
    Parcel localParcel = zza(2, zzZ());
    String str = localParcel.readString();
    localParcel.recycle();
    return str;
  }
  
  public final LatLng getPosition()
    throws RemoteException
  {
    Parcel localParcel = zza(4, zzZ());
    LatLng localLatLng = (LatLng)zzef.zza(localParcel, LatLng.CREATOR);
    localParcel.recycle();
    return localLatLng;
  }
  
  public final IObjectWrapper getTag()
    throws RemoteException
  {
    Parcel localParcel = zza(25, zzZ());
    IObjectWrapper localIObjectWrapper = IObjectWrapper.zza.zzM(localParcel.readStrongBinder());
    localParcel.recycle();
    return localIObjectWrapper;
  }
  
  public final float getTransparency()
    throws RemoteException
  {
    Parcel localParcel = zza(18, zzZ());
    float f = localParcel.readFloat();
    localParcel.recycle();
    return f;
  }
  
  public final float getWidth()
    throws RemoteException
  {
    Parcel localParcel = zza(7, zzZ());
    float f = localParcel.readFloat();
    localParcel.recycle();
    return f;
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
    Parcel localParcel = zza(20, zzZ());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final boolean isClickable()
    throws RemoteException
  {
    Parcel localParcel = zza(23, zzZ());
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
  
  public final void setBearing(float paramFloat)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeFloat(paramFloat);
    zzb(11, localParcel);
  }
  
  public final void setClickable(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBoolean);
    zzb(22, localParcel);
  }
  
  public final void setDimensions(float paramFloat)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeFloat(paramFloat);
    zzb(5, localParcel);
  }
  
  public final void setPosition(LatLng paramLatLng)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramLatLng);
    zzb(3, localParcel);
  }
  
  public final void setPositionFromBounds(LatLngBounds paramLatLngBounds)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramLatLngBounds);
    zzb(9, localParcel);
  }
  
  public final void setTag(IObjectWrapper paramIObjectWrapper)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramIObjectWrapper);
    zzb(24, localParcel);
  }
  
  public final void setTransparency(float paramFloat)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeFloat(paramFloat);
    zzb(17, localParcel);
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
  
  public final void zzJ(IObjectWrapper paramIObjectWrapper)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramIObjectWrapper);
    zzb(21, localParcel);
  }
  
  public final boolean zzb(zzg paramzzg)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzg);
    paramzzg = zza(19, localParcel);
    boolean bool = zzef.zza(paramzzg);
    paramzzg.recycle();
    return bool;
  }
  
  public final void zzf(float paramFloat1, float paramFloat2)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeFloat(paramFloat1);
    localParcel.writeFloat(paramFloat2);
    zzb(6, localParcel);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */