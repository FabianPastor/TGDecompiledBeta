package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.internal.zzp;

public interface IGoogleMapDelegate extends IInterface {
    zzp addMarker(MarkerOptions markerOptions) throws RemoteException;

    void animateCamera(IObjectWrapper iObjectWrapper) throws RemoteException;

    CameraPosition getCameraPosition() throws RemoteException;

    float getMaxZoomLevel() throws RemoteException;

    IUiSettingsDelegate getUiSettings() throws RemoteException;

    void moveCamera(IObjectWrapper iObjectWrapper) throws RemoteException;

    void setMapType(int i) throws RemoteException;

    void setMyLocationEnabled(boolean z) throws RemoteException;

    void setOnMyLocationChangeListener(zzax com_google_android_gms_maps_internal_zzax) throws RemoteException;

    void setPadding(int i, int i2, int i3, int i4) throws RemoteException;
}
