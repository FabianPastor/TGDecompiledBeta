package org.telegram.messenger;

import com.google.android.gms.maps.GoogleMap;

public final /* synthetic */ class GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda0 implements GoogleMap.OnCameraMoveListener {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda0(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void onCameraMove() {
        this.f$0.run();
    }
}
