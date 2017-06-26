package com.google.android.gms.maps;

import android.graphics.Bitmap;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.internal.zzbr;

final class zzq extends zzbr {
    private /* synthetic */ SnapshotReadyCallback zzblP;

    zzq(GoogleMap googleMap, SnapshotReadyCallback snapshotReadyCallback) {
        this.zzblP = snapshotReadyCallback;
    }

    public final void onSnapshotReady(Bitmap bitmap) throws RemoteException {
        this.zzblP.onSnapshotReady(bitmap);
    }

    public final void zzG(IObjectWrapper iObjectWrapper) throws RemoteException {
        this.zzblP.onSnapshotReady((Bitmap) zzn.zzE(iObjectWrapper));
    }
}
