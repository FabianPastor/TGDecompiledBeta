package org.telegram.ui;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

final /* synthetic */ class LocationActivity$$Lambda$14 implements OnMapReadyCallback {
    private final LocationActivity arg$1;

    LocationActivity$$Lambda$14(LocationActivity locationActivity) {
        this.arg$1 = locationActivity;
    }

    public void onMapReady(GoogleMap googleMap) {
        this.arg$1.lambda$null$3$LocationActivity(googleMap);
    }
}
