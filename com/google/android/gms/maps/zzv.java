package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnCameraMoveCanceledListener;
import com.google.android.gms.maps.internal.zzq;

final class zzv extends zzq {
    private /* synthetic */ OnCameraMoveCanceledListener zzblU;

    zzv(GoogleMap googleMap, OnCameraMoveCanceledListener onCameraMoveCanceledListener) {
        this.zzblU = onCameraMoveCanceledListener;
    }

    public final void onCameraMoveCanceled() {
        this.zzblU.onCameraMoveCanceled();
    }
}
