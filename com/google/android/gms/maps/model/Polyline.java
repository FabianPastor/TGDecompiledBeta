package com.google.android.gms.maps.model;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.maps.model.internal.IPolylineDelegate;
import java.util.List;

public final class Polyline
{
  private final IPolylineDelegate zzbnR;
  
  public Polyline(IPolylineDelegate paramIPolylineDelegate)
  {
    this.zzbnR = ((IPolylineDelegate)zzbo.zzu(paramIPolylineDelegate));
  }
  
  public final boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Polyline)) {
      return false;
    }
    try
    {
      boolean bool = this.zzbnR.equalsRemote(((Polyline)paramObject).zzbnR);
      return bool;
    }
    catch (RemoteException paramObject)
    {
      throw new RuntimeRemoteException((RemoteException)paramObject);
    }
  }
  
  public final int getColor()
  {
    try
    {
      int i = this.zzbnR.getColor();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  @NonNull
  public final Cap getEndCap()
  {
    try
    {
      Cap localCap = this.zzbnR.getEndCap().zzwk();
      return localCap;
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
      String str = this.zzbnR.getId();
      return str;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final int getJointType()
  {
    try
    {
      int i = this.zzbnR.getJointType();
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  @Nullable
  public final List<PatternItem> getPattern()
  {
    try
    {
      List localList = PatternItem.zzF(this.zzbnR.getPattern());
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final List<LatLng> getPoints()
  {
    try
    {
      List localList = this.zzbnR.getPoints();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  @NonNull
  public final Cap getStartCap()
  {
    try
    {
      Cap localCap = this.zzbnR.getStartCap().zzwk();
      return localCap;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  @Nullable
  public final Object getTag()
  {
    try
    {
      Object localObject = zzn.zzE(this.zzbnR.getTag());
      return localObject;
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
      float f = this.zzbnR.getWidth();
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
      float f = this.zzbnR.getZIndex();
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
      int i = this.zzbnR.hashCodeRemote();
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
      boolean bool = this.zzbnR.isClickable();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final boolean isGeodesic()
  {
    try
    {
      boolean bool = this.zzbnR.isGeodesic();
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
      boolean bool = this.zzbnR.isVisible();
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
      this.zzbnR.remove();
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
      this.zzbnR.setClickable(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setColor(int paramInt)
  {
    try
    {
      this.zzbnR.setColor(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setEndCap(@NonNull Cap paramCap)
  {
    zzbo.zzb(paramCap, "endCap must not be null");
    try
    {
      this.zzbnR.setEndCap(paramCap);
      return;
    }
    catch (RemoteException paramCap)
    {
      throw new RuntimeRemoteException(paramCap);
    }
  }
  
  public final void setGeodesic(boolean paramBoolean)
  {
    try
    {
      this.zzbnR.setGeodesic(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setJointType(int paramInt)
  {
    try
    {
      this.zzbnR.setJointType(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setPattern(@Nullable List<PatternItem> paramList)
  {
    try
    {
      this.zzbnR.setPattern(paramList);
      return;
    }
    catch (RemoteException paramList)
    {
      throw new RuntimeRemoteException(paramList);
    }
  }
  
  public final void setPoints(List<LatLng> paramList)
  {
    try
    {
      this.zzbnR.setPoints(paramList);
      return;
    }
    catch (RemoteException paramList)
    {
      throw new RuntimeRemoteException(paramList);
    }
  }
  
  public final void setStartCap(@NonNull Cap paramCap)
  {
    zzbo.zzb(paramCap, "startCap must not be null");
    try
    {
      this.zzbnR.setStartCap(paramCap);
      return;
    }
    catch (RemoteException paramCap)
    {
      throw new RuntimeRemoteException(paramCap);
    }
  }
  
  public final void setTag(@Nullable Object paramObject)
  {
    try
    {
      this.zzbnR.setTag(zzn.zzw(paramObject));
      return;
    }
    catch (RemoteException paramObject)
    {
      throw new RuntimeRemoteException((RemoteException)paramObject);
    }
  }
  
  public final void setVisible(boolean paramBoolean)
  {
    try
    {
      this.zzbnR.setVisible(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setWidth(float paramFloat)
  {
    try
    {
      this.zzbnR.setWidth(paramFloat);
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
      this.zzbnR.setZIndex(paramFloat);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/Polyline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */