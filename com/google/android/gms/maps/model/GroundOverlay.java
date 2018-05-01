package com.google.android.gms.maps.model;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.maps.model.internal.zzg;

public final class GroundOverlay
{
  private final zzg zzbno;
  
  public GroundOverlay(zzg paramzzg)
  {
    this.zzbno = ((zzg)zzbo.zzu(paramzzg));
  }
  
  public final boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof GroundOverlay)) {
      return false;
    }
    try
    {
      boolean bool = this.zzbno.zzb(((GroundOverlay)paramObject).zzbno);
      return bool;
    }
    catch (RemoteException paramObject)
    {
      throw new RuntimeRemoteException((RemoteException)paramObject);
    }
  }
  
  public final float getBearing()
  {
    try
    {
      float f = this.zzbno.getBearing();
      return f;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final LatLngBounds getBounds()
  {
    try
    {
      LatLngBounds localLatLngBounds = this.zzbno.getBounds();
      return localLatLngBounds;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final float getHeight()
  {
    try
    {
      float f = this.zzbno.getHeight();
      return f;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final String getId()
  {
    try
    {
      String str = this.zzbno.getId();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final LatLng getPosition()
  {
    try
    {
      LatLng localLatLng = this.zzbno.getPosition();
      return localLatLng;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final Object getTag()
  {
    try
    {
      Object localObject = zzn.zzE(this.zzbno.getTag());
      return localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final float getTransparency()
  {
    try
    {
      float f = this.zzbno.getTransparency();
      return f;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final float getWidth()
  {
    try
    {
      float f = this.zzbno.getWidth();
      return f;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final float getZIndex()
  {
    try
    {
      float f = this.zzbno.getZIndex();
      return f;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final int hashCode()
  {
    try
    {
      int i = this.zzbno.hashCodeRemote();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final boolean isClickable()
  {
    try
    {
      boolean bool = this.zzbno.isClickable();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final boolean isVisible()
  {
    try
    {
      boolean bool = this.zzbno.isVisible();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void remove()
  {
    try
    {
      this.zzbno.remove();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setBearing(float paramFloat)
  {
    try
    {
      this.zzbno.setBearing(paramFloat);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setClickable(boolean paramBoolean)
  {
    try
    {
      this.zzbno.setClickable(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setDimensions(float paramFloat)
  {
    try
    {
      this.zzbno.setDimensions(paramFloat);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setDimensions(float paramFloat1, float paramFloat2)
  {
    try
    {
      this.zzbno.zzf(paramFloat1, paramFloat2);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setImage(@NonNull BitmapDescriptor paramBitmapDescriptor)
  {
    zzbo.zzb(paramBitmapDescriptor, "imageDescriptor must not be null");
    try
    {
      paramBitmapDescriptor = paramBitmapDescriptor.zzwe();
      this.zzbno.zzJ(paramBitmapDescriptor);
      return;
    }
    catch (RemoteException paramBitmapDescriptor)
    {
      throw new RuntimeRemoteException(paramBitmapDescriptor);
    }
  }
  
  public final void setPosition(LatLng paramLatLng)
  {
    try
    {
      this.zzbno.setPosition(paramLatLng);
      return;
    }
    catch (RemoteException paramLatLng)
    {
      throw new RuntimeRemoteException(paramLatLng);
    }
  }
  
  public final void setPositionFromBounds(LatLngBounds paramLatLngBounds)
  {
    try
    {
      this.zzbno.setPositionFromBounds(paramLatLngBounds);
      return;
    }
    catch (RemoteException paramLatLngBounds)
    {
      throw new RuntimeRemoteException(paramLatLngBounds);
    }
  }
  
  public final void setTag(Object paramObject)
  {
    try
    {
      this.zzbno.setTag(zzn.zzw(paramObject));
      return;
    }
    catch (RemoteException paramObject)
    {
      throw new RuntimeRemoteException((RemoteException)paramObject);
    }
  }
  
  public final void setTransparency(float paramFloat)
  {
    try
    {
      this.zzbno.setTransparency(paramFloat);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setVisible(boolean paramBoolean)
  {
    try
    {
      this.zzbno.setVisible(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setZIndex(float paramFloat)
  {
    try
    {
      this.zzbno.setZIndex(paramFloat);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/GroundOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */