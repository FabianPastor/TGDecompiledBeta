package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.maps.model.LatLng;

public interface zzan extends IInterface {
    void onMapLongClick(LatLng latLng) throws RemoteException;
}
