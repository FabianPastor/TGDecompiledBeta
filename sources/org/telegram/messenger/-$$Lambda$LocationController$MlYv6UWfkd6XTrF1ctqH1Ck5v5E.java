package org.telegram.messenger;

import android.location.Location;
import org.telegram.messenger.LocationController.LocationFetchCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$MlYv6UWfkd6XTrF1ctqH1Ck5v5E implements Runnable {
    private final /* synthetic */ Location f$0;
    private final /* synthetic */ LocationFetchCallback f$1;

    public /* synthetic */ -$$Lambda$LocationController$MlYv6UWfkd6XTrF1ctqH1Ck5v5E(Location location, LocationFetchCallback locationFetchCallback) {
        this.f$0 = location;
        this.f$1 = locationFetchCallback;
    }

    public final void run() {
        LocationController.lambda$fetchLocationAddress$27(this.f$0, this.f$1);
    }
}
