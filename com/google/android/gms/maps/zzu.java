package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener;
import com.google.android.gms.maps.internal.zzs;

final class zzu extends zzs {
    private /* synthetic */ OnCameraMoveListener zzblT;

    zzu(GoogleMap googleMap, OnCameraMoveListener onCameraMoveListener) {
        this.zzblT = onCameraMoveListener;
    }

    public final void onCameraMove() {
        this.zzblT.onCameraMove();
    }
}
