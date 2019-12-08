package org.telegram.messenger;

import android.location.Location;
import org.telegram.messenger.LocationController.LocationFetchCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$IKRBEguhH-LGcCc_pJj8yfAZBN8 implements Runnable {
    private final /* synthetic */ Location f$0;
    private final /* synthetic */ LocationFetchCallback f$1;

    public /* synthetic */ -$$Lambda$LocationController$IKRBEguhH-LGcCc_pJj8yfAZBN8(Location location, LocationFetchCallback locationFetchCallback) {
        this.f$0 = location;
        this.f$1 = locationFetchCallback;
    }

    public final void run() {
        LocationController.lambda$fetchLocationAddress$26(this.f$0, this.f$1);
    }
}
