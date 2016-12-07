package com.google.android.gms.maps;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.RemoteException;
import android.support.annotation.RequiresPermission;
import android.view.View;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.zzp;
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
import com.google.android.gms.maps.model.internal.zzd;
import com.google.android.gms.maps.model.internal.zzf;
import com.google.android.gms.maps.model.internal.zzg;
import com.google.android.gms.maps.model.internal.zzh;

public final class GoogleMap {
    public static final int MAP_TYPE_HYBRID = 4;
    public static final int MAP_TYPE_NONE = 0;
    public static final int MAP_TYPE_NORMAL = 1;
    public static final int MAP_TYPE_SATELLITE = 2;
    public static final int MAP_TYPE_TERRAIN = 3;
    private final IGoogleMapDelegate anP;
    private UiSettings anQ;

    public interface CancelableCallback {
        void onCancel();

        void onFinish();
    }

    public interface InfoWindowAdapter {
        View getInfoContents(Marker marker);

        View getInfoWindow(Marker marker);
    }

    @Deprecated
    public interface OnCameraChangeListener {
        void onCameraChange(CameraPosition cameraPosition);
    }

    public interface OnCameraIdleListener {
        void onCameraIdle();
    }

    public interface OnCameraMoveCanceledListener {
        void onCameraMoveCanceled();
    }

    public interface OnCameraMoveListener {
        void onCameraMove();
    }

    public interface OnCameraMoveStartedListener {
        public static final int REASON_API_ANIMATION = 2;
        public static final int REASON_DEVELOPER_ANIMATION = 3;
        public static final int REASON_GESTURE = 1;

        void onCameraMoveStarted(int i);
    }

    public interface OnCircleClickListener {
        void onCircleClick(Circle circle);
    }

    public interface OnGroundOverlayClickListener {
        void onGroundOverlayClick(GroundOverlay groundOverlay);
    }

    public interface OnIndoorStateChangeListener {
        void onIndoorBuildingFocused();

        void onIndoorLevelActivated(IndoorBuilding indoorBuilding);
    }

    public interface OnInfoWindowClickListener {
        void onInfoWindowClick(Marker marker);
    }

    public interface OnInfoWindowCloseListener {
        void onInfoWindowClose(Marker marker);
    }

    public interface OnInfoWindowLongClickListener {
        void onInfoWindowLongClick(Marker marker);
    }

    public interface OnMapClickListener {
        void onMapClick(LatLng latLng);
    }

    public interface OnMapLoadedCallback {
        void onMapLoaded();
    }

    public interface OnMapLongClickListener {
        void onMapLongClick(LatLng latLng);
    }

    public interface OnMarkerClickListener {
        boolean onMarkerClick(Marker marker);
    }

    public interface OnMarkerDragListener {
        void onMarkerDrag(Marker marker);

        void onMarkerDragEnd(Marker marker);

        void onMarkerDragStart(Marker marker);
    }

    public interface OnMyLocationButtonClickListener {
        boolean onMyLocationButtonClick();
    }

    @Deprecated
    public interface OnMyLocationChangeListener {
        void onMyLocationChange(Location location);
    }

    public interface OnPoiClickListener {
        void onPoiClick(PointOfInterest pointOfInterest);
    }

    public interface OnPolygonClickListener {
        void onPolygonClick(Polygon polygon);
    }

    public interface OnPolylineClickListener {
        void onPolylineClick(Polyline polyline);
    }

    public interface SnapshotReadyCallback {
        void onSnapshotReady(Bitmap bitmap);
    }

    private static final class zza extends com.google.android.gms.maps.internal.zzb.zza {
        private final CancelableCallback aos;

        zza(CancelableCallback cancelableCallback) {
            this.aos = cancelableCallback;
        }

        public void onCancel() {
            this.aos.onCancel();
        }

        public void onFinish() {
            this.aos.onFinish();
        }
    }

    protected GoogleMap(IGoogleMapDelegate iGoogleMapDelegate) {
        this.anP = (IGoogleMapDelegate) zzaa.zzy(iGoogleMapDelegate);
    }

