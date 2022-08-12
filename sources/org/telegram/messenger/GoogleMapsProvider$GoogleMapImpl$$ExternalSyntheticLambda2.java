package org.telegram.messenger;

import com.google.android.gms.maps.GoogleMap;

public final /* synthetic */ class GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda2 implements GoogleMap.OnMapLoadedCallback {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ GoogleMapsProvider$GoogleMapImpl$$ExternalSyntheticLambda2(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void onMapLoaded() {
        this.f$0.run();
    }
}
