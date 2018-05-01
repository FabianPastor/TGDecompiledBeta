package com.google.android.gms.maps;

import android.location.Location;
import android.os.RemoteException;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.RuntimeRemoteException;

public final class GoogleMap
{
  private final IGoogleMapDelegate zzg;
  private UiSettings zzh;
  
  public GoogleMap(IGoogleMapDelegate paramIGoogleMapDelegate)
  {
    this.zzg = ((IGoogleMapDelegate)Preconditions.checkNotNull(paramIGoogleMapDelegate));
  }
  
  /* Error */
  public final com.google.android.gms.maps.model.Marker addMarker(com.google.android.gms.maps.model.MarkerOptions paramMarkerOptions)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 26	com/google/android/gms/maps/GoogleMap:zzg	Lcom/google/android/gms/maps/internal/IGoogleMapDelegate;
    //   4: aload_1
    //   5: invokeinterface 34 2 0
    //   10: astore_1
    //   11: aload_1
    //   12: ifnull +14 -> 26
    //   15: new 36	com/google/android/gms/maps/model/Marker
    //   18: dup
    //   19: aload_1
    //   20: invokespecial 39	com/google/android/gms/maps/model/Marker:<init>	(Lcom/google/android/gms/internal/maps/zzt;)V
    //   23: astore_1
    //   24: aload_1
    //   25: areturn
    //   26: aconst_null
    //   27: astore_1
    //   28: goto -4 -> 24
    //   31: astore_1
    //   32: new 41	com/google/android/gms/maps/model/RuntimeRemoteException
    //   35: dup
    //   36: aload_1
    //   37: invokespecial 44	com/google/android/gms/maps/model/RuntimeRemoteException:<init>	(Landroid/os/RemoteException;)V
    //   40: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	41	0	this	GoogleMap
    //   0	41	1	paramMarkerOptions	com.google.android.gms.maps.model.MarkerOptions
    // Exception table:
    //   from	to	target	type
    //   0	11	31	android/os/RemoteException
    //   15	24	31	android/os/RemoteException
  }
  
  public final void animateCamera(CameraUpdate paramCameraUpdate)
  {
    try
    {
      this.zzg.animateCamera(paramCameraUpdate.zza());
      return;
    }
    catch (RemoteException paramCameraUpdate)
    {
      throw new RuntimeRemoteException(paramCameraUpdate);
    }
  }
  
  public final CameraPosition getCameraPosition()
  {
    try
    {
      CameraPosition localCameraPosition = this.zzg.getCameraPosition();
      return localCameraPosition;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final float getMaxZoomLevel()
  {
    try
    {
      float f = this.zzg.getMaxZoomLevel();
      return f;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final UiSettings getUiSettings()
  {
    try
    {
      if (this.zzh == null)
      {
        localUiSettings = new com/google/android/gms/maps/UiSettings;
        localUiSettings.<init>(this.zzg.getUiSettings());
        this.zzh = localUiSettings;
      }
      UiSettings localUiSettings = this.zzh;
      return localUiSettings;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void moveCamera(CameraUpdate paramCameraUpdate)
  {
    try
    {
      this.zzg.moveCamera(paramCameraUpdate.zza());
      return;
    }
    catch (RemoteException paramCameraUpdate)
    {
      throw new RuntimeRemoteException(paramCameraUpdate);
    }
  }
  
  public final void setMapType(int paramInt)
  {
    try
    {
      this.zzg.setMapType(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setMyLocationEnabled(boolean paramBoolean)
  {
    try
    {
      this.zzg.setMyLocationEnabled(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  @Deprecated
  public final void setOnMyLocationChangeListener(OnMyLocationChangeListener paramOnMyLocationChangeListener)
  {
    if (paramOnMyLocationChangeListener == null) {}
    for (;;)
    {
      try
      {
        this.zzg.setOnMyLocationChangeListener(null);
        return;
      }
      catch (RemoteException paramOnMyLocationChangeListener)
      {
        IGoogleMapDelegate localIGoogleMapDelegate;
        zzh localzzh;
        throw new RuntimeRemoteException(paramOnMyLocationChangeListener);
      }
      localIGoogleMapDelegate = this.zzg;
      localzzh = new com/google/android/gms/maps/zzh;
      localzzh.<init>(this, paramOnMyLocationChangeListener);
      localIGoogleMapDelegate.setOnMyLocationChangeListener(localzzh);
    }
  }
  
  public final void setPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      this.zzg.setPadding(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  @Deprecated
  public static abstract interface OnMyLocationChangeListener
  {
    public abstract void onMyLocationChange(Location paramLocation);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/GoogleMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */