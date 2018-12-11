package org.telegram.p005ui;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

/* renamed from: org.telegram.ui.LocationActivity$$Lambda$14 */
final /* synthetic */ class LocationActivity$$Lambda$14 implements OnMapReadyCallback {
    private final LocationActivity arg$1;

    LocationActivity$$Lambda$14(LocationActivity locationActivity) {
        this.arg$1 = locationActivity;
    }

    public void onMapReady(GoogleMap googleMap) {
        this.arg$1.lambda$null$3$LocationActivity(googleMap);
    }
}
