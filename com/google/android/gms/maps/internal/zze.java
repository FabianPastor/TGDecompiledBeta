package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.internal.zza;

public interface zze extends IInterface {
    IMapViewDelegate zza(IObjectWrapper iObjectWrapper, GoogleMapOptions googleMapOptions) throws RemoteException;

    ICameraUpdateFactoryDelegate zzawc() throws RemoteException;

    zza zzawd() throws RemoteException;

    void zzi(IObjectWrapper iObjectWrapper, int i) throws RemoteException;
}
