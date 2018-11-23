package org.telegram.p005ui;

import com.google.android.gms.maps.MapView;

/* renamed from: org.telegram.ui.LocationActivity$$Lambda$13 */
final /* synthetic */ class LocationActivity$$Lambda$13 implements Runnable {
    private final LocationActivity arg$1;
    private final MapView arg$2;

    LocationActivity$$Lambda$13(LocationActivity locationActivity, MapView mapView) {
        this.arg$1 = locationActivity;
        this.arg$2 = mapView;
    }

    public void run() {
        this.arg$1.lambda$null$4$LocationActivity(this.arg$2);
    }
}
