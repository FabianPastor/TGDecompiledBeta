package com.google.android.gms.maps;

import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener;
import com.google.android.gms.maps.internal.zzu;

final class zzt extends zzu {
    private /* synthetic */ OnCameraMoveStartedListener zzblS;

    zzt(GoogleMap googleMap, OnCameraMoveStartedListener onCameraMoveStartedListener) {
        this.zzblS = onCameraMoveStartedListener;
    }

    public final void onCameraMoveStarted(int i) {
        this.zzblS.onCameraMoveStarted(i);
    }
}
