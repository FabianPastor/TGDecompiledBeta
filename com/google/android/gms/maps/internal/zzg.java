package com.google.android.gms.maps.internal;

import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.zzed;
import com.google.android.gms.internal.zzef;
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
import com.google.android.gms.maps.model.internal.IPolylineDelegate.zza;
import com.google.android.gms.maps.model.internal.zzd;
import com.google.android.gms.maps.model.internal.zze;
import com.google.android.gms.maps.model.internal.zzh;
import com.google.android.gms.maps.model.internal.zzj;
import com.google.android.gms.maps.model.internal.zzk;
import com.google.android.gms.maps.model.internal.zzp;
import com.google.android.gms.maps.model.internal.zzq;
import com.google.android.gms.maps.model.internal.zzs;
import com.google.android.gms.maps.model.internal.zzt;
import com.google.android.gms.maps.model.internal.zzw;
import com.google.android.gms.maps.model.internal.zzx;

public final class zzg extends zzed implements IGoogleMapDelegate {
    zzg(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.maps.internal.IGoogleMapDelegate");
    }

    public final zzd addCircle(CircleOptions circleOptions) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) circleOptions);
        zzZ = zza(35, zzZ);
        zzd zzab = zze.zzab(zzZ.readStrongBinder());
        zzZ.recycle();
        return zzab;
    }

    public final com.google.android.gms.maps.model.internal.zzg addGroundOverlay(GroundOverlayOptions groundOverlayOptions) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) groundOverlayOptions);
        zzZ = zza(12, zzZ);
        com.google.android.gms.maps.model.internal.zzg zzac = zzh.zzac(zzZ.readStrongBinder());
        zzZ.recycle();
        return zzac;
    }

    public final zzp addMarker(MarkerOptions markerOptions) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) markerOptions);
        zzZ = zza(11, zzZ);
        zzp zzaf = zzq.zzaf(zzZ.readStrongBinder());
        zzZ.recycle();
        return zzaf;
    }

    public final zzs addPolygon(PolygonOptions polygonOptions) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) polygonOptions);
        zzZ = zza(10, zzZ);
        zzs zzag = zzt.zzag(zzZ.readStrongBinder());
        zzZ.recycle();
        return zzag;
    }

    public final IPolylineDelegate addPolyline(PolylineOptions polylineOptions) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) polylineOptions);
        zzZ = zza(9, zzZ);
        IPolylineDelegate zzah = zza.zzah(zzZ.readStrongBinder());
        zzZ.recycle();
        return zzah;
    }

    public final zzw addTileOverlay(TileOverlayOptions tileOverlayOptions) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) tileOverlayOptions);
        zzZ = zza(13, zzZ);
        zzw zzai = zzx.zzai(zzZ.readStrongBinder());
        zzZ.recycle();
        return zzai;
    }

    public final void animateCamera(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzb(5, zzZ);
    }

    public final void animateCameraWithCallback(IObjectWrapper iObjectWrapper, zzc com_google_android_gms_maps_internal_zzc) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzc);
        zzb(6, zzZ);
    }

    public final void animateCameraWithDurationAndCallback(IObjectWrapper iObjectWrapper, int i, zzc com_google_android_gms_maps_internal_zzc) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzZ.writeInt(i);
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzc);
        zzb(7, zzZ);
    }

    public final void clear() throws RemoteException {
        zzb(14, zzZ());
    }

    public final CameraPosition getCameraPosition() throws RemoteException {
        Parcel zza = zza(1, zzZ());
        CameraPosition cameraPosition = (CameraPosition) zzef.zza(zza, CameraPosition.CREATOR);
        zza.recycle();
        return cameraPosition;
    }

    public final zzj getFocusedBuilding() throws RemoteException {
        Parcel zza = zza(44, zzZ());
        zzj zzad = zzk.zzad(zza.readStrongBinder());
        zza.recycle();
        return zzad;
    }

    public final void getMapAsync(zzap com_google_android_gms_maps_internal_zzap) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzap);
        zzb(53, zzZ);
    }

    public final int getMapType() throws RemoteException {
        Parcel zza = zza(15, zzZ());
        int readInt = zza.readInt();
        zza.recycle();
        return readInt;
    }

    public final float getMaxZoomLevel() throws RemoteException {
        Parcel zza = zza(2, zzZ());
        float readFloat = zza.readFloat();
        zza.recycle();
        return readFloat;
    }

    public final float getMinZoomLevel() throws RemoteException {
        Parcel zza = zza(3, zzZ());
        float readFloat = zza.readFloat();
        zza.recycle();
        return readFloat;
    }

    public final Location getMyLocation() throws RemoteException {
        Parcel zza = zza(23, zzZ());
        Location location = (Location) zzef.zza(zza, Location.CREATOR);
        zza.recycle();
        return location;
    }

    public final IProjectionDelegate getProjection() throws RemoteException {
        IProjectionDelegate iProjectionDelegate;
        Parcel zza = zza(26, zzZ());
        IBinder readStrongBinder = zza.readStrongBinder();
        if (readStrongBinder == null) {
            iProjectionDelegate = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.maps.internal.IProjectionDelegate");
            iProjectionDelegate = queryLocalInterface instanceof IProjectionDelegate ? (IProjectionDelegate) queryLocalInterface : new zzbp(readStrongBinder);
        }
        zza.recycle();
        return iProjectionDelegate;
    }

    public final IUiSettingsDelegate getUiSettings() throws RemoteException {
        IUiSettingsDelegate iUiSettingsDelegate;
        Parcel zza = zza(25, zzZ());
        IBinder readStrongBinder = zza.readStrongBinder();
        if (readStrongBinder == null) {
            iUiSettingsDelegate = null;
        } else {
            IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("com.google.android.gms.maps.internal.IUiSettingsDelegate");
            iUiSettingsDelegate = queryLocalInterface instanceof IUiSettingsDelegate ? (IUiSettingsDelegate) queryLocalInterface : new zzbv(readStrongBinder);
        }
        zza.recycle();
        return iUiSettingsDelegate;
    }

    public final boolean isBuildingsEnabled() throws RemoteException {
        Parcel zza = zza(40, zzZ());
        boolean zza2 = zzef.zza(zza);
        zza.recycle();
        return zza2;
    }

    public final boolean isIndoorEnabled() throws RemoteException {
        Parcel zza = zza(19, zzZ());
        boolean zza2 = zzef.zza(zza);
        zza.recycle();
        return zza2;
    }

    public final boolean isMyLocationEnabled() throws RemoteException {
        Parcel zza = zza(21, zzZ());
        boolean zza2 = zzef.zza(zza);
        zza.recycle();
        return zza2;
    }

    public final boolean isTrafficEnabled() throws RemoteException {
        Parcel zza = zza(17, zzZ());
        boolean zza2 = zzef.zza(zza);
        zza.recycle();
        return zza2;
    }

    public final void moveCamera(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzb(4, zzZ);
    }

    public final void onCreate(Bundle bundle) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) bundle);
        zzb(54, zzZ);
    }

    public final void onDestroy() throws RemoteException {
        zzb(57, zzZ());
    }

    public final void onEnterAmbient(Bundle bundle) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) bundle);
        zzb(81, zzZ);
    }

    public final void onExitAmbient() throws RemoteException {
        zzb(82, zzZ());
    }

    public final void onLowMemory() throws RemoteException {
        zzb(58, zzZ());
    }

    public final void onPause() throws RemoteException {
        zzb(56, zzZ());
    }

    public final void onResume() throws RemoteException {
        zzb(55, zzZ());
    }

    public final void onSaveInstanceState(Bundle bundle) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) bundle);
        zzZ = zza(60, zzZ);
        if (zzZ.readInt() != 0) {
            bundle.readFromParcel(zzZ);
        }
        zzZ.recycle();
    }

    public final void onStart() throws RemoteException {
        zzb(101, zzZ());
    }

    public final void onStop() throws RemoteException {
        zzb(102, zzZ());
    }

    public final void resetMinMaxZoomPreference() throws RemoteException {
        zzb(94, zzZ());
    }

    public final void setBuildingsEnabled(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(41, zzZ);
    }

    public final void setContentDescription(String str) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeString(str);
        zzb(61, zzZ);
    }

    public final boolean setIndoorEnabled(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzZ = zza(20, zzZ);
        boolean zza = zzef.zza(zzZ);
        zzZ.recycle();
        return zza;
    }

    public final void setInfoWindowAdapter(zzh com_google_android_gms_maps_internal_zzh) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzh);
        zzb(33, zzZ);
    }

    public final void setLatLngBoundsForCameraTarget(LatLngBounds latLngBounds) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) latLngBounds);
        zzb(95, zzZ);
    }

    public final void setLocationSource(ILocationSourceDelegate iLocationSourceDelegate) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) iLocationSourceDelegate);
        zzb(24, zzZ);
    }

    public final boolean setMapStyle(MapStyleOptions mapStyleOptions) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (Parcelable) mapStyleOptions);
        zzZ = zza(91, zzZ);
        boolean zza = zzef.zza(zzZ);
        zzZ.recycle();
        return zza;
    }

    public final void setMapType(int i) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeInt(i);
        zzb(16, zzZ);
    }

    public final void setMaxZoomPreference(float f) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeFloat(f);
        zzb(93, zzZ);
    }

    public final void setMinZoomPreference(float f) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeFloat(f);
        zzb(92, zzZ);
    }

    public final void setMyLocationEnabled(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(22, zzZ);
    }

    public final void setOnCameraChangeListener(zzl com_google_android_gms_maps_internal_zzl) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzl);
        zzb(27, zzZ);
    }

    public final void setOnCameraIdleListener(zzn com_google_android_gms_maps_internal_zzn) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzn);
        zzb(99, zzZ);
    }

    public final void setOnCameraMoveCanceledListener(zzp com_google_android_gms_maps_internal_zzp) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzp);
        zzb(98, zzZ);
    }

    public final void setOnCameraMoveListener(zzr com_google_android_gms_maps_internal_zzr) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzr);
        zzb(97, zzZ);
    }

    public final void setOnCameraMoveStartedListener(zzt com_google_android_gms_maps_internal_zzt) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzt);
        zzb(96, zzZ);
    }

    public final void setOnCircleClickListener(zzv com_google_android_gms_maps_internal_zzv) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzv);
        zzb(89, zzZ);
    }

    public final void setOnGroundOverlayClickListener(zzx com_google_android_gms_maps_internal_zzx) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzx);
        zzb(83, zzZ);
    }

    public final void setOnIndoorStateChangeListener(zzz com_google_android_gms_maps_internal_zzz) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzz);
        zzb(45, zzZ);
    }

    public final void setOnInfoWindowClickListener(zzab com_google_android_gms_maps_internal_zzab) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzab);
        zzb(32, zzZ);
    }

    public final void setOnInfoWindowCloseListener(zzad com_google_android_gms_maps_internal_zzad) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzad);
        zzb(86, zzZ);
    }

    public final void setOnInfoWindowLongClickListener(zzaf com_google_android_gms_maps_internal_zzaf) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzaf);
        zzb(84, zzZ);
    }

    public final void setOnMapClickListener(zzaj com_google_android_gms_maps_internal_zzaj) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzaj);
        zzb(28, zzZ);
    }

    public final void setOnMapLoadedCallback(zzal com_google_android_gms_maps_internal_zzal) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzal);
        zzb(42, zzZ);
    }

    public final void setOnMapLongClickListener(zzan com_google_android_gms_maps_internal_zzan) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzan);
        zzb(29, zzZ);
    }

    public final void setOnMarkerClickListener(zzar com_google_android_gms_maps_internal_zzar) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzar);
        zzb(30, zzZ);
    }

    public final void setOnMarkerDragListener(zzat com_google_android_gms_maps_internal_zzat) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzat);
        zzb(31, zzZ);
    }

    public final void setOnMyLocationButtonClickListener(zzav com_google_android_gms_maps_internal_zzav) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzav);
        zzb(37, zzZ);
    }

    public final void setOnMyLocationChangeListener(zzax com_google_android_gms_maps_internal_zzax) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzax);
        zzb(36, zzZ);
    }

    public final void setOnPoiClickListener(zzaz com_google_android_gms_maps_internal_zzaz) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzaz);
        zzb(80, zzZ);
    }

    public final void setOnPolygonClickListener(zzbb com_google_android_gms_maps_internal_zzbb) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzbb);
        zzb(85, zzZ);
    }

    public final void setOnPolylineClickListener(zzbd com_google_android_gms_maps_internal_zzbd) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzbd);
        zzb(87, zzZ);
    }

    public final void setPadding(int i, int i2, int i3, int i4) throws RemoteException {
        Parcel zzZ = zzZ();
        zzZ.writeInt(i);
        zzZ.writeInt(i2);
        zzZ.writeInt(i3);
        zzZ.writeInt(i4);
        zzb(39, zzZ);
    }

    public final void setTrafficEnabled(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(18, zzZ);
    }

    public final void setWatermarkEnabled(boolean z) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, z);
        zzb(51, zzZ);
    }

    public final void snapshot(zzbq com_google_android_gms_maps_internal_zzbq, IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzbq);
        zzef.zza(zzZ, (IInterface) iObjectWrapper);
        zzb(38, zzZ);
    }

    public final void snapshotForTest(zzbq com_google_android_gms_maps_internal_zzbq) throws RemoteException {
        Parcel zzZ = zzZ();
        zzef.zza(zzZ, (IInterface) com_google_android_gms_maps_internal_zzbq);
        zzb(71, zzZ);
    }

    public final void stopAnimation() throws RemoteException {
        zzb(8, zzZ());
    }

    public final boolean useViewLifecycleWhenInFragment() throws RemoteException {
        Parcel zza = zza(59, zzZ());
        boolean zza2 = zzef.zza(zza);
        zza.recycle();
        return zza2;
    }
}
