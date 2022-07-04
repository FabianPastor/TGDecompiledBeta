package org.telegram.messenger;

import android.location.Location;
import org.telegram.messenger.LocationController;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda11 implements Runnable {
    public final /* synthetic */ Location f$0;
    public final /* synthetic */ LocationController.LocationFetchCallback f$1;

    public /* synthetic */ LocationController$$ExternalSyntheticLambda11(Location location, LocationController.LocationFetchCallback locationFetchCallback) {
        this.f$0 = location;
        this.f$1 = locationFetchCallback;
    }

    public final void run() {
        LocationController.lambda$fetchLocationAddress$31(this.f$0, this.f$1);
    }
}
