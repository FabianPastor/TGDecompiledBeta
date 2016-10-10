package com.google.android.gms.maps;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.RemoteException;
import android.support.annotation.RequiresPermission;
import android.view.View;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.ILocationSourceDelegate.zza;
import com.google.android.gms.maps.internal.zzaa.zza;
import com.google.android.gms.maps.internal.zzag.zza;
import com.google.android.gms.maps.internal.zzb.zza;
import com.google.android.gms.maps.internal.zzd.zza;
import com.google.android.gms.maps.internal.zze.zza;
import com.google.android.gms.maps.internal.zzf.zza;
import com.google.android.gms.maps.internal.zzg.zza;
import com.google.android.gms.maps.internal.zzh.zza;
import com.google.android.gms.maps.internal.zzi.zza;
import com.google.android.gms.maps.internal.zzj.zza;
import com.google.android.gms.maps.internal.zzk.zza;
import com.google.android.gms.maps.internal.zzl.zza;
import com.google.android.gms.maps.internal.zzm.zza;
import com.google.android.gms.maps.internal.zzn.zza;
import com.google.android.gms.maps.internal.zzo.zza;
import com.google.android.gms.maps.internal.zzp;
import com.google.android.gms.maps.internal.zzq.zza;
import com.google.android.gms.maps.internal.zzr.zza;
import com.google.android.gms.maps.internal.zzs.zza;
import com.google.android.gms.maps.internal.zzu.zza;
import com.google.android.gms.maps.internal.zzv.zza;
import com.google.android.gms.maps.internal.zzw.zza;
import com.google.android.gms.maps.internal.zzx.zza;
import com.google.android.gms.maps.internal.zzy.zza;
import com.google.android.gms.maps.internal.zzz.zza;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.internal.IPolylineDelegate;
import com.google.android.gms.maps.model.internal.zzb;
import com.google.android.gms.maps.model.internal.zzc;
import com.google.android.gms.maps.model.internal.zzf;
import com.google.android.gms.maps.model.internal.zzg;

public final class GoogleMap
{
  public static final int MAP_TYPE_HYBRID = 4;
  public static final int MAP_TYPE_NONE = 0;
  public static final int MAP_TYPE_NORMAL = 1;
  public static final int MAP_TYPE_SATELLITE = 2;
  public static final int MAP_TYPE_TERRAIN = 3;
  private final IGoogleMapDelegate akI;
  private UiSettings akJ;
  
  protected GoogleMap(IGoogleMapDelegate paramIGoogleMapDelegate)
  {
    this.akI = ((IGoogleMapDelegate)zzac.zzy(paramIGoogleMapDelegate));
  }
  
  public final Circle addCircle(CircleOptions paramCircleOptions)
  {
    try
    {
      paramCircleOptions = new Circle(this.akI.addCircle(paramCircleOptions));
      return paramCircleOptions;
    }
    catch (RemoteException paramCircleOptions)
    {
      throw new RuntimeRemoteException(paramCircleOptions);
    }
  }
  
  public final GroundOverlay addGroundOverlay(GroundOverlayOptions paramGroundOverlayOptions)
  {
    try
    {
      paramGroundOverlayOptions = this.akI.addGroundOverlay(paramGroundOverlayOptions);
      if (paramGroundOverlayOptions != null)
      {
        paramGroundOverlayOptions = new GroundOverlay(paramGroundOverlayOptions);
        return paramGroundOverlayOptions;
      }
      return null;
    }
    catch (RemoteException paramGroundOverlayOptions)
    {
      throw new RuntimeRemoteException(paramGroundOverlayOptions);
    }
  }
  
  public final Marker addMarker(MarkerOptions paramMarkerOptions)
  {
    try
    {
      paramMarkerOptions = this.akI.addMarker(paramMarkerOptions);
      if (paramMarkerOptions != null)
      {
        paramMarkerOptions = new Marker(paramMarkerOptions);
        return paramMarkerOptions;
      }
      return null;
    }
    catch (RemoteException paramMarkerOptions)
    {
      throw new RuntimeRemoteException(paramMarkerOptions);
    }
  }
  
  public final Polygon addPolygon(PolygonOptions paramPolygonOptions)
  {
    try
    {
      paramPolygonOptions = new Polygon(this.akI.addPolygon(paramPolygonOptions));
      return paramPolygonOptions;
    }
    catch (RemoteException paramPolygonOptions)
    {
      throw new RuntimeRemoteException(paramPolygonOptions);
    }
  }
  
