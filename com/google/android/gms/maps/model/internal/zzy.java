package com.google.android.gms.maps.model.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;

public final class zzy
  extends zzed
  implements zzw
{
  zzy(IBinder paramIBinder)
  {
    super(paramIBinder, "com.google.android.gms.maps.model.internal.ITileOverlayDelegate");
  }
  
  public final void clearTileCache()
    throws RemoteException
  {
    zzb(2, zzZ());
  }
  
  public final boolean getFadeIn()
    throws RemoteException
  {
    Parcel localParcel = zza(11, zzZ());
    boolean bool = zzef.zza(localParcel);
    localParcel.recycle();
    return bool;
  }
  
  public final String getId()
    throws RemoteException
  {
    Parcel localParcel = zza(3, zzZ());
    String str = localParcel.readString();
    localParcel.recycle();
    return str;
  }
  
  public final float getTransparency()
    throws RemoteException
  {
    Parcel localParcel = zza(13, zzZ());
    float f = localParcel.readFloat();
    localParcel.recycle();
    return f;
  }
  
  public final float getZIndex()
    throws RemoteException
  {
    Parcel localParcel = zza(5, zzZ());
    float f = localParcel.readFloat();
    localParcel.recycle();
    return f;
  }
  
  public final int hashCodeRemote()
    throws RemoteException
  {
    Parcel localParcel = zza(9, zzZ());
    int i = localParcel.readInt();
    localParcel.recycle();
    return i;
  }
  
  public final boolean isVisible()
    throws RemoteException
  {
    Parcel localParcel = zza(7, zzZ());
    boolean bool = zzef.zza(localParcel);
    localParcel.recycle();
    return bool;
  }
  
  public final void remove()
    throws RemoteException
  {
    zzb(1, zzZ());
  }
  
  public final void setFadeIn(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBoolean);
    zzb(10, localParcel);
  }
  
  public final void setTransparency(float paramFloat)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeFloat(paramFloat);
    zzb(12, localParcel);
  }
  
  public final void setVisible(boolean paramBoolean)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramBoolean);
    zzb(6, localParcel);
  }
  
  public final void setZIndex(float paramFloat)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    localParcel.writeFloat(paramFloat);
    zzb(4, localParcel);
  }
  
  public final boolean zza(zzw paramzzw)
    throws RemoteException
  {
    Parcel localParcel = zzZ();
    zzef.zza(localParcel, paramzzw);
    paramzzw = zza(8, localParcel);
    boolean bool = zzef.zza(paramzzw);
    paramzzw.recycle();
    return bool;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/internal/zzy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */