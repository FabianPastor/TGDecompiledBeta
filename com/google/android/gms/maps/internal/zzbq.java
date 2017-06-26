package com.google.android.gms.maps.internal;

import android.graphics.Bitmap;
import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;

public interface zzbq extends IInterface {
    void onSnapshotReady(Bitmap bitmap) throws RemoteException;

    void zzG(IObjectWrapper iObjectWrapper) throws RemoteException;
}
