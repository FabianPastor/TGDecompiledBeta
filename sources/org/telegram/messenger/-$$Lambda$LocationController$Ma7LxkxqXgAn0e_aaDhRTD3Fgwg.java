package org.telegram.messenger;

import android.location.Location;
import org.telegram.messenger.LocationController.LocationFetchCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationController$Ma7LxkxqXgAn0e_aaDhRTD3Fgwg implements Runnable {
    private final /* synthetic */ LocationFetchCallback f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ Location f$2;

    public /* synthetic */ -$$Lambda$LocationController$Ma7LxkxqXgAn0e_aaDhRTD3Fgwg(LocationFetchCallback locationFetchCallback, String str, Location location) {
        this.f$0 = locationFetchCallback;
        this.f$1 = str;
        this.f$2 = location;
    }

    public final void run() {
        LocationController.lambda$null$21(this.f$0, this.f$1, this.f$2);
    }
}
