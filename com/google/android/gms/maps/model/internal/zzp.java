package com.google.android.gms.maps.model.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.maps.model.LatLng;

public interface zzp extends IInterface {
    LatLng getPosition() throws RemoteException;

    int hashCodeRemote() throws RemoteException;

    void setPosition(LatLng latLng) throws RemoteException;

    boolean zzj(zzp com_google_android_gms_maps_model_internal_zzp) throws RemoteException;
}