  public final Polyline addPolyline(PolylineOptions paramPolylineOptions)
  {
    try
    {
      paramPolylineOptions = new Polyline(this.akI.addPolyline(paramPolylineOptions));
      return paramPolylineOptions;
    }
    catch (RemoteException paramPolylineOptions)
    {
      throw new RuntimeRemoteException(paramPolylineOptions);
    }
  }
  
  public final TileOverlay addTileOverlay(TileOverlayOptions paramTileOverlayOptions)
  {
    try
    {
      paramTileOverlayOptions = this.akI.addTileOverlay(paramTileOverlayOptions);
      if (paramTileOverlayOptions != null)
      {
        paramTileOverlayOptions = new TileOverlay(paramTileOverlayOptions);
        return paramTileOverlayOptions;
      }
      return null;
    }
    catch (RemoteException paramTileOverlayOptions)
    {
      throw new RuntimeRemoteException(paramTileOverlayOptions);
    }
  }
  
  public final void animateCamera(CameraUpdate paramCameraUpdate)
  {
    try
    {
      this.akI.animateCamera(paramCameraUpdate.zzbrh());
      return;
    }
    catch (RemoteException paramCameraUpdate)
    {
      throw new RuntimeRemoteException(paramCameraUpdate);
    }
  }
  
  /* Error */
  public final void animateCamera(CameraUpdate paramCameraUpdate, int paramInt, CancelableCallback paramCancelableCallback)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 159	com/google/android/gms/maps/GoogleMap:akI	Lcom/google/android/gms/maps/internal/IGoogleMapDelegate;
    //   4: astore 4
    //   6: aload_1
    //   7: invokevirtual 235	com/google/android/gms/maps/CameraUpdate:zzbrh	()Lcom/google/android/gms/dynamic/zzd;
    //   10: astore 5
    //   12: aload_3
    //   13: ifnonnull +17 -> 30
    //   16: aconst_null
    //   17: astore_1
    //   18: aload 4
    //   20: aload 5
    //   22: iload_2
    //   23: aload_1
    //   24: invokeinterface 243 4 0
    //   29: return
    //   30: new 128	com/google/android/gms/maps/GoogleMap$zza
    //   33: dup
    //   34: aload_3
    //   35: invokespecial 246	com/google/android/gms/maps/GoogleMap$zza:<init>	(Lcom/google/android/gms/maps/GoogleMap$CancelableCallback;)V
    //   38: astore_1
    //   39: goto -21 -> 18
    //   42: astore_1
    //   43: new 174	com/google/android/gms/maps/model/RuntimeRemoteException
    //   46: dup
    //   47: aload_1
    //   48: invokespecial 177	com/google/android/gms/maps/model/RuntimeRemoteException:<init>	(Landroid/os/RemoteException;)V
    //   51: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	52	0	this	GoogleMap
    //   0	52	1	paramCameraUpdate	CameraUpdate
    //   0	52	2	paramInt	int
    //   0	52	3	paramCancelableCallback	CancelableCallback
    //   4	15	4	localIGoogleMapDelegate	IGoogleMapDelegate
    //   10	11	5	localzzd	com.google.android.gms.dynamic.zzd
    // Exception table:
    //   from	to	target	type
    //   0	12	42	android/os/RemoteException
    //   18	29	42	android/os/RemoteException
    //   30	39	42	android/os/RemoteException
  }
  
  /* Error */
  public final void animateCamera(CameraUpdate paramCameraUpdate, CancelableCallback paramCancelableCallback)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 159	com/google/android/gms/maps/GoogleMap:akI	Lcom/google/android/gms/maps/internal/IGoogleMapDelegate;
    //   4: astore_3
    //   5: aload_1
    //   6: invokevirtual 235	com/google/android/gms/maps/CameraUpdate:zzbrh	()Lcom/google/android/gms/dynamic/zzd;
    //   9: astore 4
    //   11: aload_2
    //   12: ifnonnull +15 -> 27
    //   15: aconst_null
    //   16: astore_1
    //   17: aload_3
    //   18: aload 4
    //   20: aload_1
    //   21: invokeinterface 251 3 0
    //   26: return
    //   27: new 128	com/google/android/gms/maps/GoogleMap$zza
    //   30: dup
    //   31: aload_2
    //   32: invokespecial 246	com/google/android/gms/maps/GoogleMap$zza:<init>	(Lcom/google/android/gms/maps/GoogleMap$CancelableCallback;)V
    //   35: astore_1
    //   36: goto -19 -> 17
    //   39: astore_1
    //   40: new 174	com/google/android/gms/maps/model/RuntimeRemoteException
    //   43: dup
    //   44: aload_1
    //   45: invokespecial 177	com/google/android/gms/maps/model/RuntimeRemoteException:<init>	(Landroid/os/RemoteException;)V
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	GoogleMap
    //   0	49	1	paramCameraUpdate	CameraUpdate
    //   0	49	2	paramCancelableCallback	CancelableCallback
    //   4	14	3	localIGoogleMapDelegate	IGoogleMapDelegate
    //   9	10	4	localzzd	com.google.android.gms.dynamic.zzd
    // Exception table:
    //   from	to	target	type
    //   0	11	39	android/os/RemoteException
    //   17	26	39	android/os/RemoteException
    //   27	36	39	android/os/RemoteException
  }
  
  public final void clear()
  {
    try
    {
      this.akI.clear();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final CameraPosition getCameraPosition()
  {
    try
    {
      CameraPosition localCameraPosition = this.akI.getCameraPosition();
      return localCameraPosition;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public IndoorBuilding getFocusedBuilding()
  {
    try
    {
      Object localObject = this.akI.getFocusedBuilding();
      if (localObject != null)
      {
        localObject = new IndoorBuilding((com.google.android.gms.maps.model.internal.zzd)localObject);
        return (IndoorBuilding)localObject;
      }
      return null;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final int getMapType()
  {
    try
    {
      int i = this.akI.getMapType();
      return i;
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
      float f = this.akI.getMaxZoomLevel();
      return f;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final float getMinZoomLevel()
  {
    try
    {
      float f = this.akI.getMinZoomLevel();
      return f;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  @Deprecated
  public final Location getMyLocation()
  {
    try
    {
      Location localLocation = this.akI.getMyLocation();
      return localLocation;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final Projection getProjection()
  {
    try
    {
      Projection localProjection = new Projection(this.akI.getProjection());
      return localProjection;
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
      if (this.akJ == null) {
        this.akJ = new UiSettings(this.akI.getUiSettings());
      }
      UiSettings localUiSettings = this.akJ;
      return localUiSettings;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final boolean isBuildingsEnabled()
  {
    try
    {
      boolean bool = this.akI.isBuildingsEnabled();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final boolean isIndoorEnabled()
  {
    try
    {
      boolean bool = this.akI.isIndoorEnabled();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final boolean isMyLocationEnabled()
  {
    try
    {
      boolean bool = this.akI.isMyLocationEnabled();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final boolean isTrafficEnabled()
  {
    try
    {
      boolean bool = this.akI.isTrafficEnabled();
      return bool;
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
      this.akI.moveCamera(paramCameraUpdate.zzbrh());
      return;
    }
    catch (RemoteException paramCameraUpdate)
    {
      throw new RuntimeRemoteException(paramCameraUpdate);
    }
  }
  
  public void resetMinMaxZoomPreference()
  {
    try
    {
      this.akI.resetMinMaxZoomPreference();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setBuildingsEnabled(boolean paramBoolean)
  {
    try
    {
      this.akI.setBuildingsEnabled(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setContentDescription(String paramString)
  {
    try
    {
      this.akI.setContentDescription(paramString);
      return;
    }
    catch (RemoteException paramString)
    {
      throw new RuntimeRemoteException(paramString);
    }
  }
  
  public final boolean setIndoorEnabled(boolean paramBoolean)
  {
    try
    {
      paramBoolean = this.akI.setIndoorEnabled(paramBoolean);
      return paramBoolean;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setInfoWindowAdapter(final InfoWindowAdapter paramInfoWindowAdapter)
  {
    if (paramInfoWindowAdapter == null) {}
    try
    {
      this.akI.setInfoWindowAdapter(null);
      return;
    }
    catch (RemoteException paramInfoWindowAdapter)
    {
      throw new RuntimeRemoteException(paramInfoWindowAdapter);
    }
    this.akI.setInfoWindowAdapter(new zzd.zza()
    {
      public com.google.android.gms.dynamic.zzd zzh(zzf paramAnonymouszzf)
      {
        return zze.zzac(paramInfoWindowAdapter.getInfoWindow(new Marker(paramAnonymouszzf)));
      }
      
      public com.google.android.gms.dynamic.zzd zzi(zzf paramAnonymouszzf)
      {
        return zze.zzac(paramInfoWindowAdapter.getInfoContents(new Marker(paramAnonymouszzf)));
      }
    });
  }
  
  public void setLatLngBoundsForCameraTarget(LatLngBounds paramLatLngBounds)
  {
    try
    {
      this.akI.setLatLngBoundsForCameraTarget(paramLatLngBounds);
      return;
    }
    catch (RemoteException paramLatLngBounds)
    {
      throw new RuntimeRemoteException(paramLatLngBounds);
    }
  }
  
  public final void setLocationSource(final LocationSource paramLocationSource)
  {
    if (paramLocationSource == null) {}
    try
    {
      this.akI.setLocationSource(null);
      return;
    }
    catch (RemoteException paramLocationSource)
    {
      throw new RuntimeRemoteException(paramLocationSource);
    }
    this.akI.setLocationSource(new ILocationSourceDelegate.zza()
    {
      public void activate(final zzp paramAnonymouszzp)
      {
        paramLocationSource.activate(new LocationSource.OnLocationChangedListener()
        {
          public void onLocationChanged(Location paramAnonymous2Location)
          {
            try
            {
              paramAnonymouszzp.zze(paramAnonymous2Location);
              return;
            }
            catch (RemoteException paramAnonymous2Location)
            {
              throw new RuntimeRemoteException(paramAnonymous2Location);
            }
          }
        });
      }
      
      public void deactivate()
      {
        paramLocationSource.deactivate();
      }
    });
  }
  
  public boolean setMapStyle(MapStyleOptions paramMapStyleOptions)
  {
    try
    {
      boolean bool = this.akI.setMapStyle(paramMapStyleOptions);
      return bool;
    }
    catch (RemoteException paramMapStyleOptions)
    {
      throw new RuntimeRemoteException(paramMapStyleOptions);
    }
  }
  
  public final void setMapType(int paramInt)
  {
    try
    {
      this.akI.setMapType(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setMaxZoomPreference(float paramFloat)
  {
    try
    {
      this.akI.setMaxZoomPreference(paramFloat);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public void setMinZoomPreference(float paramFloat)
  {
    try
    {
      this.akI.setMinZoomPreference(paramFloat);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  @RequiresPermission(anyOf={"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"})
  public final void setMyLocationEnabled(boolean paramBoolean)
  {
    try
    {
      this.akI.setMyLocationEnabled(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  @Deprecated
  public final void setOnCameraChangeListener(final OnCameraChangeListener paramOnCameraChangeListener)
  {
    if (paramOnCameraChangeListener == null) {}
    try
    {
      this.akI.setOnCameraChangeListener(null);
      return;
    }
    catch (RemoteException paramOnCameraChangeListener)
    {
      throw new RuntimeRemoteException(paramOnCameraChangeListener);
    }
    this.akI.setOnCameraChangeListener(new zze.zza()
    {
      public void onCameraChange(CameraPosition paramAnonymousCameraPosition)
      {
        paramOnCameraChangeListener.onCameraChange(paramAnonymousCameraPosition);
      }
    });
  }
  
  public final void setOnCameraIdleListener(final OnCameraIdleListener paramOnCameraIdleListener)
  {
    if (paramOnCameraIdleListener == null) {}
    try
    {
      this.akI.setOnCameraIdleListener(null);
      return;
    }
    catch (RemoteException paramOnCameraIdleListener)
    {
      throw new RuntimeRemoteException(paramOnCameraIdleListener);
    }
    this.akI.setOnCameraIdleListener(new zzf.zza()
    {
      public void onCameraIdle()
      {
        paramOnCameraIdleListener.onCameraIdle();
      }
    });
  }
  
  public final void setOnCameraMoveCanceledListener(final OnCameraMoveCanceledListener paramOnCameraMoveCanceledListener)
  {
    if (paramOnCameraMoveCanceledListener == null) {}
    try
    {
      this.akI.setOnCameraMoveCanceledListener(null);
      return;
    }
    catch (RemoteException paramOnCameraMoveCanceledListener)
    {
      throw new RuntimeRemoteException(paramOnCameraMoveCanceledListener);
    }
    this.akI.setOnCameraMoveCanceledListener(new zzg.zza()
    {
      public void onCameraMoveCanceled()
      {
        paramOnCameraMoveCanceledListener.onCameraMoveCanceled();
      }
    });
  }
  
  public final void setOnCameraMoveListener(final OnCameraMoveListener paramOnCameraMoveListener)
  {
    if (paramOnCameraMoveListener == null) {}
    try
    {
      this.akI.setOnCameraMoveListener(null);
      return;
    }
    catch (RemoteException paramOnCameraMoveListener)
    {
      throw new RuntimeRemoteException(paramOnCameraMoveListener);
    }
    this.akI.setOnCameraMoveListener(new zzh.zza()
    {
      public void onCameraMove()
      {
        paramOnCameraMoveListener.onCameraMove();
      }
    });
  }
  
  public final void setOnCameraMoveStartedListener(final OnCameraMoveStartedListener paramOnCameraMoveStartedListener)
  {
    if (paramOnCameraMoveStartedListener == null) {}
    try
    {
      this.akI.setOnCameraMoveStartedListener(null);
      return;
    }
    catch (RemoteException paramOnCameraMoveStartedListener)
    {
      throw new RuntimeRemoteException(paramOnCameraMoveStartedListener);
    }
    this.akI.setOnCameraMoveStartedListener(new zzi.zza()
    {
      public void onCameraMoveStarted(int paramAnonymousInt)
      {
        paramOnCameraMoveStartedListener.onCameraMoveStarted(paramAnonymousInt);
      }
    });
  }
  
  public final void setOnCircleClickListener(final OnCircleClickListener paramOnCircleClickListener)
  {
    if (paramOnCircleClickListener == null) {}
    try
    {
      this.akI.setOnCircleClickListener(null);
      return;
    }
    catch (RemoteException paramOnCircleClickListener)
    {
      throw new RuntimeRemoteException(paramOnCircleClickListener);
    }
    this.akI.setOnCircleClickListener(new zzj.zza()
    {
      public void zza(zzb paramAnonymouszzb)
      {
        paramOnCircleClickListener.onCircleClick(new Circle(paramAnonymouszzb));
      }
    });
  }
  
  public final void setOnGroundOverlayClickListener(final OnGroundOverlayClickListener paramOnGroundOverlayClickListener)
  {
    if (paramOnGroundOverlayClickListener == null) {}
    try
    {
      this.akI.setOnGroundOverlayClickListener(null);
      return;
    }
    catch (RemoteException paramOnGroundOverlayClickListener)
    {
      throw new RuntimeRemoteException(paramOnGroundOverlayClickListener);
    }
    this.akI.setOnGroundOverlayClickListener(new zzk.zza()
    {
      public void zza(zzc paramAnonymouszzc)
      {
        paramOnGroundOverlayClickListener.onGroundOverlayClick(new GroundOverlay(paramAnonymouszzc));
      }
    });
  }
  
  public final void setOnIndoorStateChangeListener(final OnIndoorStateChangeListener paramOnIndoorStateChangeListener)
  {
    if (paramOnIndoorStateChangeListener == null) {}
    try
    {
      this.akI.setOnIndoorStateChangeListener(null);
      return;
    }
    catch (RemoteException paramOnIndoorStateChangeListener)
    {
      throw new RuntimeRemoteException(paramOnIndoorStateChangeListener);
    }
    this.akI.setOnIndoorStateChangeListener(new zzl.zza()
    {
      public void onIndoorBuildingFocused()
      {
        paramOnIndoorStateChangeListener.onIndoorBuildingFocused();
      }
      
      public void zza(com.google.android.gms.maps.model.internal.zzd paramAnonymouszzd)
      {
        paramOnIndoorStateChangeListener.onIndoorLevelActivated(new IndoorBuilding(paramAnonymouszzd));
      }
    });
  }
  
  public final void setOnInfoWindowClickListener(final OnInfoWindowClickListener paramOnInfoWindowClickListener)
  {
    if (paramOnInfoWindowClickListener == null) {}
    try
    {
      this.akI.setOnInfoWindowClickListener(null);
      return;
    }
    catch (RemoteException paramOnInfoWindowClickListener)
    {
      throw new RuntimeRemoteException(paramOnInfoWindowClickListener);
    }
    this.akI.setOnInfoWindowClickListener(new zzm.zza()
    {
      public void zze(zzf paramAnonymouszzf)
      {
        paramOnInfoWindowClickListener.onInfoWindowClick(new Marker(paramAnonymouszzf));
      }
    });
  }
  
  public final void setOnInfoWindowCloseListener(final OnInfoWindowCloseListener paramOnInfoWindowCloseListener)
  {
    if (paramOnInfoWindowCloseListener == null) {}
    try
    {
      this.akI.setOnInfoWindowCloseListener(null);
      return;
    }
    catch (RemoteException paramOnInfoWindowCloseListener)
    {
      throw new RuntimeRemoteException(paramOnInfoWindowCloseListener);
    }
    this.akI.setOnInfoWindowCloseListener(new zzn.zza()
    {
      public void zzg(zzf paramAnonymouszzf)
      {
        paramOnInfoWindowCloseListener.onInfoWindowClose(new Marker(paramAnonymouszzf));
      }
    });
  }
  
  public final void setOnInfoWindowLongClickListener(final OnInfoWindowLongClickListener paramOnInfoWindowLongClickListener)
  {
    if (paramOnInfoWindowLongClickListener == null) {}
    try
    {
      this.akI.setOnInfoWindowLongClickListener(null);
      return;
    }
    catch (RemoteException paramOnInfoWindowLongClickListener)
    {
      throw new RuntimeRemoteException(paramOnInfoWindowLongClickListener);
    }
    this.akI.setOnInfoWindowLongClickListener(new zzo.zza()
    {
      public void zzf(zzf paramAnonymouszzf)
      {
        paramOnInfoWindowLongClickListener.onInfoWindowLongClick(new Marker(paramAnonymouszzf));
      }
    });
  }
  
  public final void setOnMapClickListener(final OnMapClickListener paramOnMapClickListener)
  {
    if (paramOnMapClickListener == null) {}
    try
    {
      this.akI.setOnMapClickListener(null);
      return;
    }
    catch (RemoteException paramOnMapClickListener)
    {
      throw new RuntimeRemoteException(paramOnMapClickListener);
    }
    this.akI.setOnMapClickListener(new zzq.zza()
    {
      public void onMapClick(LatLng paramAnonymousLatLng)
      {
        paramOnMapClickListener.onMapClick(paramAnonymousLatLng);
      }
    });
  }
  
  public void setOnMapLoadedCallback(final OnMapLoadedCallback paramOnMapLoadedCallback)
  {
    if (paramOnMapLoadedCallback == null) {}
    try
    {
      this.akI.setOnMapLoadedCallback(null);
      return;
    }
    catch (RemoteException paramOnMapLoadedCallback)
    {
      throw new RuntimeRemoteException(paramOnMapLoadedCallback);
    }
    this.akI.setOnMapLoadedCallback(new zzr.zza()
    {
      public void onMapLoaded()
        throws RemoteException
      {
        paramOnMapLoadedCallback.onMapLoaded();
      }
    });
  }
  
  public final void setOnMapLongClickListener(final OnMapLongClickListener paramOnMapLongClickListener)
  {
    if (paramOnMapLongClickListener == null) {}
    try
    {
      this.akI.setOnMapLongClickListener(null);
      return;
    }
    catch (RemoteException paramOnMapLongClickListener)
    {
      throw new RuntimeRemoteException(paramOnMapLongClickListener);
    }
    this.akI.setOnMapLongClickListener(new zzs.zza()
    {
      public void onMapLongClick(LatLng paramAnonymousLatLng)
      {
        paramOnMapLongClickListener.onMapLongClick(paramAnonymousLatLng);
      }
    });
  }
  
  public final void setOnMarkerClickListener(final OnMarkerClickListener paramOnMarkerClickListener)
  {
    if (paramOnMarkerClickListener == null) {}
    try
    {
      this.akI.setOnMarkerClickListener(null);
      return;
    }
    catch (RemoteException paramOnMarkerClickListener)
    {
      throw new RuntimeRemoteException(paramOnMarkerClickListener);
    }
    this.akI.setOnMarkerClickListener(new zzu.zza()
    {
      public boolean zza(zzf paramAnonymouszzf)
      {
        return paramOnMarkerClickListener.onMarkerClick(new Marker(paramAnonymouszzf));
      }
    });
  }
  
  public final void setOnMarkerDragListener(final OnMarkerDragListener paramOnMarkerDragListener)
  {
    if (paramOnMarkerDragListener == null) {}
    try
    {
      this.akI.setOnMarkerDragListener(null);
      return;
    }
    catch (RemoteException paramOnMarkerDragListener)
    {
      throw new RuntimeRemoteException(paramOnMarkerDragListener);
    }
    this.akI.setOnMarkerDragListener(new zzv.zza()
    {
      public void zzb(zzf paramAnonymouszzf)
      {
        paramOnMarkerDragListener.onMarkerDragStart(new Marker(paramAnonymouszzf));
      }
      
      public void zzc(zzf paramAnonymouszzf)
      {
        paramOnMarkerDragListener.onMarkerDragEnd(new Marker(paramAnonymouszzf));
      }
      
      public void zzd(zzf paramAnonymouszzf)
      {
        paramOnMarkerDragListener.onMarkerDrag(new Marker(paramAnonymouszzf));
      }
    });
  }
  
  public final void setOnMyLocationButtonClickListener(final OnMyLocationButtonClickListener paramOnMyLocationButtonClickListener)
  {
    if (paramOnMyLocationButtonClickListener == null) {}
    try
    {
      this.akI.setOnMyLocationButtonClickListener(null);
      return;
    }
    catch (RemoteException paramOnMyLocationButtonClickListener)
    {
      throw new RuntimeRemoteException(paramOnMyLocationButtonClickListener);
    }
    this.akI.setOnMyLocationButtonClickListener(new zzw.zza()
    {
      public boolean onMyLocationButtonClick()
        throws RemoteException
      {
        return paramOnMyLocationButtonClickListener.onMyLocationButtonClick();
      }
    });
  }
  
  @Deprecated
  public final void setOnMyLocationChangeListener(final OnMyLocationChangeListener paramOnMyLocationChangeListener)
  {
    if (paramOnMyLocationChangeListener == null) {}
    try
    {
      this.akI.setOnMyLocationChangeListener(null);
      return;
    }
    catch (RemoteException paramOnMyLocationChangeListener)
    {
      throw new RuntimeRemoteException(paramOnMyLocationChangeListener);
    }
    this.akI.setOnMyLocationChangeListener(new zzx.zza()
    {
      public void zzaf(com.google.android.gms.dynamic.zzd paramAnonymouszzd)
      {
        paramOnMyLocationChangeListener.onMyLocationChange((Location)zze.zzae(paramAnonymouszzd));
      }
    });
  }
  
  public final void setOnPoiClickListener(final OnPoiClickListener paramOnPoiClickListener)
  {
    if (paramOnPoiClickListener == null) {}
    try
    {
      this.akI.setOnPoiClickListener(null);
      return;
    }
    catch (RemoteException paramOnPoiClickListener)
    {
      throw new RuntimeRemoteException(paramOnPoiClickListener);
    }
    this.akI.setOnPoiClickListener(new zzy.zza()
    {
      public void zza(PointOfInterest paramAnonymousPointOfInterest)
        throws RemoteException
      {
        paramOnPoiClickListener.onPoiClick(paramAnonymousPointOfInterest);
      }
    });
  }
  
  public final void setOnPolygonClickListener(final OnPolygonClickListener paramOnPolygonClickListener)
  {
    if (paramOnPolygonClickListener == null) {}
    try
    {
      this.akI.setOnPolygonClickListener(null);
      return;
    }
    catch (RemoteException paramOnPolygonClickListener)
    {
      throw new RuntimeRemoteException(paramOnPolygonClickListener);
    }
    this.akI.setOnPolygonClickListener(new zzz.zza()
    {
      public void zza(zzg paramAnonymouszzg)
      {
        paramOnPolygonClickListener.onPolygonClick(new Polygon(paramAnonymouszzg));
      }
    });
  }
  
  public final void setOnPolylineClickListener(final OnPolylineClickListener paramOnPolylineClickListener)
  {
    if (paramOnPolylineClickListener == null) {}
    try
    {
      this.akI.setOnPolylineClickListener(null);
      return;
    }
    catch (RemoteException paramOnPolylineClickListener)
    {
      throw new RuntimeRemoteException(paramOnPolylineClickListener);
    }
    this.akI.setOnPolylineClickListener(new zzaa.zza()
    {
      public void zza(IPolylineDelegate paramAnonymousIPolylineDelegate)
      {
        paramOnPolylineClickListener.onPolylineClick(new Polyline(paramAnonymousIPolylineDelegate));
      }
    });
  }
  
  public final void setPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      this.akI.setPadding(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void setTrafficEnabled(boolean paramBoolean)
  {
    try
    {
      this.akI.setTrafficEnabled(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public final void snapshot(SnapshotReadyCallback paramSnapshotReadyCallback)
  {
    snapshot(paramSnapshotReadyCallback, null);
  }
  
  public final void snapshot(final SnapshotReadyCallback paramSnapshotReadyCallback, Bitmap paramBitmap)
  {
    if (paramBitmap != null) {}
    for (paramBitmap = zze.zzac(paramBitmap);; paramBitmap = null)
    {
      paramBitmap = (zze)paramBitmap;
      try
      {
        this.akI.snapshot(new zzag.zza()
        {
          public void onSnapshotReady(Bitmap paramAnonymousBitmap)
            throws RemoteException
          {
            paramSnapshotReadyCallback.onSnapshotReady(paramAnonymousBitmap);
          }
          
          public void zzag(com.google.android.gms.dynamic.zzd paramAnonymouszzd)
            throws RemoteException
          {
            paramSnapshotReadyCallback.onSnapshotReady((Bitmap)zze.zzae(paramAnonymouszzd));
          }
        }, paramBitmap);
        return;
      }
      catch (RemoteException paramSnapshotReadyCallback)
      {
        throw new RuntimeRemoteException(paramSnapshotReadyCallback);
      }
    }
  }
  
  public final void stopAnimation()
  {
    try
    {
      this.akI.stopAnimation();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeRemoteException(localRemoteException);
    }
  }
  
  public static abstract interface CancelableCallback
  {
    public abstract void onCancel();
    
    public abstract void onFinish();
  }
  
  public static abstract interface InfoWindowAdapter
  {
    public abstract View getInfoContents(Marker paramMarker);
    
    public abstract View getInfoWindow(Marker paramMarker);
  }
  
  @Deprecated
  public static abstract interface OnCameraChangeListener
  {
    public abstract void onCameraChange(CameraPosition paramCameraPosition);
  }
  
  public static abstract interface OnCameraIdleListener
  {
    public abstract void onCameraIdle();
  }
  
  public static abstract interface OnCameraMoveCanceledListener
  {
    public abstract void onCameraMoveCanceled();
  }
  
  public static abstract interface OnCameraMoveListener
  {
    public abstract void onCameraMove();
  }
  
  public static abstract interface OnCameraMoveStartedListener
  {
    public static final int REASON_API_ANIMATION = 2;
    public static final int REASON_DEVELOPER_ANIMATION = 3;
    public static final int REASON_GESTURE = 1;
    
    public abstract void onCameraMoveStarted(int paramInt);
  }
  
  public static abstract interface OnCircleClickListener
  {
    public abstract void onCircleClick(Circle paramCircle);
  }
  
  public static abstract interface OnGroundOverlayClickListener
  {
    public abstract void onGroundOverlayClick(GroundOverlay paramGroundOverlay);
  }
  
  public static abstract interface OnIndoorStateChangeListener
  {
    public abstract void onIndoorBuildingFocused();
    
    public abstract void onIndoorLevelActivated(IndoorBuilding paramIndoorBuilding);
  }
  
  public static abstract interface OnInfoWindowClickListener
  {
    public abstract void onInfoWindowClick(Marker paramMarker);
  }
  
  public static abstract interface OnInfoWindowCloseListener
  {
    public abstract void onInfoWindowClose(Marker paramMarker);
  }
  
  public static abstract interface OnInfoWindowLongClickListener
  {
    public abstract void onInfoWindowLongClick(Marker paramMarker);
  }
  
  public static abstract interface OnMapClickListener
  {
    public abstract void onMapClick(LatLng paramLatLng);
  }
  
  public static abstract interface OnMapLoadedCallback
  {
    public abstract void onMapLoaded();
  }
  
  public static abstract interface OnMapLongClickListener
  {
    public abstract void onMapLongClick(LatLng paramLatLng);
  }
  
  public static abstract interface OnMarkerClickListener
  {
    public abstract boolean onMarkerClick(Marker paramMarker);
  }
  
  public static abstract interface OnMarkerDragListener
  {
    public abstract void onMarkerDrag(Marker paramMarker);
    
    public abstract void onMarkerDragEnd(Marker paramMarker);
    
    public abstract void onMarkerDragStart(Marker paramMarker);
  }
  
  public static abstract interface OnMyLocationButtonClickListener
  {
    public abstract boolean onMyLocationButtonClick();
  }
  
  @Deprecated
  public static abstract interface OnMyLocationChangeListener
  {
    public abstract void onMyLocationChange(Location paramLocation);
  }
  
  public static abstract interface OnPoiClickListener
  {
    public abstract void onPoiClick(PointOfInterest paramPointOfInterest);
  }
  
  public static abstract interface OnPolygonClickListener
  {
    public abstract void onPolygonClick(Polygon paramPolygon);
  }
  
  public static abstract interface OnPolylineClickListener
  {
    public abstract void onPolylineClick(Polyline paramPolyline);
  }
  
  public static abstract interface SnapshotReadyCallback
  {
    public abstract void onSnapshotReady(Bitmap paramBitmap);
  }
  
  private static final class zza
    extends zzb.zza
  {
    private final GoogleMap.CancelableCallback all;
    
    zza(GoogleMap.CancelableCallback paramCancelableCallback)
    {
      this.all = paramCancelableCallback;
    }
    
    public void onCancel()
    {
      this.all.onCancel();
    }
    
    public void onFinish()
    {
      this.all.onFinish();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/GoogleMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */