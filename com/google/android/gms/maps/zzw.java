package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.internal.zzo;

final class zzw extends zzo {
    private /* synthetic */ OnCameraIdleListener zzblV;

    zzw(GoogleMap googleMap, OnCameraIdleListener onCameraIdleListener) {
        this.zzblV = onCameraIdleListener;
    }

    public final void onCameraIdle() {
        this.zzblV.onCameraIdle();
    }
}
