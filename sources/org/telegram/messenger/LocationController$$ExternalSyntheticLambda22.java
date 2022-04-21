package org.telegram.messenger;

import android.location.Location;
import org.telegram.messenger.LocationController;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda22 implements Runnable {
    public final /* synthetic */ LocationController.LocationFetchCallback f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ Location f$3;

    public /* synthetic */ LocationController$$ExternalSyntheticLambda22(LocationController.LocationFetchCallback locationFetchCallback, String str, String str2, Location location) {
        this.f$0 = locationFetchCallback;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = location;
    }

    public final void run() {
        LocationController.lambda$fetchLocationAddress$30(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
