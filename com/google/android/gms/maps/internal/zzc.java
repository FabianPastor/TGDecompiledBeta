package com.google.android.gms.maps.internal;

import android.os.IInterface;
import android.os.RemoteException;

public interface zzc extends IInterface {
    void onCancel() throws RemoteException;

    void onFinish() throws RemoteException;
}
