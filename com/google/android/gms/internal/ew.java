package com.google.android.gms.internal;

import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.vision.barcode.Barcode;

public interface ew extends IInterface {
    void zzDO() throws RemoteException;

    Barcode[] zza(IObjectWrapper iObjectWrapper, fb fbVar) throws RemoteException;

    Barcode[] zzb(IObjectWrapper iObjectWrapper, fb fbVar) throws RemoteException;
}