    public final Circle addCircle(CircleOptions circleOptions) {
        try {
            return new Circle(this.anP.addCircle(circleOptions));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final GroundOverlay addGroundOverlay(GroundOverlayOptions groundOverlayOptions) {
        try {
            zzc addGroundOverlay = this.anP.addGroundOverlay(groundOverlayOptions);
            return addGroundOverlay != null ? new GroundOverlay(addGroundOverlay) : null;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final Marker addMarker(MarkerOptions markerOptions) {
        try {
            zzf addMarker = this.anP.addMarker(markerOptions);
            return addMarker != null ? new Marker(addMarker) : null;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final Polygon addPolygon(PolygonOptions polygonOptions) {
        try {
            return new Polygon(this.anP.addPolygon(polygonOptions));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final Polyline addPolyline(PolylineOptions polylineOptions) {
        try {
            return new Polyline(this.anP.addPolyline(polylineOptions));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final TileOverlay addTileOverlay(TileOverlayOptions tileOverlayOptions) {
        try {
            zzh addTileOverlay = this.anP.addTileOverlay(tileOverlayOptions);
            return addTileOverlay != null ? new TileOverlay(addTileOverlay) : null;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void animateCamera(CameraUpdate cameraUpdate) {
        try {
            this.anP.animateCamera(cameraUpdate.zzbsc());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void animateCamera(CameraUpdate cameraUpdate, int i, CancelableCallback cancelableCallback) {
        try {
            this.anP.animateCameraWithDurationAndCallback(cameraUpdate.zzbsc(), i, cancelableCallback == null ? null : new zza(cancelableCallback));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void animateCamera(CameraUpdate cameraUpdate, CancelableCallback cancelableCallback) {
        try {
            this.anP.animateCameraWithCallback(cameraUpdate.zzbsc(), cancelableCallback == null ? null : new zza(cancelableCallback));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void clear() {
        try {
            this.anP.clear();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final CameraPosition getCameraPosition() {
        try {
            return this.anP.getCameraPosition();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public IndoorBuilding getFocusedBuilding() {
        try {
            zzd focusedBuilding = this.anP.getFocusedBuilding();
            return focusedBuilding != null ? new IndoorBuilding(focusedBuilding) : null;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final int getMapType() {
        try {
            return this.anP.getMapType();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final float getMaxZoomLevel() {
        try {
            return this.anP.getMaxZoomLevel();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final float getMinZoomLevel() {
        try {
            return this.anP.getMinZoomLevel();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    @Deprecated
    public final Location getMyLocation() {
        try {
            return this.anP.getMyLocation();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final Projection getProjection() {
        try {
            return new Projection(this.anP.getProjection());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final UiSettings getUiSettings() {
        try {
            if (this.anQ == null) {
                this.anQ = new UiSettings(this.anP.getUiSettings());
            }
            return this.anQ;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean isBuildingsEnabled() {
        try {
            return this.anP.isBuildingsEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean isIndoorEnabled() {
        try {
            return this.anP.isIndoorEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean isMyLocationEnabled() {
        try {
            return this.anP.isMyLocationEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean isTrafficEnabled() {
        try {
            return this.anP.isTrafficEnabled();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void moveCamera(CameraUpdate cameraUpdate) {
        try {
            this.anP.moveCamera(cameraUpdate.zzbsc());
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void resetMinMaxZoomPreference() {
        try {
            this.anP.resetMinMaxZoomPreference();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setBuildingsEnabled(boolean z) {
        try {
            this.anP.setBuildingsEnabled(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setContentDescription(String str) {
        try {
            this.anP.setContentDescription(str);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final boolean setIndoorEnabled(boolean z) {
        try {
            return this.anP.setIndoorEnabled(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setInfoWindowAdapter(final InfoWindowAdapter infoWindowAdapter) {
        if (infoWindowAdapter == null) {
            try {
                this.anP.setInfoWindowAdapter(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setInfoWindowAdapter(new com.google.android.gms.maps.internal.zzd.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public com.google.android.gms.dynamic.zzd zzh(zzf com_google_android_gms_maps_model_internal_zzf) {
                return zze.zzac(infoWindowAdapter.getInfoWindow(new Marker(com_google_android_gms_maps_model_internal_zzf)));
            }

            public com.google.android.gms.dynamic.zzd zzi(zzf com_google_android_gms_maps_model_internal_zzf) {
                return zze.zzac(infoWindowAdapter.getInfoContents(new Marker(com_google_android_gms_maps_model_internal_zzf)));
            }
        });
    }

    public void setLatLngBoundsForCameraTarget(LatLngBounds latLngBounds) {
        try {
            this.anP.setLatLngBoundsForCameraTarget(latLngBounds);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setLocationSource(final LocationSource locationSource) {
        if (locationSource == null) {
            try {
                this.anP.setLocationSource(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setLocationSource(new com.google.android.gms.maps.internal.ILocationSourceDelegate.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void activate(final zzp com_google_android_gms_maps_internal_zzp) {
                locationSource.activate(new OnLocationChangedListener(this) {
                    final /* synthetic */ AnonymousClass12 aof;

                    public void onLocationChanged(Location location) {
                        try {
                            com_google_android_gms_maps_internal_zzp.zze(location);
                        } catch (RemoteException e) {
                            throw new RuntimeRemoteException(e);
                        }
                    }
                });
            }

            public void deactivate() {
                locationSource.deactivate();
            }
        });
    }

    public boolean setMapStyle(MapStyleOptions mapStyleOptions) {
        try {
            return this.anP.setMapStyle(mapStyleOptions);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setMapType(int i) {
        try {
            this.anP.setMapType(i);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setMaxZoomPreference(float f) {
        try {
            this.anP.setMaxZoomPreference(f);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public void setMinZoomPreference(float f) {
        try {
            this.anP.setMinZoomPreference(f);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    @RequiresPermission(anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"})
    public final void setMyLocationEnabled(boolean z) {
        try {
            this.anP.setMyLocationEnabled(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    @Deprecated
    public final void setOnCameraChangeListener(final OnCameraChangeListener onCameraChangeListener) {
        if (onCameraChangeListener == null) {
            try {
                this.anP.setOnCameraChangeListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnCameraChangeListener(new com.google.android.gms.maps.internal.zze.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void onCameraChange(CameraPosition cameraPosition) {
                onCameraChangeListener.onCameraChange(cameraPosition);
            }
        });
    }

    public final void setOnCameraIdleListener(final OnCameraIdleListener onCameraIdleListener) {
        if (onCameraIdleListener == null) {
            try {
                this.anP.setOnCameraIdleListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnCameraIdleListener(new com.google.android.gms.maps.internal.zzf.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void onCameraIdle() {
                onCameraIdleListener.onCameraIdle();
            }
        });
    }

    public final void setOnCameraMoveCanceledListener(final OnCameraMoveCanceledListener onCameraMoveCanceledListener) {
        if (onCameraMoveCanceledListener == null) {
            try {
                this.anP.setOnCameraMoveCanceledListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnCameraMoveCanceledListener(new com.google.android.gms.maps.internal.zzg.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void onCameraMoveCanceled() {
                onCameraMoveCanceledListener.onCameraMoveCanceled();
            }
        });
    }

    public final void setOnCameraMoveListener(final OnCameraMoveListener onCameraMoveListener) {
        if (onCameraMoveListener == null) {
            try {
                this.anP.setOnCameraMoveListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnCameraMoveListener(new com.google.android.gms.maps.internal.zzh.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void onCameraMove() {
                onCameraMoveListener.onCameraMove();
            }
        });
    }

    public final void setOnCameraMoveStartedListener(final OnCameraMoveStartedListener onCameraMoveStartedListener) {
        if (onCameraMoveStartedListener == null) {
            try {
                this.anP.setOnCameraMoveStartedListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnCameraMoveStartedListener(new com.google.android.gms.maps.internal.zzi.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void onCameraMoveStarted(int i) {
                onCameraMoveStartedListener.onCameraMoveStarted(i);
            }
        });
    }

    public final void setOnCircleClickListener(final OnCircleClickListener onCircleClickListener) {
        if (onCircleClickListener == null) {
            try {
                this.anP.setOnCircleClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnCircleClickListener(new com.google.android.gms.maps.internal.zzj.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void zza(zzb com_google_android_gms_maps_model_internal_zzb) {
                onCircleClickListener.onCircleClick(new Circle(com_google_android_gms_maps_model_internal_zzb));
            }
        });
    }

    public final void setOnGroundOverlayClickListener(final OnGroundOverlayClickListener onGroundOverlayClickListener) {
        if (onGroundOverlayClickListener == null) {
            try {
                this.anP.setOnGroundOverlayClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnGroundOverlayClickListener(new com.google.android.gms.maps.internal.zzk.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void zza(zzc com_google_android_gms_maps_model_internal_zzc) {
                onGroundOverlayClickListener.onGroundOverlayClick(new GroundOverlay(com_google_android_gms_maps_model_internal_zzc));
            }
        });
    }

    public final void setOnIndoorStateChangeListener(final OnIndoorStateChangeListener onIndoorStateChangeListener) {
        if (onIndoorStateChangeListener == null) {
            try {
                this.anP.setOnIndoorStateChangeListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnIndoorStateChangeListener(new com.google.android.gms.maps.internal.zzl.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void onIndoorBuildingFocused() {
                onIndoorStateChangeListener.onIndoorBuildingFocused();
            }

            public void zza(zzd com_google_android_gms_maps_model_internal_zzd) {
                onIndoorStateChangeListener.onIndoorLevelActivated(new IndoorBuilding(com_google_android_gms_maps_model_internal_zzd));
            }
        });
    }

    public final void setOnInfoWindowClickListener(final OnInfoWindowClickListener onInfoWindowClickListener) {
        if (onInfoWindowClickListener == null) {
            try {
                this.anP.setOnInfoWindowClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnInfoWindowClickListener(new com.google.android.gms.maps.internal.zzm.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void zze(zzf com_google_android_gms_maps_model_internal_zzf) {
                onInfoWindowClickListener.onInfoWindowClick(new Marker(com_google_android_gms_maps_model_internal_zzf));
            }
        });
    }

    public final void setOnInfoWindowCloseListener(final OnInfoWindowCloseListener onInfoWindowCloseListener) {
        if (onInfoWindowCloseListener == null) {
            try {
                this.anP.setOnInfoWindowCloseListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnInfoWindowCloseListener(new com.google.android.gms.maps.internal.zzn.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void zzg(zzf com_google_android_gms_maps_model_internal_zzf) {
                onInfoWindowCloseListener.onInfoWindowClose(new Marker(com_google_android_gms_maps_model_internal_zzf));
            }
        });
    }

    public final void setOnInfoWindowLongClickListener(final OnInfoWindowLongClickListener onInfoWindowLongClickListener) {
        if (onInfoWindowLongClickListener == null) {
            try {
                this.anP.setOnInfoWindowLongClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnInfoWindowLongClickListener(new com.google.android.gms.maps.internal.zzo.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void zzf(zzf com_google_android_gms_maps_model_internal_zzf) {
                onInfoWindowLongClickListener.onInfoWindowLongClick(new Marker(com_google_android_gms_maps_model_internal_zzf));
            }
        });
    }

    public final void setOnMapClickListener(final OnMapClickListener onMapClickListener) {
        if (onMapClickListener == null) {
            try {
                this.anP.setOnMapClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnMapClickListener(new com.google.android.gms.maps.internal.zzq.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void onMapClick(LatLng latLng) {
                onMapClickListener.onMapClick(latLng);
            }
        });
    }

    public void setOnMapLoadedCallback(final OnMapLoadedCallback onMapLoadedCallback) {
        if (onMapLoadedCallback == null) {
            try {
                this.anP.setOnMapLoadedCallback(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnMapLoadedCallback(new com.google.android.gms.maps.internal.zzr.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void onMapLoaded() throws RemoteException {
                onMapLoadedCallback.onMapLoaded();
            }
        });
    }

    public final void setOnMapLongClickListener(final OnMapLongClickListener onMapLongClickListener) {
        if (onMapLongClickListener == null) {
            try {
                this.anP.setOnMapLongClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnMapLongClickListener(new com.google.android.gms.maps.internal.zzs.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void onMapLongClick(LatLng latLng) {
                onMapLongClickListener.onMapLongClick(latLng);
            }
        });
    }

    public final void setOnMarkerClickListener(final OnMarkerClickListener onMarkerClickListener) {
        if (onMarkerClickListener == null) {
            try {
                this.anP.setOnMarkerClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnMarkerClickListener(new com.google.android.gms.maps.internal.zzu.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public boolean zza(zzf com_google_android_gms_maps_model_internal_zzf) {
                return onMarkerClickListener.onMarkerClick(new Marker(com_google_android_gms_maps_model_internal_zzf));
            }
        });
    }

    public final void setOnMarkerDragListener(final OnMarkerDragListener onMarkerDragListener) {
        if (onMarkerDragListener == null) {
            try {
                this.anP.setOnMarkerDragListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnMarkerDragListener(new com.google.android.gms.maps.internal.zzv.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void zzb(zzf com_google_android_gms_maps_model_internal_zzf) {
                onMarkerDragListener.onMarkerDragStart(new Marker(com_google_android_gms_maps_model_internal_zzf));
            }

            public void zzc(zzf com_google_android_gms_maps_model_internal_zzf) {
                onMarkerDragListener.onMarkerDragEnd(new Marker(com_google_android_gms_maps_model_internal_zzf));
            }

            public void zzd(zzf com_google_android_gms_maps_model_internal_zzf) {
                onMarkerDragListener.onMarkerDrag(new Marker(com_google_android_gms_maps_model_internal_zzf));
            }
        });
    }

    public final void setOnMyLocationButtonClickListener(final OnMyLocationButtonClickListener onMyLocationButtonClickListener) {
        if (onMyLocationButtonClickListener == null) {
            try {
                this.anP.setOnMyLocationButtonClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnMyLocationButtonClickListener(new com.google.android.gms.maps.internal.zzw.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public boolean onMyLocationButtonClick() throws RemoteException {
                return onMyLocationButtonClickListener.onMyLocationButtonClick();
            }
        });
    }

    @Deprecated
    public final void setOnMyLocationChangeListener(final OnMyLocationChangeListener onMyLocationChangeListener) {
        if (onMyLocationChangeListener == null) {
            try {
                this.anP.setOnMyLocationChangeListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnMyLocationChangeListener(new com.google.android.gms.maps.internal.zzx.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void zzaf(com.google.android.gms.dynamic.zzd com_google_android_gms_dynamic_zzd) {
                onMyLocationChangeListener.onMyLocationChange((Location) zze.zzae(com_google_android_gms_dynamic_zzd));
            }
        });
    }

    public final void setOnPoiClickListener(final OnPoiClickListener onPoiClickListener) {
        if (onPoiClickListener == null) {
            try {
                this.anP.setOnPoiClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnPoiClickListener(new com.google.android.gms.maps.internal.zzy.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void zza(PointOfInterest pointOfInterest) throws RemoteException {
                onPoiClickListener.onPoiClick(pointOfInterest);
            }
        });
    }

    public final void setOnPolygonClickListener(final OnPolygonClickListener onPolygonClickListener) {
        if (onPolygonClickListener == null) {
            try {
                this.anP.setOnPolygonClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnPolygonClickListener(new com.google.android.gms.maps.internal.zzz.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void zza(zzg com_google_android_gms_maps_model_internal_zzg) {
                onPolygonClickListener.onPolygonClick(new Polygon(com_google_android_gms_maps_model_internal_zzg));
            }
        });
    }

    public final void setOnPolylineClickListener(final OnPolylineClickListener onPolylineClickListener) {
        if (onPolylineClickListener == null) {
            try {
                this.anP.setOnPolylineClickListener(null);
                return;
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            }
        }
        this.anP.setOnPolylineClickListener(new com.google.android.gms.maps.internal.zzaa.zza(this) {
            final /* synthetic */ GoogleMap anS;

            public void zza(IPolylineDelegate iPolylineDelegate) {
                onPolylineClickListener.onPolylineClick(new Polyline(iPolylineDelegate));
            }
        });
    }

    public final void setPadding(int i, int i2, int i3, int i4) {
        try {
            this.anP.setPadding(i, i2, i3, i4);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setTrafficEnabled(boolean z) {
        try {
            this.anP.setTrafficEnabled(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void snapshot(SnapshotReadyCallback snapshotReadyCallback) {
        snapshot(snapshotReadyCallback, null);
    }

    public final void snapshot(final SnapshotReadyCallback snapshotReadyCallback, Bitmap bitmap) {
        try {
            this.anP.snapshot(new com.google.android.gms.maps.internal.zzag.zza(this) {
                final /* synthetic */ GoogleMap anS;

                public void onSnapshotReady(Bitmap bitmap) throws RemoteException {
                    snapshotReadyCallback.onSnapshotReady(bitmap);
                }

                public void zzag(com.google.android.gms.dynamic.zzd com_google_android_gms_dynamic_zzd) throws RemoteException {
                    snapshotReadyCallback.onSnapshotReady((Bitmap) zze.zzae(com_google_android_gms_dynamic_zzd));
                }
            }, (zze) (bitmap != null ? zze.zzac(bitmap) : null));
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void stopAnimation() {
        try {
            this.anP.stopAnimation();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
