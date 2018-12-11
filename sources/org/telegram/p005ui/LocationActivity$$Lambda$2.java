package org.telegram.p005ui;

import com.google.android.gms.maps.MapView;

/* renamed from: org.telegram.ui.LocationActivity$$Lambda$2 */
final /* synthetic */ class LocationActivity$$Lambda$2 implements Runnable {
    private final LocationActivity arg$1;
    private final MapView arg$2;

    LocationActivity$$Lambda$2(LocationActivity locationActivity, MapView mapView) {
        this.arg$1 = locationActivity;
        this.arg$2 = mapView;
    }

    public void run() {
        this.arg$1.lambda$createView$5$LocationActivity(this.arg$2);
    }
}
