package org.telegram.messenger;

import android.location.Location;
import org.telegram.messenger.LocationController.LocationFetchCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$XuNJ2NZGLma3Lbmqy9-FFBKvzBM implements Runnable {
    private final /* synthetic */ Location f$0;
    private final /* synthetic */ LocationFetchCallback f$1;

    public /* synthetic */ -$$Lambda$LocationController$XuNJ2NZGLma3Lbmqy9-FFBKvzBM(Location location, LocationFetchCallback locationFetchCallback) {
        this.f$0 = location;
        this.f$1 = locationFetchCallback;
    }

    public final void run() {
        LocationController.lambda$fetchLocationAddress$22(this.f$0, this.f$1);
    }
}
