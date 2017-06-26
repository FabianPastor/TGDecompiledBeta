package com.google.android.gms.maps.internal;

import android.location.Location;
import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.internal.IPolylineDelegate;
import com.google.android.gms.maps.model.internal.zzd;
import com.google.android.gms.maps.model.internal.zzg;
import com.google.android.gms.maps.model.internal.zzj;
import com.google.android.gms.maps.model.internal.zzp;
import com.google.android.gms.maps.model.internal.zzs;
import com.google.android.gms.maps.model.internal.zzw;

public interface IGoogleMapDelegate extends IInterface {
    zzd addCircle(CircleOptions circleOptions) throws RemoteException;

    zzg addGroundOverlay(GroundOverlayOptions groundOverlayOptions) throws RemoteException;

    zzp addMarker(MarkerOptions markerOptions) throws RemoteException;

    zzs addPolygon(PolygonOptions polygonOptions) throws RemoteException;

    IPolylineDelegate addPolyline(PolylineOptions polylineOptions) throws RemoteException;

    zzw addTileOverlay(TileOverlayOptions tileOverlayOptions) throws RemoteException;

    void animateCamera(IObjectWrapper iObjectWrapper) throws RemoteException;

    void animateCameraWithCallback(IObjectWrapper iObjectWrapper, zzc com_google_android_gms_maps_internal_zzc) throws RemoteException;

    void animateCameraWithDurationAndCallback(IObjectWrapper iObjectWrapper, int i, zzc com_google_android_gms_maps_internal_zzc) throws RemoteException;

    void clear() throws RemoteException;

    CameraPosition getCameraPosition() throws RemoteException;

    zzj getFocusedBuilding() throws RemoteException;

    void getMapAsync(zzap com_google_android_gms_maps_internal_zzap) throws RemoteException;

    int getMapType() throws RemoteException;

    float getMaxZoomLevel() throws RemoteException;

    float getMinZoomLevel() throws RemoteException;

    Location getMyLocation() throws RemoteException;

    IProjectionDelegate getProjection() throws RemoteException;

    IUiSettingsDelegate getUiSettings() throws RemoteException;

    boolean isBuildingsEnabled() throws RemoteException;

    boolean isIndoorEnabled() throws RemoteException;

    boolean isMyLocationEnabled() throws RemoteException;

    boolean isTrafficEnabled() throws RemoteException;

    void moveCamera(IObjectWrapper iObjectWrapper) throws RemoteException;

    void onCreate(Bundle bundle) throws RemoteException;

    void onDestroy() throws RemoteException;

    void onEnterAmbient(Bundle bundle) throws RemoteException;

    void onExitAmbient() throws RemoteException;

    void onLowMemory() throws RemoteException;

    void onPause() throws RemoteException;

    void onResume() throws RemoteException;

    void onSaveInstanceState(Bundle bundle) throws RemoteException;

    void onStart() throws RemoteException;

    void onStop() throws RemoteException;

    void resetMinMaxZoomPreference() throws RemoteException;

    void setBuildingsEnabled(boolean z) throws RemoteException;

    void setContentDescription(String str) throws RemoteException;

    boolean setIndoorEnabled(boolean z) throws RemoteException;

    void setInfoWindowAdapter(zzh com_google_android_gms_maps_internal_zzh) throws RemoteException;

    void setLatLngBoundsForCameraTarget(LatLngBounds latLngBounds) throws RemoteException;

    void setLocationSource(ILocationSourceDelegate iLocationSourceDelegate) throws RemoteException;

    boolean setMapStyle(MapStyleOptions mapStyleOptions) throws RemoteException;

    void setMapType(int i) throws RemoteException;

    void setMaxZoomPreference(float f) throws RemoteException;

    void setMinZoomPreference(float f) throws RemoteException;

    void setMyLocationEnabled(boolean z) throws RemoteException;

    void setOnCameraChangeListener(zzl com_google_android_gms_maps_internal_zzl) throws RemoteException;

    void setOnCameraIdleListener(zzn com_google_android_gms_maps_internal_zzn) throws RemoteException;

    void setOnCameraMoveCanceledListener(zzp com_google_android_gms_maps_internal_zzp) throws RemoteException;

    void setOnCameraMoveListener(zzr com_google_android_gms_maps_internal_zzr) throws RemoteException;

    void setOnCameraMoveStartedListener(zzt com_google_android_gms_maps_internal_zzt) throws RemoteException;

    void setOnCircleClickListener(zzv com_google_android_gms_maps_internal_zzv) throws RemoteException;

    void setOnGroundOverlayClickListener(zzx com_google_android_gms_maps_internal_zzx) throws RemoteException;

    void setOnIndoorStateChangeListener(zzz com_google_android_gms_maps_internal_zzz) throws RemoteException;

    void setOnInfoWindowClickListener(zzab com_google_android_gms_maps_internal_zzab) throws RemoteException;

    void setOnInfoWindowCloseListener(zzad com_google_android_gms_maps_internal_zzad) throws RemoteException;

    void setOnInfoWindowLongClickListener(zzaf com_google_android_gms_maps_internal_zzaf) throws RemoteException;

    void setOnMapClickListener(zzaj com_google_android_gms_maps_internal_zzaj) throws RemoteException;

    void setOnMapLoadedCallback(zzal com_google_android_gms_maps_internal_zzal) throws RemoteException;

    void setOnMapLongClickListener(zzan com_google_android_gms_maps_internal_zzan) throws RemoteException;

    void setOnMarkerClickListener(zzar com_google_android_gms_maps_internal_zzar) throws RemoteException;

    void setOnMarkerDragListener(zzat com_google_android_gms_maps_internal_zzat) throws RemoteException;

    void setOnMyLocationButtonClickListener(zzav com_google_android_gms_maps_internal_zzav) throws RemoteException;

    void setOnMyLocationChangeListener(zzax com_google_android_gms_maps_internal_zzax) throws RemoteException;

    void setOnPoiClickListener(zzaz com_google_android_gms_maps_internal_zzaz) throws RemoteException;

    void setOnPolygonClickListener(zzbb com_google_android_gms_maps_internal_zzbb) throws RemoteException;

    void setOnPolylineClickListener(zzbd com_google_android_gms_maps_internal_zzbd) throws RemoteException;

    void setPadding(int i, int i2, int i3, int i4) throws RemoteException;

    void setTrafficEnabled(boolean z) throws RemoteException;

    void setWatermarkEnabled(boolean z) throws RemoteException;

    void snapshot(zzbq com_google_android_gms_maps_internal_zzbq, IObjectWrapper iObjectWrapper) throws RemoteException;

    void snapshotForTest(zzbq com_google_android_gms_maps_internal_zzbq) throws RemoteException;

    void stopAnimation() throws RemoteException;

    boolean useViewLifecycleWhenInFragment() throws RemoteException;
}
