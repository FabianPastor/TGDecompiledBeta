package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.internal.zzm;
import com.google.android.gms.maps.model.CameraPosition;

final class zzs extends zzm {
    private /* synthetic */ OnCameraChangeListener zzblR;

    zzs(GoogleMap googleMap, OnCameraChangeListener onCameraChangeListener) {
        this.zzblR = onCameraChangeListener;
    }

    public final void onCameraChange(CameraPosition cameraPosition) {
        this.zzblR.onCameraChange(cameraPosition);
    }
}
